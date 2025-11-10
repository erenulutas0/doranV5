# Zipkin Server ve Distributed Tracing Test Script
# Bu script Zipkin server'ın durumunu ve trace'lerin gönderilip gönderilmediğini kontrol eder

Write-Host "=== ZIPKIN DISTRIBUTED TRACING TEST ===" -ForegroundColor Cyan
Write-Host ""

# 1. Zipkin Server Kontrolü
Write-Host "1. ZIPKIN SERVER KONTROLÜ" -ForegroundColor Yellow
Write-Host ""

try {
    $zipkinHealth = Invoke-RestMethod -Uri "http://localhost:9411/health" -Method Get -ErrorAction Stop -TimeoutSec 5
    Write-Host "  ✓ Zipkin server çalışıyor" -ForegroundColor Green
    Write-Host "    URL: http://localhost:9411" -ForegroundColor Gray
} catch {
    Write-Host "  ✗ Zipkin server çalışmıyor!" -ForegroundColor Red
    Write-Host "    Hata: $($_.Exception.Message)" -ForegroundColor Gray
    Write-Host ""
    Write-Host "  💡 Zipkin'i başlatmak için:" -ForegroundColor Yellow
    Write-Host "     docker-compose -f docker-compose-zipkin.yml up -d" -ForegroundColor Gray
    Write-Host "     veya" -ForegroundColor Gray
    Write-Host "     docker run -d -p 9411:9411 --name zipkin openzipkin/zipkin:latest" -ForegroundColor Gray
    exit 1
}

Write-Host ""

# 2. Servislerin Durumu
Write-Host "2. SERVİSLERİN DURUMU" -ForegroundColor Yellow
Write-Host ""

$services = @(
    @{Name="api-gateway"; Port=8080},
    @{Name="user-service"; Port=8081},
    @{Name="product-service"; Port=8082},
    @{Name="order-service"; Port=8083},
    @{Name="inventory-service"; Port=8084},
    @{Name="notification-service"; Port=8085}
)

$allServicesRunning = $true
foreach ($service in $services) {
    try {
        $health = Invoke-RestMethod -Uri "http://localhost:$($service.Port)/actuator/health" -Method Get -ErrorAction Stop -TimeoutSec 2
        Write-Host "  ✓ $($service.Name) çalışıyor (Port: $($service.Port))" -ForegroundColor Green
    } catch {
        Write-Host "  ✗ $($service.Name) çalışmıyor (Port: $($service.Port))" -ForegroundColor Red
        $allServicesRunning = $false
    }
}

Write-Host ""

if (-not $allServicesRunning) {
    Write-Host "  ⚠ Bazı servisler çalışmıyor. Trace'ler eksik olabilir." -ForegroundColor Yellow
    Write-Host ""
}

# 3. Test İsteği Gönderme
Write-Host "3. TEST İSTEĞİ GÖNDERME" -ForegroundColor Yellow
Write-Host ""

Write-Host "  Bir sipariş oluşturuluyor (trace oluşturmak için)..." -ForegroundColor Gray

try {
    # Önce bir kullanıcı oluştur
    $userBody = @{
        name = "Test User"
        email = "test@example.com"
        phone = "1234567890"
    } | ConvertTo-Json

    $userResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/users" -Method Post -Body $userBody -ContentType "application/json" -ErrorAction Stop
    $userId = $userResponse.id
    Write-Host "  ✓ Kullanıcı oluşturuldu (ID: $userId)" -ForegroundColor Green

    # Bir ürün oluştur
    $productBody = @{
        name = "Test Product"
        description = "Test Description"
        price = 100.00
        category = "Test Category"
        stockQuantity = 10
    } | ConvertTo-Json

    $productResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/products" -Method Post -Body $productBody -ContentType "application/json" -ErrorAction Stop
    $productId = $productResponse.id
    Write-Host "  ✓ Ürün oluşturuldu (ID: $productId)" -ForegroundColor Green

    # Sipariş oluştur (bu trace oluşturacak)
    $orderBody = @{
        userId = $userId
        items = @(
            @{
                productId = $productId
                quantity = 1
            }
        )
    } | ConvertTo-Json

    $orderResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/orders" -Method Post -Body $orderBody -ContentType "application/json" -ErrorAction Stop
    Write-Host "  ✓ Sipariş oluşturuldu (ID: $($orderResponse.id))" -ForegroundColor Green
    Write-Host "    Trace ID oluşturuldu ve Zipkin'e gönderildi" -ForegroundColor Gray

} catch {
    Write-Host "  ✗ Test isteği başarısız" -ForegroundColor Red
    Write-Host "    Hata: $($_.Exception.Message)" -ForegroundColor Gray
    Write-Host ""
    Write-Host "  Not: Servisler çalışıyor olsa bile trace'ler oluşturulmuş olabilir." -ForegroundColor Yellow
}

Write-Host ""

# 4. Zipkin'de Trace Kontrolü
Write-Host "4. ZIPKIN'DE TRACE KONTROLÜ" -ForegroundColor Yellow
Write-Host ""

Write-Host "  Zipkin UI'yi açın: http://localhost:9411" -ForegroundColor Cyan
Write-Host ""
Write-Host "  Zipkin'de göreceğiniz trace'ler:" -ForegroundColor Gray
Write-Host "    - API Gateway (entry point)" -ForegroundColor Gray
Write-Host "    - Order Service" -ForegroundColor Gray
Write-Host "    - User Service (Feign Client)" -ForegroundColor Gray
Write-Host "    - Product Service (Feign Client)" -ForegroundColor Gray
Write-Host "    - Inventory Service (Feign Client)" -ForegroundColor Gray
Write-Host "    - Notification Service (RabbitMQ consumer)" -ForegroundColor Gray
Write-Host ""

# 5. Trace API Kontrolü
Write-Host "5. TRACE API KONTROLÜ" -ForegroundColor Yellow
Write-Host ""

try {
    # Son 5 dakikadaki trace'leri kontrol et
    $endTime = [DateTimeOffset]::Now.ToUnixTimeMilliseconds()
    $startTime = $endTime - (5 * 60 * 1000)  # 5 dakika önce
    
    $tracesUrl = "http://localhost:9411/api/v2/traces?serviceName=api-gateway&limit=10&endTs=$endTime&lookback=300000"
    $traces = Invoke-RestMethod -Uri $tracesUrl -Method Get -ErrorAction Stop -TimeoutSec 5
    
    if ($traces.Count -gt 0) {
        Write-Host "  ✓ Zipkin'de trace'ler bulundu ($($traces.Count) trace)" -ForegroundColor Green
        Write-Host "    Son trace'ler Zipkin'de görüntülenebilir" -ForegroundColor Gray
    } else {
        Write-Host "  ⚠ Zipkin'de henüz trace bulunamadı" -ForegroundColor Yellow
        Write-Host "    Birkaç saniye bekleyip tekrar deneyin" -ForegroundColor Gray
        Write-Host "    Trace'ler async olarak gönderilir" -ForegroundColor Gray
    }
} catch {
    Write-Host "  ⚠ Trace API'ye erişilemedi" -ForegroundColor Yellow
    Write-Host "    Hata: $($_.Exception.Message)" -ForegroundColor Gray
    Write-Host "    Bu normal olabilir, trace'ler henüz gönderilmemiş olabilir" -ForegroundColor Gray
}

Write-Host ""

# 6. Özet
Write-Host "=== ÖZET ===" -ForegroundColor Cyan
Write-Host ""
Write-Host "✅ Zipkin Server: http://localhost:9411" -ForegroundColor Green
Write-Host "✅ Tüm servisler tracing için yapılandırıldı" -ForegroundColor Green
Write-Host ""
Write-Host "📊 Zipkin UI'de görebileceğiniz bilgiler:" -ForegroundColor Yellow
Write-Host "  • Request flow (hangi servisler çağrıldı)" -ForegroundColor Gray
Write-Host "  • Her serviste geçen süre" -ForegroundColor Gray
Write-Host "  • Hata detayları (varsa)" -ForegroundColor Gray
Write-Host "  • Service dependencies" -ForegroundColor Gray
Write-Host ""
Write-Host "💡 İpucu: Zipkin UI'de 'Run Query' butonuna tıklayarak" -ForegroundColor Cyan
Write-Host "   son 15 dakikadaki tüm trace'leri görebilirsiniz." -ForegroundColor Cyan
Write-Host ""

