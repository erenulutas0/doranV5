# Flutter Başlatma Script'i
Write-Host "`n🚀 Flutter'ı başlatıyorum...`n" -ForegroundColor Green

# Flutter dizinine git
Set-Location -Path "$PSScriptRoot\flutter-app"

# Eski process'leri temizle
Write-Host "Eski process'leri temizliyorum..." -ForegroundColor Yellow
Get-Process -Name "dart","flutter" -ErrorAction SilentlyContinue | Stop-Process -Force -ErrorAction SilentlyContinue
Start-Sleep -Seconds 2

# Port 8088'ı kontrol et (Product Service ile çakışmayı önlemek için 8082 yerine 8088)
$flutterPort = 8088
$portCheck = Get-NetTCPConnection -LocalPort $flutterPort -ErrorAction SilentlyContinue
if ($portCheck) {
    Write-Host "Port $flutterPort kullanılıyor, temizleniyor..." -ForegroundColor Yellow
    $portCheck | ForEach-Object { Stop-Process -Id $_.OwningProcess -Force -ErrorAction SilentlyContinue }
    Start-Sleep -Seconds 2
}

# Flutter'ı başlat
Write-Host "`n✅ Flutter başlatılıyor...`n" -ForegroundColor Green
Write-Host "⚠️  NOT: Bu terminal penceresini AÇIK TUTUN!" -ForegroundColor Yellow
Write-Host "⚠️  Compile işlemi 2-3 dakika sürebilir`n" -ForegroundColor Yellow
Write-Host "URL: http://localhost:$flutterPort`n" -ForegroundColor Cyan

flutter run -d chrome --web-port=$flutterPort --web-hostname=localhost




