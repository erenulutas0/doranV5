# Quick Test Script - RabbitMQ Flow Test
Write-Host "=== Quick RabbitMQ Test ===" -ForegroundColor Cyan
Write-Host ""

# 1. Mevcut User'ları kontrol et
Write-Host "1. Mevcut User'lar:" -ForegroundColor Yellow
try {
    $users = Invoke-RestMethod -Uri "http://localhost:8080/api/users" -Method Get
    if ($users.Count -gt 0) {
        $userId = $users[0].id
        Write-Host "   OK User bulundu: $userId ($($users[0].email))" -ForegroundColor Green
    } else {
        Write-Host "   WARNING User bulunamadi, olusturuluyor..." -ForegroundColor Yellow
        # Farklı email ile dene
        $newUser = @{
            username = "testuser$(Get-Random)"
            email = "test$(Get-Random)@example.com"
            password = "Test123!@#"
            firstName = "Test"
            lastName = "User"
            phone = "5551234567"
            address = "Test Address 123"
            city = "Istanbul"
            state = "IST"
            zip = "34000"
        } | ConvertTo-Json
        $createdUser = Invoke-RestMethod -Uri "http://localhost:8081/users" -Method Post -Body $newUser -ContentType "application/json"
        $userId = $createdUser.id
        Write-Host "   OK User olusturuldu: $userId" -ForegroundColor Green
    }
} catch {
    Write-Host "   ERROR: $_" -ForegroundColor Red
    exit 1
}
Write-Host ""

# 2. Mevcut Product'ları kontrol et
Write-Host "2. Mevcut Product'lar:" -ForegroundColor Yellow
try {
    $products = Invoke-RestMethod -Uri "http://localhost:8080/api/products" -Method Get
    if ($products.Count -gt 0) {
        $productId = $products[0].id
        Write-Host "   OK Product bulundu: $productId ($($products[0].name))" -ForegroundColor Green
    } else {
        Write-Host "   WARNING Product bulunamadi, olusturuluyor..." -ForegroundColor Yellow
        $newProduct = @{
            name = "Test Product"
            description = "Test Description"
            price = 100.00
            category = "Test"
        } | ConvertTo-Json
        $createdProduct = Invoke-RestMethod -Uri "http://localhost:8082/products" -Method Post -Body $newProduct -ContentType "application/json"
        $productId = $createdProduct.id
        Write-Host "   OK Product olusturuldu: $productId" -ForegroundColor Green
    }
} catch {
    Write-Host "   ERROR: $_" -ForegroundColor Red
    exit 1
}
Write-Host ""

# 3. Inventory kontrolü
Write-Host "3. Inventory kontrolu:" -ForegroundColor Yellow
try {
    $inventory = Invoke-RestMethod -Uri "http://localhost:8084/inventory/product/$productId" -Method Get
    Write-Host "   OK Inventory mevcut: Quantity=$($inventory.quantity)" -ForegroundColor Green
} catch {
    Write-Host "   WARNING Inventory bulunamadi, olusturuluyor..." -ForegroundColor Yellow
    $newInventory = @{
        productId = $productId
        quantity = 100
        minStockLevel = 10
    } | ConvertTo-Json
    try {
        $createdInventory = Invoke-RestMethod -Uri "http://localhost:8084/inventory" -Method Post -Body $newInventory -ContentType "application/json"
        Write-Host "   OK Inventory olusturuldu" -ForegroundColor Green
    } catch {
        Write-Host "   WARNING Inventory olusturulamadi (zaten var olabilir)" -ForegroundColor Yellow
    }
}
Write-Host ""

# 4. Order Oluştur
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
    $order = Invoke-RestMethod -Uri "http://localhost:8080/api/orders" -Method Post -Body $orderBody -ContentType "application/json"
    $orderId = $order.id
    Write-Host "   OK Order olusturuldu!" -ForegroundColor Green
    Write-Host "      Order ID: $orderId" -ForegroundColor Cyan
    Write-Host "      Status: $($order.status)" -ForegroundColor Cyan
    Write-Host "      Total: $($order.totalAmount)" -ForegroundColor Cyan
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

# 5. RabbitMQ Queue Kontrolü
Write-Host "5. RabbitMQ Queue Kontrolu (3 saniye bekleniyor...)" -ForegroundColor Yellow
Start-Sleep -Seconds 3

try {
    $authHeader = "Basic " + [Convert]::ToBase64String([Text.Encoding]::ASCII.GetBytes("guest:guest"))
    $queues = Invoke-RestMethod -Uri "http://localhost:15672/api/queues" -Headers @{Authorization = $authHeader}
    
    $orderCreatedQueue = $queues | Where-Object { $_.name -eq "order.created" }
    $orderStatusQueue = $queues | Where-Object { $_.name -eq "order.status.changed" }
    
    Write-Host "   order.created queue:" -ForegroundColor Cyan
    if ($orderCreatedQueue) {
        Write-Host "      Messages: $($orderCreatedQueue.messages)" -ForegroundColor Green
        Write-Host "      Ready: $($orderCreatedQueue.messages_ready)" -ForegroundColor Green
        Write-Host "      Unacked: $($orderCreatedQueue.messages_unacknowledged)" -ForegroundColor Green
    } else {
        Write-Host "      Queue bulunamadi" -ForegroundColor Red
    }
    
    Write-Host "   order.status.changed queue:" -ForegroundColor Cyan
    if ($orderStatusQueue) {
        Write-Host "      Messages: $($orderStatusQueue.messages)" -ForegroundColor Green
    } else {
        Write-Host "      Queue bulunamadi" -ForegroundColor Red
    }
} catch {
    Write-Host "   ERROR Queue kontrolu yapilamadi: $_" -ForegroundColor Red
}
Write-Host ""

# 6. Notification Kontrolü
Write-Host "6. Notification Kontrolu (2 saniye bekleniyor...)" -ForegroundColor Yellow
Start-Sleep -Seconds 2

try {
    $notifications = Invoke-RestMethod -Uri "http://localhost:8080/api/notifications" -Method Get
    $orderNotifications = $notifications | Where-Object { $_.relatedEntityId -eq $orderId }
    
    if ($orderNotifications -and $orderNotifications.Count -gt 0) {
        Write-Host "   OK Bildirimler olusturuldu:" -ForegroundColor Green
        foreach ($notif in $orderNotifications) {
            Write-Host "      - $($notif.subject)" -ForegroundColor Cyan
            Write-Host "        Status: $($notif.status), Type: $($notif.type)" -ForegroundColor Cyan
        }
    } else {
        Write-Host "   WARNING Henuz bildirim olusturulmamis" -ForegroundColor Yellow
        Write-Host "   Toplam bildirim sayisi: $($notifications.Count)" -ForegroundColor Yellow
    }
} catch {
    Write-Host "   ERROR Notification kontrolu yapilamadi: $_" -ForegroundColor Red
}
Write-Host ""

Write-Host "=== Test Tamamlandi ===" -ForegroundColor Cyan
Write-Host ""
Write-Host "Order ID: $orderId" -ForegroundColor Cyan
Write-Host "RabbitMQ Management: http://localhost:15672" -ForegroundColor Cyan
Write-Host "Queue'larda mesajlari kontrol edebilirsiniz!" -ForegroundColor Cyan
Write-Host ""

