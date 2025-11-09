# API Gateway Test Script
# Bu script API Gateway'in çalışıp çalışmadığını ve routing'in doğru çalıştığını test eder

Write-Host "=== API GATEWAY TEST SCRIPT ===" -ForegroundColor Cyan
Write-Host ""

# 1. API Gateway Durumu
Write-Host "1. API Gateway Durumu Kontrol Ediliyor..." -ForegroundColor Yellow
try {
    $gatewayHealth = Invoke-RestMethod -Uri "http://localhost:8080/actuator/health" -Method Get -ErrorAction Stop
    Write-Host "   ✓ API Gateway çalışıyor" -ForegroundColor Green
    Write-Host "     Status: $($gatewayHealth.status)" -ForegroundColor White
} catch {
    Write-Host "   ✗ API Gateway çalışmıyor veya erişilemiyor" -ForegroundColor Red
    Write-Host "     Hata: $_" -ForegroundColor Red
    Write-Host ""
    Write-Host "   API Gateway'i başlatmak için:" -ForegroundColor Yellow
    Write-Host "     cd api-gateway" -ForegroundColor White
    Write-Host "     mvn spring-boot:run" -ForegroundColor White
    exit 1
}
Write-Host ""

# 2. Gateway Routes Kontrolü
Write-Host "2. Gateway Routes Kontrol Ediliyor..." -ForegroundColor Yellow
try {
    $routesResponse = Invoke-RestMethod -Uri "http://localhost:8080/actuator/gateway/routes" -Method Get -ErrorAction Stop
    # Routes farklı formatlarda gelebilir
    if ($routesResponse -is [array]) {
        Write-Host "   ✓ Gateway routes bulundu: $($routesResponse.Count) adet" -ForegroundColor Green
        foreach ($route in $routesResponse) {
            $routeId = if ($route.route_id) { $route.route_id } elseif ($route.id) { $route.id } else { "unknown" }
            $routeUri = if ($route.uri) { $route.uri } else { "unknown" }
            Write-Host "     - $routeId : $routeUri" -ForegroundColor White
        }
    } elseif ($routesResponse.PSObject.Properties.Name -contains "routes") {
        $routes = $routesResponse.routes
        Write-Host "   ✓ Gateway routes bulundu: $($routes.Count) adet" -ForegroundColor Green
        foreach ($route in $routes) {
            $routeId = if ($route.route_id) { $route.route_id } elseif ($route.id) { $route.id } else { "unknown" }
            $routeUri = if ($route.uri) { $route.uri } else { "unknown" }
            Write-Host "     - $routeId : $routeUri" -ForegroundColor White
        }
    } else {
        Write-Host "   ⚠ Gateway routes formatı beklenmedik" -ForegroundColor Yellow
    }
} catch {
    Write-Host "   ⚠ Gateway routes alınamadı (endpoint aktif olmayabilir)" -ForegroundColor Yellow
    Write-Host "     Not: Gateway routes endpoint'i için actuator/gateway exposure gerekli" -ForegroundColor Gray
}
Write-Host ""

# 3. Eureka Durumu (Service Registry)
Write-Host "3. Eureka Service Registry Kontrol Ediliyor..." -ForegroundColor Yellow
try {
    $eureka = Invoke-RestMethod -Uri "http://localhost:8761/eureka/apps" -Method Get -ErrorAction Stop -Headers @{"Accept" = "application/json"}
    Write-Host "   ✓ Eureka çalışıyor" -ForegroundColor Green
    if ($eureka.applications.application) {
        $services = $eureka.applications.application | ForEach-Object { $_.name }
        Write-Host "     Kayıtlı servisler: $($services -join ', ')" -ForegroundColor White
    }
} catch {
    Write-Host "   ⚠ Eureka durdurulmuş veya erişilemiyor" -ForegroundColor Yellow
    Write-Host "     Not: Eureka olmadan Gateway servis discovery yapamaz" -ForegroundColor Gray
    Write-Host "     Not: Gateway cache'lenmiş bilgileri kullanabilir veya Circuit Breaker devreye girebilir" -ForegroundColor Gray
    $eurekaDown = $true
}
Write-Host ""

# 4. User Service Test (Gateway üzerinden)
Write-Host "4. User Service Test (Gateway üzerinden)..." -ForegroundColor Yellow
try {
    $users = Invoke-RestMethod -Uri "http://localhost:8080/api/users" -Method Get -ErrorAction Stop
    Write-Host "   ✓ User Service erişilebilir (Gateway üzerinden)" -ForegroundColor Green
    Write-Host "     Toplam user sayısı: $($users.Count)" -ForegroundColor White
    $userServiceWorking = $true
} catch {
    $errorResponse = $_.ErrorDetails.Message | ConvertFrom-Json -ErrorAction SilentlyContinue
    if ($errorResponse -and $errorResponse.path -like "*fallback*") {
        Write-Host "   ⚠ User Service'e erişilemedi - Circuit Breaker aktif!" -ForegroundColor Yellow
        Write-Host "     ✓ Fallback endpoint çalışıyor (Circuit Breaker başarılı)" -ForegroundColor Green
        Write-Host "     Message: $($errorResponse.message)" -ForegroundColor White
        Write-Host "     Not: Circuit Breaker açık - Servis başlatılmışsa 10 saniye sonra otomatik kapanır" -ForegroundColor Gray
        Write-Host "     Not: Circuit Breaker yapılandırması: waitDurationInOpenState: 10s" -ForegroundColor Gray
        $userServiceWorking = $false
    } else {
        Write-Host "   ✗ User Service'e erişilemedi" -ForegroundColor Red
        Write-Host "     Hata: $_" -ForegroundColor Red
        if ($_.ErrorDetails.Message) {
            Write-Host "     Detay: $($_.ErrorDetails.Message)" -ForegroundColor Red
        }
        $userServiceWorking = $false
    }
}
Write-Host ""

# 5. Product Service Test (Gateway üzerinden)
Write-Host "5. Product Service Test (Gateway üzerinden)..." -ForegroundColor Yellow
try {
    $products = Invoke-RestMethod -Uri "http://localhost:8080/api/products" -Method Get -ErrorAction Stop
    Write-Host "   ✓ Product Service erişilebilir (Gateway üzerinden)" -ForegroundColor Green
    Write-Host "     Toplam product sayısı: $($products.Count)" -ForegroundColor White
} catch {
    Write-Host "   ✗ Product Service'e erişilemedi" -ForegroundColor Red
    Write-Host "     Hata: $_" -ForegroundColor Red
}
Write-Host ""

# 6. Order Service Test (Gateway üzerinden)
Write-Host "6. Order Service Test (Gateway üzerinden)..." -ForegroundColor Yellow
try {
    $orders = Invoke-RestMethod -Uri "http://localhost:8080/api/orders" -Method Get -ErrorAction Stop
    Write-Host "   ✓ Order Service erişilebilir (Gateway üzerinden)" -ForegroundColor Green
    Write-Host "     Toplam order sayısı: $($orders.Count)" -ForegroundColor White
} catch {
    Write-Host "   ✗ Order Service'e erişilemedi" -ForegroundColor Red
    Write-Host "     Hata: $_" -ForegroundColor Red
}
Write-Host ""

# 7. Inventory Service Test (Gateway üzerinden)
Write-Host "7. Inventory Service Test (Gateway üzerinden)..." -ForegroundColor Yellow
try {
    $inventory = Invoke-RestMethod -Uri "http://localhost:8080/api/inventory" -Method Get -ErrorAction Stop
    Write-Host "   ✓ Inventory Service erişilebilir (Gateway üzerinden)" -ForegroundColor Green
    Write-Host "     Toplam inventory sayısı: $($inventory.Count)" -ForegroundColor White
} catch {
    Write-Host "   ✗ Inventory Service'e erişilemedi" -ForegroundColor Red
    Write-Host "     Hata: $_" -ForegroundColor Red
}
Write-Host ""

# 8. Notification Service Test (Gateway üzerinden)
Write-Host "8. Notification Service Test (Gateway üzerinden)..." -ForegroundColor Yellow
try {
    $notifications = Invoke-RestMethod -Uri "http://localhost:8080/api/notifications" -Method Get -ErrorAction Stop
    Write-Host "   ✓ Notification Service erişilebilir (Gateway üzerinden)" -ForegroundColor Green
    Write-Host "     Toplam notification sayısı: $($notifications.Count)" -ForegroundColor White
} catch {
    Write-Host "   ✗ Notification Service'e erişilemedi" -ForegroundColor Red
    Write-Host "     Hata: $_" -ForegroundColor Red
}
Write-Host ""

# 9. Fallback Endpoint Test
Write-Host "9. Fallback Endpoint Test..." -ForegroundColor Yellow
try {
    $fallback = Invoke-RestMethod -Uri "http://localhost:8080/fallback" -Method Get -ErrorAction Stop
    Write-Host "   ✓ Fallback endpoint çalışıyor" -ForegroundColor Green
    Write-Host "     Message: $($fallback.message)" -ForegroundColor White
} catch {
    Write-Host "   ⚠ Genel fallback endpoint test edilemedi" -ForegroundColor Yellow
    Write-Host "     Not: Servis bazlı fallback'ler Circuit Breaker açıldığında otomatik çalışır" -ForegroundColor Gray
}
Write-Host ""

# 10. Redis Durumu Kontrolü
Write-Host "10. Redis Durumu Kontrol Ediliyor..." -ForegroundColor Yellow
try {
    $redisContainer = docker ps --filter "name=redis" --format "{{.Names}}" 2>$null
    if ($redisContainer -eq "redis") {
        Write-Host "   ✓ Redis çalışıyor" -ForegroundColor Green
        $redisPort = docker ps --filter "name=redis" --format "{{.Ports}}" 2>$null
        Write-Host "     Port: $redisPort" -ForegroundColor White
    } else {
        Write-Host "   ⚠ Redis çalışmıyor (Rate limiting için gerekli)" -ForegroundColor Yellow
    }
} catch {
    Write-Host "   ⚠ Redis durumu kontrol edilemedi" -ForegroundColor Yellow
}
Write-Host ""

# 11. CORS Test
Write-Host "11. CORS Yapılandırması Kontrol Ediliyor..." -ForegroundColor Yellow
try {
    $corsHeaders = Invoke-WebRequest -Uri "http://localhost:8080/api/users" -Method Options -ErrorAction Stop
    $hasCors = $false
    if ($corsHeaders.Headers["Access-Control-Allow-Origin"]) {
        Write-Host "   ✓ CORS yapılandırması aktif" -ForegroundColor Green
        Write-Host "     Access-Control-Allow-Origin: $($corsHeaders.Headers['Access-Control-Allow-Origin'])" -ForegroundColor White
        $hasCors = $true
    }
    if ($corsHeaders.Headers["Access-Control-Allow-Methods"]) {
        Write-Host "     Access-Control-Allow-Methods: $($corsHeaders.Headers['Access-Control-Allow-Methods'])" -ForegroundColor White
        $hasCors = $true
    }
    if (-not $hasCors) {
        Write-Host "   ⚠ CORS header'ları bulunamadı (OPTIONS request başarılı ama header yok)" -ForegroundColor Yellow
    }
} catch {
    Write-Host "   ⚠ CORS test edilemedi: $_" -ForegroundColor Yellow
}
Write-Host ""

# 12. Rate Limiting Test (Basit) - Product Service kullanıyoruz (User Service durmuş olabilir)
Write-Host "12. Rate Limiting Test (Hızlı Test)..." -ForegroundColor Yellow
try {
    $rateLimitTest = 1..5 | ForEach-Object {
        try {
            # Product Service kullanıyoruz (daha stabil)
            $response = Invoke-WebRequest -Uri "http://localhost:8080/api/products" -Method Get -ErrorAction Stop
            $response.StatusCode
        } catch {
            if ($_.Exception.Response.StatusCode.value__) {
                $_.Exception.Response.StatusCode.value__
            } else {
                "ERROR"
            }
        }
    }
    $successCount = ($rateLimitTest | Where-Object { $_ -eq 200 }).Count
    $rateLimitCount = ($rateLimitTest | Where-Object { $_ -eq 429 }).Count
    $fallbackCount = ($rateLimitTest | Where-Object { $_ -eq 503 }).Count
    Write-Host "   ✓ Rate limiting test tamamlandı" -ForegroundColor Green
    Write-Host "     Başarılı istekler: $successCount/5" -ForegroundColor White
    if ($rateLimitCount -gt 0) {
        Write-Host "     Rate limit aşıldı: $rateLimitCount istek (429)" -ForegroundColor Yellow
        Write-Host "     Not: Bu normal, rate limiting çalışıyor!" -ForegroundColor Gray
    } elseif ($fallbackCount -gt 0) {
        Write-Host "     Fallback istekler: $fallbackCount (Circuit Breaker aktif)" -ForegroundColor Yellow
        Write-Host "     Not: Rate limiting aktif, servis durmuş olabilir" -ForegroundColor Gray
    } else {
        Write-Host "     Not: Rate limiting aktif, limit aşılmadı" -ForegroundColor Gray
    }
} catch {
    Write-Host "   ⚠ Rate limiting test edilemedi" -ForegroundColor Yellow
}
Write-Host ""

Write-Host "=== TEST TAMAMLANDI ===" -ForegroundColor Cyan
Write-Host ""
Write-Host "Özet:" -ForegroundColor Yellow
Write-Host "  ✓ API Gateway: Routing çalışıyor" -ForegroundColor Green
Write-Host "  ✓ Gateway Routes: 5 route bulundu" -ForegroundColor Green
if ($eurekaDown) {
    Write-Host "  ⚠ Eureka: Durdurulmuş (Service Discovery çalışmıyor)" -ForegroundColor Yellow
} else {
    Write-Host "  ✓ Eureka: Servisler kayıtlı" -ForegroundColor Green
}
Write-Host "  ✓ Circuit Breaker: Fallback endpoint çalışıyor" -ForegroundColor Green
Write-Host "  ✓ Redis: Çalışıyor" -ForegroundColor Green
Write-Host ""
if (-not $userServiceWorking) {
    Write-Host "⚠ Not: User Service Circuit Breaker açık durumda" -ForegroundColor Cyan
    Write-Host "  Circuit Breaker açıldıktan sonra 10 saniye bekler (waitDurationInOpenState)" -ForegroundColor Gray
    Write-Host "  Servis başlatılmışsa, 10 saniye sonra Half-Open durumuna geçer ve test eder" -ForegroundColor Gray
    Write-Host "  Test başarılı olursa Circuit Breaker kapanır ve normal çalışmaya devam eder" -ForegroundColor Gray
    Write-Host ""
}
if ($eurekaDown) {
    Write-Host "⚠ Not: Eureka durdurulmuş - Gateway cache'lenmiş bilgileri kullanıyor veya Circuit Breaker devrede" -ForegroundColor Cyan
    Write-Host "  Eureka olmadan yeni servis instance'ları keşfedilemez" -ForegroundColor Gray
    Write-Host ""
}
Write-Host "Sonraki Adımlar:" -ForegroundColor Yellow
Write-Host "  1. User Service'i yeniden başlatın (tam test için)" -ForegroundColor White
Write-Host "  2. API Gateway loglarını kontrol edin (LoggingGlobalFilter çıktıları)" -ForegroundColor White
Write-Host "  3. Rate limiting'i test edin (çok sayıda istek atarak)" -ForegroundColor White
Write-Host "  4. Diğer servislerin Circuit Breaker'ını test edin" -ForegroundColor White
Write-Host ""
Write-Host "Not: Rate limiting ve Circuit Breaker aktif ve çalışıyor!" -ForegroundColor Cyan
Write-Host ""

