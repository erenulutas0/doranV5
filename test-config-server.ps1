# Config Server ve Tüm Servislerin Durumunu Test Et

Write-Host "=== CONFIG SERVER VE SERVİSLER DURUM KONTROLÜ ===" -ForegroundColor Cyan
Write-Host ""

# 1. Config Server Durumu
Write-Host "1. Config Server Durumu Kontrol Ediliyor..." -ForegroundColor Yellow
try {
    $configHealth = Invoke-RestMethod -Uri "http://localhost:8888/actuator/health" -Method Get -ErrorAction Stop
    if ($configHealth.status -eq "UP") {
        Write-Host "   ✓ Config Server çalışıyor (Port: 8888)" -ForegroundColor Green
    } else {
        Write-Host "   ✗ Config Server durumu: $($configHealth.status)" -ForegroundColor Red
    }
} catch {
    Write-Host "   ✗ Config Server'a erişilemedi!" -ForegroundColor Red
    Write-Host "     Hata: $_" -ForegroundColor Red
}
Write-Host ""

# 2. Config Server'dan Yapılandırma Testi
Write-Host "2. Config Server'dan Yapılandırma Testi..." -ForegroundColor Yellow
$services = @("user-service", "product-service", "order-service", "inventory-service", "notification-service")
$configWorking = $true

foreach ($service in $services) {
    try {
        $config = Invoke-RestMethod -Uri "http://localhost:8888/$service/default" -Method Get -ErrorAction Stop
        Write-Host "   ✓ $service yapılandırması alındı" -ForegroundColor Green
    } catch {
        Write-Host "   ✗ $service yapılandırması alınamadı" -ForegroundColor Red
        $configWorking = $false
    }
}
Write-Host ""

# 3. Eureka Durumu
Write-Host "3. Eureka Service Registry Kontrol Ediliyor..." -ForegroundColor Yellow
try {
    $eureka = Invoke-RestMethod -Uri "http://localhost:8761/eureka/apps" -Method Get -ErrorAction Stop -Headers @{"Accept" = "application/json"}
    if ($eureka.applications.application) {
        $services = $eureka.applications.application | ForEach-Object { $_.name }
        $serviceCount = $services.Count
        Write-Host "   ✓ Eureka çalışıyor - $serviceCount servis kayıtlı" -ForegroundColor Green
        Write-Host "     Servisler: $($services -join ', '))" -ForegroundColor White
        
        # Config Server kayıtlı mı kontrol et
        if ($services -contains "config-server") {
            Write-Host "     ✓ Config Server Eureka'ya kayıtlı" -ForegroundColor Green
        } else {
            Write-Host "     ⚠ Config Server Eureka'ya kayıtlı değil" -ForegroundColor Yellow
        }
    }
} catch {
    Write-Host "   ⚠ Eureka'ya erişilemedi" -ForegroundColor Yellow
}
Write-Host ""

# 4. Tüm Servislerin Durumu
Write-Host "4. Tüm Servislerin Durumu Kontrol Ediliyor..." -ForegroundColor Yellow
$servicePorts = @{
    "user-service" = 8081
    "product-service" = 8082
    "order-service" = 8083
    "inventory-service" = 8084
    "notification-service" = 8085
    "api-gateway" = 8080
}

$allServicesUp = $true
foreach ($service in $servicePorts.Keys) {
    $port = $servicePorts[$service]
    try {
        $health = Invoke-RestMethod -Uri "http://localhost:$port/actuator/health" -Method Get -ErrorAction Stop -TimeoutSec 2
        if ($health.status -eq "UP") {
            Write-Host "   ✓ $service çalışıyor (Port: $port)" -ForegroundColor Green
        } else {
            Write-Host "   ⚠ $service durumu: $($health.status) (Port: $port)" -ForegroundColor Yellow
            $allServicesUp = $false
        }
    } catch {
        Write-Host "   ✗ $service çalışmıyor (Port: $port)" -ForegroundColor Red
        $allServicesUp = $false
    }
}
Write-Host ""

# 5. Config Server Endpoint Testi
Write-Host "5. Config Server Endpoint Testi..." -ForegroundColor Yellow
try {
    $userConfig = Invoke-RestMethod -Uri "http://localhost:8888/user-service/default" -Method Get -ErrorAction Stop
    Write-Host "   ✓ User Service yapılandırması alındı" -ForegroundColor Green
    if ($userConfig.propertySources) {
        Write-Host "     Property sources: $($userConfig.propertySources.Count) adet" -ForegroundColor Gray
    }
} catch {
    Write-Host "   ✗ Config Server endpoint testi başarısız" -ForegroundColor Red
}
Write-Host ""

# Özet
Write-Host "=== ÖZET ===" -ForegroundColor Cyan
Write-Host ""

if ($configWorking -and $allServicesUp) {
    Write-Host "✅ TÜM SİSTEM ÇALIŞIYOR!" -ForegroundColor Green
    Write-Host ""
    Write-Host "✓ Config Server: Çalışıyor" -ForegroundColor Green
    Write-Host "✓ Tüm servisler: Çalışıyor" -ForegroundColor Green
    Write-Host "✓ Config Server'dan yapılandırma: Başarılı" -ForegroundColor Green
    Write-Host ""
    Write-Host "Config Server Endpoint'leri:" -ForegroundColor Yellow
    Write-Host "  • Health: http://localhost:8888/actuator/health" -ForegroundColor White
    Write-Host "  • User Service Config: http://localhost:8888/user-service/default" -ForegroundColor White
    Write-Host "  • Product Service Config: http://localhost:8888/product-service/default" -ForegroundColor White
    Write-Host "  • Order Service Config: http://localhost:8888/order-service/default" -ForegroundColor White
    Write-Host "  • Inventory Service Config: http://localhost:8888/inventory-service/default" -ForegroundColor White
    Write-Host "  • Notification Service Config: http://localhost:8888/notification-service/default" -ForegroundColor White
} else {
    Write-Host "⚠ BAZI SERVİSLER ÇALIŞMIYOR" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Lütfen çalışmayan servisleri başlatın:" -ForegroundColor White
    Write-Host "  cd [service-name]" -ForegroundColor Gray
    Write-Host "  mvn spring-boot:run" -ForegroundColor Gray
}
Write-Host ""

