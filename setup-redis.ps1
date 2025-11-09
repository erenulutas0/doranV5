# Redis Setup Script
# Bu script Redis'i Docker ile kurar ve durumunu kontrol eder

Write-Host "=== REDIS KURULUMU ===" -ForegroundColor Cyan
Write-Host ""

# 1. Redis Container Kontrolü
Write-Host "1. Redis Container Kontrol Ediliyor..." -ForegroundColor Yellow
$redisContainer = docker ps -a --filter "name=redis" --format "{{.Names}}"
if ($redisContainer -eq "redis") {
    $redisRunning = docker ps --filter "name=redis" --format "{{.Names}}"
    if ($redisRunning -eq "redis") {
        Write-Host "   ✓ Redis zaten çalışıyor" -ForegroundColor Green
        Write-Host ""
        Write-Host "Redis durumu:" -ForegroundColor Cyan
        docker ps --filter "name=redis" --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
        exit 0
    } else {
        Write-Host "   ⚠ Redis container var ama çalışmıyor, başlatılıyor..." -ForegroundColor Yellow
        docker start redis
        Start-Sleep -Seconds 2
    }
} else {
    Write-Host "   Redis container bulunamadı, oluşturuluyor..." -ForegroundColor Yellow
}

# 2. Redis Container Oluşturma
Write-Host "2. Redis Container Oluşturuluyor..." -ForegroundColor Yellow
try {
    docker run -d -p 6379:6379 --name redis redis:alpine
    Write-Host "   ✓ Redis container oluşturuldu" -ForegroundColor Green
    Start-Sleep -Seconds 3
} catch {
    Write-Host "   ✗ Redis container oluşturulamadı: $_" -ForegroundColor Red
    Write-Host ""
    Write-Host "   Manuel kurulum için:" -ForegroundColor Yellow
    Write-Host "     docker run -d -p 6379:6379 --name redis redis:alpine" -ForegroundColor White
    exit 1
}

# 3. Redis Durumu Kontrolü
Write-Host "3. Redis Durumu Kontrol Ediliyor..." -ForegroundColor Yellow
Start-Sleep -Seconds 2
$redisRunning = docker ps --filter "name=redis" --format "{{.Names}}"
if ($redisRunning -eq "redis") {
    Write-Host "   ✓ Redis başarıyla çalışıyor" -ForegroundColor Green
    docker ps --filter "name=redis" --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
} else {
    Write-Host "   ✗ Redis başlatılamadı" -ForegroundColor Red
    Write-Host "   Logları kontrol edin: docker logs redis" -ForegroundColor Yellow
    exit 1
}
Write-Host ""

# 4. Redis Port Kontrolü
Write-Host "4. Redis Port Kontrolü (6379)..." -ForegroundColor Yellow
$portOpen = Test-NetConnection -ComputerName localhost -Port 6379 -InformationLevel Quiet -WarningAction SilentlyContinue
if ($portOpen) {
    Write-Host "   ✓ Port 6379 açık" -ForegroundColor Green
} else {
    Write-Host "   ✗ Port 6379 kapalı" -ForegroundColor Red
}
Write-Host ""

# 5. Redis Connection Test (Opsiyonel)
Write-Host "5. Redis Connection Test..." -ForegroundColor Yellow
try {
    # Redis CLI ile basit bir test (eğer redis-cli yüklüyse)
    $redisTest = docker exec redis redis-cli ping 2>&1
    if ($redisTest -like "*PONG*") {
        Write-Host "   ✓ Redis connection başarılı (PONG)" -ForegroundColor Green
    } else {
        Write-Host "   ⚠ Redis CLI test edilemedi (redis-cli yüklü olmayabilir)" -ForegroundColor Yellow
    }
} catch {
    Write-Host "   ⚠ Redis CLI test edilemedi" -ForegroundColor Yellow
}
Write-Host ""

Write-Host "=== REDIS KURULUMU TAMAMLANDI ===" -ForegroundColor Cyan
Write-Host ""
Write-Host "Sonraki Adımlar:" -ForegroundColor Yellow
Write-Host "  1. API Gateway'i yeniden başlatın (yeni yapılandırma için)" -ForegroundColor White
Write-Host "  2. application.yaml'da Redis yapılandırmasını aktif edin" -ForegroundColor White
Write-Host "  3. Rate limiting filter'larını aktif edin" -ForegroundColor White
Write-Host ""
Write-Host "Redis Management:" -ForegroundColor Cyan
Write-Host "  - Redis'i durdur: docker stop redis" -ForegroundColor White
Write-Host "  - Redis'i başlat: docker start redis" -ForegroundColor White
Write-Host "  - Redis'i sil: docker rm -f redis" -ForegroundColor White
Write-Host "  - Redis logları: docker logs redis" -ForegroundColor White
Write-Host ""

