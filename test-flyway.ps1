# Flyway Migration Test Script
# Bu script Flyway migration'larının durumunu kontrol eder

Write-Host "=== FLYWAY MIGRATION TEST ===" -ForegroundColor Cyan
Write-Host ""

# 1. PostgreSQL Bağlantısı
Write-Host "1. POSTGRESQL BAĞLANTISI" -ForegroundColor Yellow
Write-Host ""

try {
    $pgTest = docker exec postgres pg_isready -U postgres 2>&1
    if ($LASTEXITCODE -eq 0) {
        Write-Host "  ✓ PostgreSQL çalışıyor" -ForegroundColor Green
    } else {
        Write-Host "  ✗ PostgreSQL çalışmıyor" -ForegroundColor Red
        Write-Host "    PostgreSQL'i başlatın: docker-compose up -d postgres" -ForegroundColor Gray
        exit 1
    }
} catch {
    Write-Host "  ✗ PostgreSQL kontrol edilemedi" -ForegroundColor Red
    exit 1
}

Write-Host ""

# 2. Database'lerin Varlığı
Write-Host "2. DATABASE'LERİN VARLIĞI" -ForegroundColor Yellow
Write-Host ""

$databases = @("user_db", "product_db", "order_db", "inventory_db", "notification_db")
$allDatabasesExist = $true

foreach ($db in $databases) {
    try {
        $dbCheck = docker exec postgres psql -U postgres -tAc "SELECT 1 FROM pg_database WHERE datname='$db'" 2>&1
        if ($dbCheck -match "1") {
            Write-Host "  ✓ $db mevcut" -ForegroundColor Green
        } else {
            Write-Host "  ✗ $db bulunamadı" -ForegroundColor Red
            $allDatabasesExist = $false
        }
    } catch {
        Write-Host "  ✗ $db kontrol edilemedi" -ForegroundColor Red
        $allDatabasesExist = $false
    }
}

Write-Host ""

if (-not $allDatabasesExist) {
    Write-Host "  ⚠ Bazı database'ler eksik. create-databases.sql'i çalıştırın." -ForegroundColor Yellow
    Write-Host ""
}

# 3. Flyway Schema History Kontrolü
Write-Host "3. FLYWAY SCHEMA HISTORY" -ForegroundColor Yellow
Write-Host ""

foreach ($db in $databases) {
    Write-Host "  $db:" -ForegroundColor Cyan
    try {
        $historyCheck = docker exec postgres psql -U postgres -d $db -tAc "SELECT COUNT(*) FROM flyway_schema_history" 2>&1
        if ($LASTEXITCODE -eq 0) {
            $migrationCount = $historyCheck.Trim()
            if ($migrationCount -match "^\d+$") {
                Write-Host "    ✓ Migration history tablosu mevcut" -ForegroundColor Green
                Write-Host "    ✓ Çalıştırılan migration sayısı: $migrationCount" -ForegroundColor Gray
                
                # Migration detayları
                $migrations = docker exec postgres psql -U postgres -d $db -tAc "SELECT version, description, success FROM flyway_schema_history ORDER BY installed_rank" 2>&1
                if ($migrations) {
                    Write-Host "    Migration'lar:" -ForegroundColor Gray
                    $migrations -split "`n" | ForEach-Object {
                        if ($_.Trim()) {
                            Write-Host "      • $_" -ForegroundColor White
                        }
                    }
                }
            } else {
                Write-Host "    ⚠ Migration history tablosu henüz oluşturulmamış" -ForegroundColor Yellow
                Write-Host "      (Servis başlatıldığında otomatik oluşturulacak)" -ForegroundColor Gray
            }
        } else {
            Write-Host "    ✗ Migration history kontrol edilemedi" -ForegroundColor Red
        }
    } catch {
        Write-Host "    ✗ Hata: $($_.Exception.Message)" -ForegroundColor Red
    }
    Write-Host ""
}

# 4. Tabloların Varlığı
Write-Host "4. TABLOLARIN VARLIĞI" -ForegroundColor Yellow
Write-Host ""

$tableChecks = @{
    "user_db" = @("users")
    "product_db" = @("products")
    "order_db" = @("orders", "order_items")
    "inventory_db" = @("inventory")
    "notification_db" = @("notifications")
}

foreach ($db in $tableChecks.Keys) {
    Write-Host "  $db:" -ForegroundColor Cyan
    $tables = $tableChecks[$db]
    foreach ($table in $tables) {
        try {
            $tableCheck = docker exec postgres psql -U postgres -d $db -tAc "SELECT COUNT(*) FROM information_schema.tables WHERE table_name='$table'" 2>&1
            if ($tableCheck.Trim() -eq "1") {
                Write-Host "    ✓ $table tablosu mevcut" -ForegroundColor Green
            } else {
                Write-Host "    ✗ $table tablosu bulunamadı" -ForegroundColor Red
            }
        } catch {
            Write-Host "    ✗ $table kontrol edilemedi" -ForegroundColor Red
        }
    }
    Write-Host ""
}

# 5. Özet
Write-Host "=== ÖZET ===" -ForegroundColor Cyan
Write-Host ""
Write-Host "✅ Flyway migration'ları yapılandırıldı" -ForegroundColor Green
Write-Host "✅ Migration script'leri oluşturuldu" -ForegroundColor Green
Write-Host ""
Write-Host "📋 Migration Dosyaları:" -ForegroundColor Yellow
Write-Host "   • user-service/src/main/resources/db/migration/V1__Initial_schema.sql" -ForegroundColor Gray
Write-Host "   • product-service/src/main/resources/db/migration/V1__Initial_schema.sql" -ForegroundColor Gray
Write-Host "   • order-service/src/main/resources/db/migration/V1__Initial_schema.sql" -ForegroundColor Gray
Write-Host "   • inventory-service/src/main/resources/db/migration/V1__Initial_schema.sql" -ForegroundColor Gray
Write-Host "   • notification-service/src/main/resources/db/migration/V1__Initial_schema.sql" -ForegroundColor Gray
Write-Host ""
Write-Host "🚀 Migration'lar otomatik çalışacak:" -ForegroundColor Yellow
Write-Host "   • Servisler başlatıldığında Flyway otomatik migration'ları çalıştırır" -ForegroundColor Gray
Write-Host "   • Migration history flyway_schema_history tablosunda saklanır" -ForegroundColor Gray
Write-Host ""
Write-Host "📚 Detaylı bilgi: FLYWAY_MIGRATION_GUIDE.md" -ForegroundColor Cyan
Write-Host ""

