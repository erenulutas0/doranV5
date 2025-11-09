# Full Flow Test Script
# User -> Product -> Inventory -> Order -> RabbitMQ -> Notification

Write-Host "=== FULL FLOW TEST ===" -ForegroundColor Cyan
Write-Host ""

# 1. Mevcut User'ı Kontrol Et
Write-Host "1. Mevcut User'ı Kontrol Ediyorum..." -ForegroundColor Yellow
try {
    $userResponse = Invoke-RestMethod -Uri "http://localhost:8081/users" -Method Get
    $user = $userResponse | Where-Object { $_.email -eq "test@example.com" }
    if ($user) {
        $userId = $user.id
        Write-Host "   ✓ User bulundu: $($user.username) (ID: $userId)" -ForegroundColor Green
    } else {
        Write-Host "   ✗ User bulunamadı!" -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "   ✗ User Service'e erişilemiyor: $_" -ForegroundColor Red
    exit 1
}
Write-Host ""

# 2. Product Oluştur
Write-Host "2. Product Oluşturuluyor..." -ForegroundColor Yellow
$productData = @{
    name = "Test Product"
    description = "Test Product Description"
    price = 99.99
    category = "Electronics"
    stockQuantity = 100
} | ConvertTo-Json

try {
    $product = Invoke-RestMethod -Uri "http://localhost:8082/products" -Method Post -Body $productData -ContentType "application/json"
    $productId = $product.id
    Write-Host "   ✓ Product oluşturuldu: $($product.name) (ID: $productId, Price: $($product.price))" -ForegroundColor Green
} catch {
    Write-Host "   ✗ Product oluşturulamadı: $_" -ForegroundColor Red
    if ($_.ErrorDetails.Message) {
        Write-Host "   Hata detayı: $($_.ErrorDetails.Message)" -ForegroundColor Red
    }
    exit 1
}
Write-Host ""

# 3. Inventory'de Stok Ekle
Write-Host "3. Inventory'de Stok Ekleniyor..." -ForegroundColor Yellow
$inventoryData = @{
    productId = $productId
    quantity = 100
    reservedQuantity = 0
    minStockLevel = 10
    maxStockLevel = 500
    location = "BESIKTAS"
    status = "IN_STOCK"
} | ConvertTo-Json

try {
    $inventory = Invoke-RestMethod -Uri "http://localhost:8084/inventory" -Method Post -Body $inventoryData -ContentType "application/json"
    Write-Host "   ✓ Stok eklendi: Quantity: $($inventory.quantity), Min Stock: $($inventory.minStockLevel)" -ForegroundColor Green
} catch {
    Write-Host "   ✗ Stok eklenemedi: $_" -ForegroundColor Red
    if ($_.ErrorDetails.Message) {
        Write-Host "   Hata detayı: $($_.ErrorDetails.Message)" -ForegroundColor Red
    }
    exit 1
}
Write-Host ""

# 4. Order Oluştur
Write-Host "4. Order Oluşturuluyor..." -ForegroundColor Yellow
$orderData = @{
    userId = $userId
    shippingAddress = "Test Address 123, Beşiktaş"
    city = "Istanbul"
    zipCode = "34000"
    phoneNumber = "5551234567"
    orderItems = @(
        @{
            productId = $productId
            quantity = 2
        }
    )
} | ConvertTo-Json -Depth 10

try {
    $order = Invoke-RestMethod -Uri "http://localhost:8083/orders" -Method Post -Body $orderData -ContentType "application/json"
    $orderId = $order.id
    Write-Host "   ✓ Order oluşturuldu: Order ID: $orderId, Total: $($order.totalAmount)" -ForegroundColor Green
    Write-Host "   Status: $($order.status)" -ForegroundColor White
} catch {
    Write-Host "   ✗ Order oluşturulamadı: $_" -ForegroundColor Red
    if ($_.ErrorDetails.Message) {
        Write-Host "   Hata detayı: $($_.ErrorDetails.Message)" -ForegroundColor Red
    }
    exit 1
}
Write-Host ""

# 5. RabbitMQ'da Mesajları Kontrol Et
Write-Host "5. RabbitMQ'da Mesajları Kontrol Ediyorum..." -ForegroundColor Yellow
Start-Sleep -Seconds 2  # Mesajların işlenmesi için bekle

try {
    $authHeader = "Basic " + [Convert]::ToBase64String([Text.Encoding]::ASCII.GetBytes("guest:guest"))
    $queues = Invoke-RestMethod -Uri "http://localhost:15672/api/queues" -Method Get -Headers @{Authorization = $authHeader}
    
    $orderCreatedQueue = $queues | Where-Object { $_.name -eq "order.created" }
    $orderStatusQueue = $queues | Where-Object { $_.name -eq "order.status.changed" }
    
    if ($orderCreatedQueue) {
        Write-Host "   ✓ order.created queue bulundu" -ForegroundColor Green
        Write-Host "     Messages: $($orderCreatedQueue.messages)" -ForegroundColor White
        Write-Host "     Consumers: $($orderCreatedQueue.consumers)" -ForegroundColor White
    } else {
        Write-Host "   ⚠ order.created queue bulunamadı" -ForegroundColor Yellow
    }
    
    if ($orderStatusQueue) {
        Write-Host "   ✓ order.status.changed queue bulundu" -ForegroundColor Green
        Write-Host "     Messages: $($orderStatusQueue.messages)" -ForegroundColor White
        Write-Host "     Consumers: $($orderStatusQueue.consumers)" -ForegroundColor White
    } else {
        Write-Host "   ⚠ order.status.changed queue bulunamadı" -ForegroundColor Yellow
    }
} catch {
    Write-Host "   ⚠ RabbitMQ Management API'ye erişilemiyor: $_" -ForegroundColor Yellow
    Write-Host "   (RabbitMQ çalışmıyor olabilir veya Management plugin aktif değil)" -ForegroundColor Yellow
}
Write-Host ""

# 6. Notification Service'de Bildirimleri Kontrol Et
Write-Host "6. Notification Service'de Bildirimleri Kontrol Ediyorum..." -ForegroundColor Yellow
Start-Sleep -Seconds 2  # Bildirimlerin oluşturulması için bekle

try {
    $notifications = Invoke-RestMethod -Uri "http://localhost:8085/notifications" -Method Get
    $userNotifications = $notifications | Where-Object { $_.userId -eq $userId }
    
    if ($userNotifications) {
        Write-Host "   ✓ Bildirimler bulundu: $($userNotifications.Count) adet" -ForegroundColor Green
        foreach ($notification in $userNotifications) {
            Write-Host "     - $($notification.subject) (Status: $($notification.status))" -ForegroundColor White
        }
    } else {
        Write-Host "   ⚠ Henüz bildirim oluşturulmamış" -ForegroundColor Yellow
    }
} catch {
    Write-Host "   ⚠ Notification Service'e erişilemiyor: $_" -ForegroundColor Yellow
}
Write-Host ""

# Özet
Write-Host "=== TEST TAMAMLANDI ===" -ForegroundColor Cyan
Write-Host ""
Write-Host "Oluşturulan Kayıtlar:" -ForegroundColor Yellow
Write-Host "  - User ID: $userId" -ForegroundColor White
Write-Host "  - Product ID: $productId" -ForegroundColor White
Write-Host "  - Order ID: $orderId" -ForegroundColor White
Write-Host ""
Write-Host "Sonraki Adımlar:" -ForegroundColor Yellow
Write-Host "  1. RabbitMQ Management UI: http://localhost:15672" -ForegroundColor Cyan
Write-Host "  2. Notification Service Loglarını kontrol edin" -ForegroundColor Cyan
Write-Host "  3. Order durumunu güncelleyin ve bildirimleri kontrol edin" -ForegroundColor Cyan
Write-Host ""

