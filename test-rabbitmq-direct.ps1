# RabbitMQ Direkt Test - Management API ile mesaj gönderir
Write-Host "=== RabbitMQ Direkt Test ===" -ForegroundColor Cyan
Write-Host ""

# Test mesajı oluştur
$orderId = [guid]::NewGuid().ToString()
$userId = [guid]::NewGuid().ToString()

$testMessage = @{
    orderId = $orderId
    userId = $userId
    userEmail = "test@example.com"
    userName = "Test User"
    totalAmount = 200.00
    shippingAddress = "Test Address, Test Street 123"
    city = "Istanbul"
    zipCode = "34000"
    phoneNumber = "5551234567"
    orderDate = (Get-Date).ToString("yyyy-MM-ddTHH:mm:ss")
    orderItems = @(
        @{
            productId = [guid]::NewGuid().ToString()
            productName = "Test Product"
            quantity = 2
            price = 100.00
            subtotal = 200.00
        }
    )
} | ConvertTo-Json -Depth 3

Write-Host "1. Test Mesaji Olusturuldu" -ForegroundColor Green
Write-Host "   Order ID: $orderId" -ForegroundColor Cyan
Write-Host ""

# RabbitMQ Management API ile mesaj gönder
Write-Host "2. RabbitMQ'ya Mesaj Gonderiliyor..." -ForegroundColor Yellow

$authHeader = "Basic " + [Convert]::ToBase64String([Text.Encoding]::ASCII.GetBytes("guest:guest"))

# Exchange'e mesaj gönder (default exchange kullanarak)
$publishBody = @{
    properties = @{}
    routing_key = "order.created"
    payload = $testMessage
    payload_encoding = "string"
} | ConvertTo-Json -Depth 3

try {
    # Management API ile mesaj göndermek için exchange'e publish ediyoruz
    # Ancak direkt queue'ya mesaj göndermek için farklı bir endpoint kullanmamız gerekiyor
    # Bu yüzden önce queue'ları kontrol edip, sonra manuel test önerisi sunacağız
    
    Write-Host "   NOT: Management API ile direkt mesaj gonderimi icin exchange yapilandirmasi gerekir" -ForegroundColor Yellow
    Write-Host "   Alternatif: Servisler uzerinden test yapin veya Management UI'dan manuel gonderin" -ForegroundColor Yellow
    Write-Host ""
    
    # Queue durumunu kontrol et
    Write-Host "3. Queue Durumu Kontrolu:" -ForegroundColor Yellow
    $queues = Invoke-RestMethod -Uri "http://localhost:15672/api/queues" -Headers @{Authorization = $authHeader}
    $orderCreatedQueue = $queues | Where-Object { $_.name -eq "order.created" }
    
    Write-Host "   order.created queue:" -ForegroundColor Cyan
    Write-Host "      Messages: $($orderCreatedQueue.messages)" -ForegroundColor $(if ($orderCreatedQueue.messages -gt 0) { "Green" } else { "Gray" })
    Write-Host "      Consumers: $($orderCreatedQueue.consumers)" -ForegroundColor $(if ($orderCreatedQueue.consumers -gt 0) { "Green" } else { "Yellow" })
    Write-Host "      State: $($orderCreatedQueue.state)" -ForegroundColor Green
    
    if ($orderCreatedQueue.consumers -eq 0) {
        Write-Host ""
        Write-Host "   WARNING: Notification Service queue'yu dinlemiyor!" -ForegroundColor Yellow
        Write-Host "   Notification Service'in calistigindan emin olun" -ForegroundColor Yellow
    } else {
        Write-Host ""
        Write-Host "   OK: Notification Service queue'yu dinliyor ($($orderCreatedQueue.consumers) consumer)" -ForegroundColor Green
    }
    
} catch {
    Write-Host "   ERROR: $_" -ForegroundColor Red
}
Write-Host ""

Write-Host "=== Test Mesaji (Manuel Test Icin) ===" -ForegroundColor Cyan
Write-Host ""
Write-Host "RabbitMQ Management UI'da manuel test yapmak icin:" -ForegroundColor Yellow
Write-Host "1. http://localhost:15672 -> Queues -> order.created" -ForegroundColor White
Write-Host "2. 'Publish message' sekmesine gidin" -ForegroundColor White
Write-Host "3. Asagidaki mesaji yapistirin:" -ForegroundColor White
Write-Host ""
Write-Host $testMessage -ForegroundColor Gray
Write-Host ""
Write-Host "4. 'Publish message' butonuna basin" -ForegroundColor White
Write-Host "5. Notification Service'de bildirim olusturuldugunu kontrol edin" -ForegroundColor White
Write-Host ""

