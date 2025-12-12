# Tüm ürünler için review ekleme script'i
Write-Host "Tüm ürünler için review ekleniyor..." -ForegroundColor Green

# Product ID'leri al
$products = docker exec postgres psql -U postgres -d product_db -t -c "SELECT id FROM products ORDER BY name;"

# Test kullanıcı ID'leri
$users = @(
    @{id='550e8400-e29b-41d4-a716-446655440001'; name='Admin User'},
    @{id='550e8400-e29b-41d4-a716-446655440002'; name='John Doe'},
    @{id='550e8400-e29b-41d4-a716-446655440003'; name='Jane Smith'},
    @{id='550e8400-e29b-41d4-a716-446655440004'; name='Premium Customer'},
    @{id='550e8400-e29b-41d4-a716-446655440005'; name='Test User'}
)

# Yorum metinleri
$positiveComments = @(
    'Harika bir ürün! Çok memnun kaldım, kesinlikle tavsiye ederim.',
    'Beklediğimden çok daha iyi çıktı. Kalite ve performans mükemmel.',
    'Çok beğendim, herkese öneririm. Fiyatına göre çok değerli.',
    'Mükemmel kalite, uzun süre kullanacağım. Çok memnun kaldım.',
    'Tasarımı çok şık ve kullanışlı. Beklentilerimi aştı.'
)

$neutralComments = @(
    'Ürün iyi ama fiyat biraz yüksek. Yine de memnunum.',
    'Güzel ürün, memnunum. Belki biraz daha uygun fiyatlı olabilirdi.',
    'Ürün beklentilerimi karşıladı. Orta seviye bir kalite.'
)

$negativeComments = @(
    'Ürün beklentilerimi karşılamadı. Kalite düşük geldi.',
    'Fiyatına göre kalite düşük. Memnun kalmadım.'
)

$sqlStatements = @()
$productCount = 0

foreach ($productLine in $products) {
    $productId = $productLine.Trim()
    if ([string]::IsNullOrWhiteSpace($productId)) { continue }
    
    $productCount++
    $reviewCount = Get-Random -Minimum 5 -Maximum 16  # 5-15 arası
    $commentCount = Get-Random -Minimum 5 -Maximum 11  # 5-10 arası
    
    Write-Host "Ürün $productCount için $reviewCount review ekleniyor..." -ForegroundColor Yellow
    
    for ($i = 1; $i -le $reviewCount; $i++) {
        $user = $users[($i - 1) % $users.Length]
        $rating = if (Get-Random -Maximum 10 -lt 6) { Get-Random -Minimum 4 -Maximum 6 } 
                  elseif (Get-Random -Maximum 10 -lt 9) { 3 } 
                  else { Get-Random -Minimum 1 -Maximum 3 }
        
        $comment = $null
        if ($i -le $commentCount) {
            if ($rating -ge 4) {
                $comment = $positiveComments | Get-Random
            } elseif ($rating -eq 3) {
                $comment = $neutralComments | Get-Random
            } else {
                $comment = $negativeComments | Get-Random
            }
            $comment = $comment.Replace("'", "''")  # SQL escape
        }
        
        $daysAgo = Get-Random -Minimum 1 -Maximum 61
        $helpfulCount = Get-Random -Minimum 0 -Maximum 51
        
        $commentSql = if ($comment) { "'$comment'" } else { "NULL" }
        
        $sql = "INSERT INTO reviews (id, product_id, user_id, user_name, rating, comment, is_approved, helpful_count, created_at, updated_at) VALUES (gen_random_uuid(), '$productId', '$($user.id)', '$($user.name)', $rating, $commentSql, true, $helpfulCount, CURRENT_TIMESTAMP - INTERVAL '$daysAgo days', CURRENT_TIMESTAMP - INTERVAL '$daysAgo days') ON CONFLICT (product_id, user_id) DO NOTHING;"
        
        $sqlStatements += $sql
    }
}

# SQL'leri çalıştır
$batchSize = 50
for ($i = 0; $i -lt $sqlStatements.Length; $i += $batchSize) {
    $batch = $sqlStatements[$i..([Math]::Min($i + $batchSize - 1, $sqlStatements.Length - 1))]
    $sqlBatch = $batch -join "`n"
    
    Write-Host "Batch $([Math]::Floor($i / $batchSize) + 1) çalıştırılıyor..." -ForegroundColor Cyan
    $sqlBatch | docker exec -i postgres psql -U postgres -d review_db
}

Write-Host "`nTamamlandı! Review sayısı kontrol ediliyor..." -ForegroundColor Green
docker exec postgres psql -U postgres -d review_db -c "SELECT COUNT(*) as total_reviews, COUNT(DISTINCT product_id) as product_count FROM reviews;"

