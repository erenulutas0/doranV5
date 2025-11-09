# Order Creation Test Script
# Bu script bir siparis olusturup RabbitMQ uzerinden mesaj akisini test eder

Write-Host "=== Order Creation Test ===" -ForegroundColor Cyan
Write-Host ""

# 1. Test User Olustur
Write-Host "1. Test User Olusturuluyor..." -ForegroundColor Yellow
$userBody = @{
    username = "testuser"
    email = "test@example.com"
    password = "Test123!@#"
    firstName = "Test"
    lastName = "User"
    phone = "5551234567"
    address = "Test Address, Test Street 123"
    city = "Istanbul"
    state = "IST"
    zip = "34000"
} | ConvertTo-Json

try {
    $userResponse = Invoke-RestMethod -Uri "http://localhost:8081/users" -Method Post -Body $userBody -ContentType "application/json"
    $userId = $userResponse.id
    Write-Host "   OK User olusturuldu: $userId" -ForegroundColor Green
} catch {
    Write-Host "   ERROR User olusturulamadi: $_" -ForegroundColor Red
    Write-Host "   Mevcut user kullanilacak..." -ForegroundColor Yellow
    # Mevcut user'ları al
    try {
        $users = Invoke-RestMethod -Uri "http://localhost:8081/users" -Method Get
        if ($users.Count -gt 0) {
            $userId = $users[0].id
            Write-Host "   OK Mevcut user kullaniliyor: $userId" -ForegroundColor Green
        } else {
            Write-Host "   ERROR User bulunamadi!" -ForegroundColor Red
            exit 1
        }
    } catch {
        Write-Host "   ERROR User listesi alinamadi: $_" -ForegroundColor Red
        exit 1
    }
}
Write-Host ""

# 2. Test Product Olustur
Write-Host "2. Test Product Olusturuluyor..." -ForegroundColor Yellow
$productBody = @{
    name = "Test Product"
    description = "Test Product Description"
    price = 100.00
    category = "Test Category"
} | ConvertTo-Json

try {
    $productResponse = Invoke-RestMethod -Uri "http://localhost:8082/products" -Method Post -Body $productBody -ContentType "application/json"
    $productId = $productResponse.id
    Write-Host "   OK Product olusturuldu: $productId" -ForegroundColor Green
} catch {
    Write-Host "   ERROR Product olusturulamadi: $_" -ForegroundColor Red
    Write-Host "   Mevcut product kullanilacak..." -ForegroundColor Yellow
    try {
        $products = Invoke-RestMethod -Uri "http://localhost:8082/products" -Method Get
        if ($products.Count -gt 0) {
            $productId = $products[0].id
            Write-Host "   OK Mevcut product kullaniliyor: $productId" -ForegroundColor Green
        } else {
            Write-Host "   ERROR Product bulunamadi!" -ForegroundColor Red
            exit 1
        }
    } catch {
        Write-Host "   ERROR Product listesi alinamadi: $_" -ForegroundColor Red
        exit 1
    }
}
Write-Host ""

# 3. Inventory Olustur/Guncelle
Write-Host "3. Inventory Olusturuluyor..." -ForegroundColor Yellow
$inventoryBody = @{
    productId = $productId
    quantity = 100
    minStockLevel = 10
} | ConvertTo-Json

try {
    $inventoryResponse = Invoke-RestMethod -Uri "http://localhost:8084/inventory" -Method Post -Body $inventoryBody -ContentType "application/json"
    Write-Host "   OK Inventory olusturuldu/guncellendi" -ForegroundColor Green
} catch {
    Write-Host "   WARNING Inventory olusturulamadi (zaten var olabilir): $_" -ForegroundColor Yellow
}
Write-Host ""

# 4. Order Olustur
Write-Host "4. Order Olusturuluyor..." -ForegroundColor Yellow
$orderBody = @{
    userId = $userId
    shippingAddress = "Test Address, Test Street 123"
    city = "Istanbul"
    zipCode = "34000"
    phoneNumber = "5551234567"
    orderItems = @(
        @{
            productId = $productId
            quantity = 2
        }
    )
} | ConvertTo-Json -Depth 3

try {
    $orderResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/orders" -Method Post -Body $orderBody -ContentType "application/json"
    $orderId = $orderResponse.id
    Write-Host "   OK Order olusturuldu: $orderId" -ForegroundColor Green
    Write-Host "   Order Status: $($orderResponse.status)" -ForegroundColor Cyan
    Write-Host "   Total Amount: $($orderResponse.totalAmount)" -ForegroundColor Cyan
} catch {
    Write-Host "   ERROR Order olusturulamadi: $_" -ForegroundColor Red
    if ($_.Exception.Response) {
        $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
        $responseBody = $reader.ReadToEnd()
        Write-Host "   Response: $responseBody" -ForegroundColor Red
    }
    exit 1
}
Write-Host ""

# 5. RabbitMQ Queue Kontrolu
Write-Host "5. RabbitMQ Queue Kontrolu..." -ForegroundColor Yellow
Start-Sleep -Seconds 2
try {
    $queues = Invoke-RestMethod -Uri "http://localhost:15672/api/queues" -Headers @{Authorization = "Basic " + [Convert]::ToBase64String([Text.Encoding]::ASCII.GetBytes("guest:guest"))}
    
    $orderCreatedQueue = $queues | Where-Object { $_.name -eq "order.created" }
    $orderStatusQueue = $queues | Where-Object { $_.name -eq "order.status.changed" }
    
    if ($orderCreatedQueue) {
        Write-Host "   OK order.created queue:" -ForegroundColor Green
        Write-Host "      Messages: $($orderCreatedQueue.messages)" -ForegroundColor Cyan
        Write-Host "      Ready: $($orderCreatedQueue.messages_ready)" -ForegroundColor Cyan
        Write-Host "      Unacked: $($orderCreatedQueue.messages_unacknowledged)" -ForegroundColor Cyan
    }
    
    if ($orderStatusQueue) {
        Write-Host "   OK order.status.changed queue:" -ForegroundColor Green
        Write-Host "      Messages: $($orderStatusQueue.messages)" -ForegroundColor Cyan
    }
} catch {
    Write-Host "   ERROR Queue kontrolu yapilamadi: $_" -ForegroundColor Red
}
Write-Host ""

# 6. Notification Kontrolu
Write-Host "6. Notification Kontrolu..." -ForegroundColor Yellow
Start-Sleep -Seconds 2
try {
    $notifications = Invoke-RestMethod -Uri "http://localhost:8085/notifications" -Method Get
    $orderNotifications = $notifications | Where-Object { $_.relatedEntityId -eq $orderId }
    
    if ($orderNotifications) {
        Write-Host "   OK Bildirimler olusturuldu:" -ForegroundColor Green
        foreach ($notif in $orderNotifications) {
            Write-Host "      - $($notif.subject) (Status: $($notif.status))" -ForegroundColor Cyan
        }
    } else {
        Write-Host "   WARNING Henuz bildirim olusturulmamis (biraz bekleyin)" -ForegroundColor Yellow
    }
} catch {
    Write-Host "   ERROR Notification kontrolu yapilamadi: $_" -ForegroundColor Red
}
Write-Host ""

Write-Host "=== Test Tamamlandi ===" -ForegroundColor Cyan
Write-Host ""
Write-Host "Order ID: $orderId" -ForegroundColor Cyan
Write-Host "RabbitMQ Management: http://localhost:15672" -ForegroundColor Cyan
Write-Host ""

