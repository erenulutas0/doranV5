# User Service Restart Script
# Bu script User Service'i temizleyip yeniden baslatir

Write-Host "=== User Service Restart ===" -ForegroundColor Cyan
Write-Host ""

# 1. Target klasorunu sil
Write-Host "1. Target klasoru temizleniyor..." -ForegroundColor Yellow
if (Test-Path "target") {
    Remove-Item -Recurse -Force target
    Write-Host "   ✓ Target klasoru silindi" -ForegroundColor Green
} else {
    Write-Host "   ⚠ Target klasoru bulunamadi" -ForegroundColor Yellow
}
Write-Host ""

# 2. Yeniden derle
Write-Host "2. Proje derleniyor..." -ForegroundColor Yellow
mvn clean compile
if ($LASTEXITCODE -eq 0) {
    Write-Host "   ✓ Derleme basarili" -ForegroundColor Green
} else {
    Write-Host "   ✗ Derleme basarisiz!" -ForegroundColor Red
    exit 1
}
Write-Host ""

# 3. Servisi baslat
Write-Host "3. User Service baslatiliyor..." -ForegroundColor Yellow
Write-Host "   (Ctrl+C ile durdurabilirsiniz)" -ForegroundColor Gray
Write-Host ""
mvn spring-boot:run

