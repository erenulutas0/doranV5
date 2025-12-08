# Flutter Başlatma Script'i
Write-Host "`n🚀 Flutter'ı başlatıyorum...`n" -ForegroundColor Green

# Flutter dizinine git
Set-Location -Path "$PSScriptRoot\flutter-app"

# Eski process'leri temizle
Write-Host "Eski process'leri temizliyorum..." -ForegroundColor Yellow
Get-Process -Name "dart","flutter" -ErrorAction SilentlyContinue | Stop-Process -Force -ErrorAction SilentlyContinue
Start-Sleep -Seconds 2

# Port 8082'yi kontrol et
$port8082 = Get-NetTCPConnection -LocalPort 8082 -ErrorAction SilentlyContinue
if ($port8082) {
    Write-Host "Port 8082 kullanılıyor, temizleniyor..." -ForegroundColor Yellow
    $port8082 | ForEach-Object { Stop-Process -Id $_.OwningProcess -Force -ErrorAction SilentlyContinue }
    Start-Sleep -Seconds 2
}

# Flutter'ı başlat
Write-Host "`n✅ Flutter başlatılıyor...`n" -ForegroundColor Green
Write-Host "⚠️  NOT: Bu terminal penceresini AÇIK TUTUN!" -ForegroundColor Yellow
Write-Host "⚠️  Compile işlemi 2-3 dakika sürebilir`n" -ForegroundColor Yellow
Write-Host "URL: http://localhost:8082`n" -ForegroundColor Cyan

flutter run -d chrome --web-port=8082 --web-hostname=localhost




