# RabbitMQ Test Script
# Bu script RabbitMQ'nun calisip calismadigini ve queue'larin olusturuldugunu test eder

Write-Host "=== RabbitMQ Test Script ===" -ForegroundColor Cyan
Write-Host ""

# 1. RabbitMQ Container Durumu
Write-Host "1. RabbitMQ Container Durumu:" -ForegroundColor Yellow
docker ps --filter "name=rabbitmq" --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
Write-Host ""

# 2. RabbitMQ Management API Test
Write-Host "2. RabbitMQ Management API Test:" -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:15672/api/overview" -Method Get -Headers @{Authorization = "Basic " + [Convert]::ToBase64String([Text.Encoding]::ASCII.GetBytes("guest:guest"))} -UseBasicParsing
    $overview = $response.Content | ConvertFrom-Json
    Write-Host "   OK RabbitMQ Version: $($overview.rabbitmq_version)" -ForegroundColor Green
    Write-Host "   OK Management Version: $($overview.management_version)" -ForegroundColor Green
} catch {
    Write-Host "   ERROR Management API'ye erisilemiyor: $_" -ForegroundColor Red
}
Write-Host ""

# 3. Queue Listesi
Write-Host "3. Mevcut Queue'lar:" -ForegroundColor Yellow
try {
    $queues = Invoke-WebRequest -Uri "http://localhost:15672/api/queues" -Method Get -Headers @{Authorization = "Basic " + [Convert]::ToBase64String([Text.Encoding]::ASCII.GetBytes("guest:guest"))} -UseBasicParsing
    $queueList = $queues.Content | ConvertFrom-Json
    if ($queueList.Count -eq 0) {
        Write-Host "   WARNING Henuz queue olusturulmamis. Servisler baslatildiginda otomatik olusturulacak." -ForegroundColor Yellow
    } else {
        foreach ($queue in $queueList) {
            Write-Host "   OK $($queue.name) - Messages: $($queue.messages)" -ForegroundColor Green
        }
    }
} catch {
    Write-Host "   ERROR Queue listesi alinamadi: $_" -ForegroundColor Red
}
Write-Host ""

# 4. Baglanti Testi
Write-Host "4. Port Kontrolu:" -ForegroundColor Yellow
$port5672 = Test-NetConnection -ComputerName localhost -Port 5672 -InformationLevel Quiet -WarningAction SilentlyContinue
$port15672 = Test-NetConnection -ComputerName localhost -Port 15672 -InformationLevel Quiet -WarningAction SilentlyContinue

if ($port5672) {
    Write-Host "   OK Port 5672 (AMQP) acik" -ForegroundColor Green
} else {
    Write-Host "   ERROR Port 5672 (AMQP) kapali" -ForegroundColor Red
}

if ($port15672) {
    Write-Host "   OK Port 15672 (Management) acik" -ForegroundColor Green
} else {
    Write-Host "   ERROR Port 15672 (Management) kapali" -ForegroundColor Red
}
Write-Host ""

Write-Host "=== Test Tamamlandi ===" -ForegroundColor Cyan
Write-Host ""
Write-Host "Management UI: http://localhost:15672" -ForegroundColor Cyan
Write-Host "Username: guest" -ForegroundColor Cyan
Write-Host "Password: guest" -ForegroundColor Cyan
