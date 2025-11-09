# PostgreSQL Bağlantı Test Script'i
# Her servisin PostgreSQL'e bağlanıp bağlanamadığını test eder

Write-Host "=== POSTGRESQL BAĞLANTI TESTİ ===" -ForegroundColor Cyan
Write-Host ""

# PostgreSQL bağlantı bilgileri
$pgHost = "localhost"
$pgPort = "5432"
$pgUser = "postgres"
$pgPassword = "postgres"

# Test edilecek veritabanları
$databases = @(
    @{Name="user_db"; Service="user-service"},
    @{Name="product_db"; Service="product-service"},
    @{Name="order_db"; Service="order-service"},
    @{Name="inventory_db"; Service="inventory-service"},
    @{Name="notification_db"; Service="notification-service"}
)

Write-Host "PostgreSQL Bağlantı Bilgileri:" -ForegroundColor Yellow
Write-Host "  Host: $pgHost" -ForegroundColor White
Write-Host "  Port: $pgPort" -ForegroundColor White
Write-Host "  User: $pgUser" -ForegroundColor White
Write-Host ""

# PostgreSQL'in çalışıp çalışmadığını kontrol et
Write-Host "1. PostgreSQL Server Durumu Kontrol Ediliyor..." -ForegroundColor Yellow
try {
    $env:PGPASSWORD = $pgPassword
    $result = psql -U $pgUser -h $pgHost -p $pgPort -d postgres -c "SELECT version();" 2>&1
    if ($LASTEXITCODE -eq 0) {
        Write-Host "   ✓ PostgreSQL çalışıyor" -ForegroundColor Green
        $version = ($result | Select-String "PostgreSQL").ToString()
        Write-Host "   $version" -ForegroundColor Gray
    } else {
        Write-Host "   ✗ PostgreSQL'e bağlanılamadı!" -ForegroundColor Red
        Write-Host "   Hata: $result" -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "   ✗ PostgreSQL kontrolü başarısız!" -ForegroundColor Red
    Write-Host "   Hata: $_" -ForegroundColor Red
    exit 1
}
Write-Host ""

# Her veritabanını test et
Write-Host "2. Veritabanları Kontrol Ediliyor..." -ForegroundColor Yellow
$allSuccess = $true

foreach ($db in $databases) {
    Write-Host "   Test ediliyor: $($db.Name) ($($db.Service))..." -ForegroundColor White
    try {
        $env:PGPASSWORD = $pgPassword
        $result = psql -U $pgUser -h $pgHost -p $pgPort -d $db.Name -c "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'public';" 2>&1
        
        if ($LASTEXITCODE -eq 0) {
            $tableCount = ($result | Select-String "\d+").ToString().Trim()
            Write-Host "     ✓ Bağlantı başarılı - Tablo sayısı: $tableCount" -ForegroundColor Green
        } else {
            Write-Host "     ✗ Bağlantı hatası!" -ForegroundColor Red
            Write-Host "     Hata: $result" -ForegroundColor Red
            $allSuccess = $false
        }
    } catch {
        Write-Host "     ✗ Test başarısız!" -ForegroundColor Red
        Write-Host "     Hata: $_" -ForegroundColor Red
        $allSuccess = $false
    }
}
Write-Host ""

# Sonuç
if ($allSuccess) {
    Write-Host "=== TÜM BAĞLANTILAR BAŞARILI ===" -ForegroundColor Green
    Write-Host ""
    Write-Host "Sonraki adımlar:" -ForegroundColor Yellow
    Write-Host "  1. Servisleri başlatın" -ForegroundColor White
    Write-Host "  2. Servis loglarında PostgreSQL bağlantı mesajlarını kontrol edin" -ForegroundColor White
    Write-Host "  3. API Gateway üzerinden servisleri test edin" -ForegroundColor White
    Write-Host ""
} else {
    Write-Host "=== BAZI BAĞLANTILAR BAŞARISIZ ===" -ForegroundColor Red
    Write-Host ""
    Write-Host "Lütfen hataları kontrol edin:" -ForegroundColor Yellow
    Write-Host "  - PostgreSQL'in çalıştığından emin olun" -ForegroundColor White
    Write-Host "  - Veritabanlarının oluşturulduğunu kontrol edin" -ForegroundColor White
    Write-Host "  - application.yaml dosyalarındaki bağlantı bilgilerini kontrol edin" -ForegroundColor White
    Write-Host ""
}

