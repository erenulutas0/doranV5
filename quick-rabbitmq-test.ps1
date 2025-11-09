# Quick RabbitMQ Test - Mesajların gelip gelmediğini test eder
Write-Host "=== Hizli RabbitMQ Test ===" -ForegroundColor Cyan
Write-Host ""

# 1. RabbitMQ Queue Durumu (Başlangıç)
Write-Host "1. RabbitMQ Queue Durumu (Baslangic):" -ForegroundColor Yellow
try {
    $authHeader = "Basic " + [Convert]::ToBase64String([Text.Encoding]::ASCII.GetBytes("guest:guest"))
    $queues = Invoke-RestMethod -Uri "http://localhost:15672/api/queues" -Headers @{Authorization = $authHeader}
    
    $orderCreatedQueue = $queues | Where-Object { $_.name -eq "order.created" }
    $orderStatusQueue = $queues | Where-Object { $_.name -eq "order.status.changed" }
    
    Write-Host "   order.created: $($orderCreatedQueue.messages) mesaj" -ForegroundColor Cyan
    Write-Host "   order.status.changed: $($orderStatusQueue.messages) mesaj" -ForegroundColor Cyan
} catch {
    Write-Host "   ERROR: $_" -ForegroundColor Red
}
Write-Host ""

# 2. Test Verileri Olustur
Write-Host "2. Test Verileri Olusturuluyor..." -ForegroundColor Yellow

# User olustur
$userBody = @{
    username = "testuser$(Get-Random -Maximum 9999)"
    email = "test$(Get-Random -Maximum 9999)@example.com"
    password = "Test123!@#"
    firstName = "Test"
    lastName = "User"
    phone = "5551234567"
    address = "Test Address 123"
    city = "Istanbul"
    state = "IST"
    zip = "34000"
} | ConvertTo-Json

$userId = $null
try {
    $user = Invoke-RestMethod -Uri "http://localhost:8081/users" -Method Post -Body $userBody -ContentType "application/json"
    $userId = $user.id
    Write-Host "   OK User: $userId" -ForegroundColor Green
} catch {
    Write-Host "   WARNING User olusturulamadi, UUID kullanilacak" -ForegroundColor Yellow
    $userId = [guid]::NewGuid().ToString()
    Write-Host "   Test User ID: $userId" -ForegroundColor Cyan
}

# Product olustur
$productBody = @{
    name = "Test Product $(Get-Random -Maximum 9999)"
    description = "Test Description"
    price = 100.00
    category = "Test"
} | ConvertTo-Json

$productId = $null
try {
    $product = Invoke-RestMethod -Uri "http://localhost:8082/products" -Method Post -Body $productBody -ContentType "application/json"
    $productId = $product.id
    Write-Host "   OK Product: $productId" -ForegroundColor Green
} catch {
    Write-Host "   WARNING Product olusturulamadi, UUID kullanilacak" -ForegroundColor Yellow
    $productId = [guid]::NewGuid().ToString()
    Write-Host "   Test Product ID: $productId" -ForegroundColor Cyan
}

# Inventory olustur
if ($productId) {
    $inventoryBody = @{
        productId = $productId
        quantity = 100
        minStockLevel = 10
    } | ConvertTo-Json
    
    try {
        $inventory = Invoke-RestMethod -Uri "http://localhost:8084/inventory" -Method Post -Body $inventoryBody -ContentType "application/json"
        Write-Host "   OK Inventory olusturuldu" -ForegroundColor Green
    } catch {
        Write-Host "   WARNING Inventory olusturulamadi (zaten var olabilir)" -ForegroundColor Yellow
    }
}
Write-Host ""

# 3. Order Olustur
Write-Host "3. Order Olusturuluyor..." -ForegroundColor Yellow
if ($userId -and $productId) {
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
        $order = Invoke-RestMethod -Uri "http://localhost:8083/orders" -Method Post -Body $orderBody -ContentType "application/json"
        $orderId = $order.id
        Write-Host "   OK Order olusturuldu!" -ForegroundColor Green
        Write-Host "      Order ID: $orderId" -ForegroundColor Cyan
        Write-Host "      Status: $($order.status)" -ForegroundColor Cyan
        Write-Host "      Total: $($order.totalAmount)" -ForegroundColor Cyan
        
        # 4. RabbitMQ Queue Kontrolu (Order sonrasi)
        Write-Host ""
        Write-Host "4. RabbitMQ Queue Kontrolu (3 saniye bekleniyor...)" -ForegroundColor Yellow
        Start-Sleep -Seconds 3
        
        $queuesAfter = Invoke-RestMethod -Uri "http://localhost:15672/api/queues" -Headers @{Authorization = $authHeader}
        $orderCreatedAfter = $queuesAfter | Where-Object { $_.name -eq "order.created" }
        $orderStatusAfter = $queuesAfter | Where-Object { $_.name -eq "order.status.changed" }
        
        Write-Host "   order.created: $($orderCreatedAfter.messages) mesaj" -ForegroundColor $(if ($orderCreatedAfter.messages -gt 0) { "Yellow" } else { "Green" })
        Write-Host "   order.status.changed: $($orderStatusAfter.messages) mesaj" -ForegroundColor $(if ($orderStatusAfter.messages -gt 0) { "Yellow" } else { "Green" })
        
        if ($orderCreatedAfter.messages -gt 0) {
            Write-Host "   INFO: Mesajlar queue'da bekliyor (Notification Service isliyor olabilir)" -ForegroundColor Yellow
        } else {
            Write-Host "   OK: Mesajlar islendi (Notification Service calisiyor!)" -ForegroundColor Green
        }
        
        # 5. Notification Kontrolu
        Write-Host ""
        Write-Host "5. Notification Kontrolu (2 saniye bekleniyor...)" -ForegroundColor Yellow
        Start-Sleep -Seconds 2
        
        try {
            $notifications = Invoke-RestMethod -Uri "http://localhost:8085/notifications" -Method Get
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
        
    } catch {
        Write-Host "   ERROR Order olusturulamadi: $_" -ForegroundColor Red
        if ($_.Exception.Response) {
            $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
            $responseBody = $reader.ReadToEnd()
            Write-Host "   Response: $responseBody" -ForegroundColor Red
        }
    }
} else {
    Write-Host "   ERROR: User veya Product olusturulamadi, test devam edemiyor" -ForegroundColor Red
}
Write-Host ""

Write-Host "=== Test Tamamlandi ===" -ForegroundColor Cyan
Write-Host ""
Write-Host "RabbitMQ Management: http://localhost:15672" -ForegroundColor Cyan
Write-Host "Queue'lari kontrol edebilirsiniz!" -ForegroundColor Cyan
Write-Host ""

