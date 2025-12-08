# Docker Desktop Yeniden Başlatma Script'i
Write-Host "`n🔧 Docker Desktop'ı yeniden başlatıyorum...`n" -ForegroundColor Yellow

# 1. Docker process'lerini durdur
Write-Host "1️⃣  Docker process'lerini durduruyorum..." -ForegroundColor Cyan
Get-Process -Name "*docker*" -ErrorAction SilentlyContinue | Stop-Process -Force -ErrorAction SilentlyContinue
Start-Sleep -Seconds 3
Write-Host "   ✅ Docker process'leri durduruldu" -ForegroundColor Green

# 2. Docker Desktop'ı kapat
Write-Host "`n2️⃣  Docker Desktop'ı kapatıyorum..." -ForegroundColor Cyan
$dockerDesktop = Get-Process -Name "Docker Desktop" -ErrorAction SilentlyContinue
if ($dockerDesktop) {
    $dockerDesktop | Stop-Process -Force -ErrorAction SilentlyContinue
    Write-Host "   ✅ Docker Desktop kapatıldı" -ForegroundColor Green
} else {
    Write-Host "   ℹ️  Docker Desktop zaten kapalı" -ForegroundColor Yellow
}

# 3. Bekle
Write-Host "`n3️⃣  10 saniye bekliyorum..." -ForegroundColor Cyan
Start-Sleep -Seconds 10

# 4. Docker Desktop'ı başlat
Write-Host "`n4️⃣  Docker Desktop'ı başlatıyorum..." -ForegroundColor Cyan
$dockerDesktopPath = "$env:ProgramFiles\Docker\Docker\Docker Desktop.exe"
if (Test-Path $dockerDesktopPath) {
    Start-Process $dockerDesktopPath
    Write-Host "   ✅ Docker Desktop başlatıldı" -ForegroundColor Green
} else {
    Write-Host "   ⚠️  Docker Desktop bulunamadı, manuel olarak başlatın" -ForegroundColor Yellow
}

Write-Host "`n⏳ Docker Desktop'ın tamamen başlamasını bekleyin (30-60 saniye)..." -ForegroundColor Yellow
Write-Host "`n✅ Tamamlandı! Docker Desktop başladıktan sonra şu komutu çalıştırın:" -ForegroundColor Green
Write-Host "   docker-compose up -d" -ForegroundColor Cyan




