-- Basit Review Ekleme Script'i
-- Her ürün için 5-15 arası review ve 5-10 yorum ekler

-- Test kullanıcılar
\set admin_user '550e8400-e29b-41d4-a716-446655440001'
\set john_user '550e8400-e29b-41d4-a716-446655440002'
\set jane_user '550e8400-e29b-41d4-a716-446655440003'
\set premium_user '550e8400-e29b-41d4-a716-446655440004'
\set test_user '550e8400-e29b-41d4-a716-446655440005'

-- Tüm ürünler için review ekle (örnek - tüm product ID'leri için)
-- Her ürün için 10 review, 7 yorum ekliyoruz

DO $$
DECLARE
    product_record RECORD;
    review_count INTEGER;
    comment_count INTEGER;
    i INTEGER;
    user_ids UUID[] := ARRAY[
        '550e8400-e29b-41d4-a716-446655440001'::UUID,
        '550e8400-e29b-41d4-a716-446655440002'::UUID,
        '550e8400-e29b-41d4-a716-446655440003'::UUID,
        '550e8400-e29b-41d4-a716-446655440004'::UUID,
        '550e8400-e29b-41d4-a716-446655440005'::UUID
    ];
    user_names TEXT[] := ARRAY['Admin User', 'John Doe', 'Jane Smith', 'Premium Customer', 'Test User'];
    positive_comments TEXT[] := ARRAY[
        'Harika bir ürün! Çok memnun kaldım, kesinlikle tavsiye ederim.',
        'Beklediğimden çok daha iyi çıktı. Kalite ve performans mükemmel.',
        'Çok beğendim, herkese öneririm. Fiyatına göre çok değerli.',
        'Mükemmel kalite, uzun süre kullanacağım. Çok memnun kaldım.',
        'Tasarımı çok şık ve kullanışlı. Beklentilerimi aştı.'
    ];
    neutral_comments TEXT[] := ARRAY[
        'Ürün iyi ama fiyat biraz yüksek. Yine de memnunum.',
        'Güzel ürün, memnunum. Belki biraz daha uygun fiyatlı olabilirdi.'
    ];
    negative_comments TEXT[] := ARRAY[
        'Ürün beklentilerimi karşılamadı. Kalite düşük geldi.',
        'Fiyatına göre kalite düşük. Memnun kalmadım.'
    ];
    selected_user_id UUID;
    selected_user_name TEXT;
    selected_rating INTEGER;
    selected_comment TEXT;
    days_ago INTEGER;
    user_index INTEGER;
BEGIN
    -- Tüm ürünler için review ekle
    FOR product_record IN 
        SELECT id FROM products ORDER BY name
    LOOP
        -- Her ürün için 5-15 arası rastgele review sayısı
        review_count := 5 + floor(random() * 11)::INTEGER;
        comment_count := 5 + floor(random() * 6)::INTEGER;
        
        -- Review'ları ekle
        FOR i IN 1..review_count LOOP
            -- Rastgele kullanıcı seç
            user_index := 1 + (i % array_length(user_ids, 1));
            selected_user_id := user_ids[user_index];
            selected_user_name := user_names[user_index];
            
            -- Rating dağılımı: %60 4-5, %30 3, %10 1-2
            IF random() < 0.6 THEN
                selected_rating := 4 + floor(random() * 2)::INTEGER;
            ELSIF random() < 0.9 THEN
                selected_rating := 3;
            ELSE
                selected_rating := 1 + floor(random() * 2)::INTEGER;
            END IF;
            
            -- Yorum ekle
            IF i <= comment_count THEN
                IF selected_rating >= 4 THEN
                    selected_comment := positive_comments[1 + floor(random() * array_length(positive_comments, 1))::INTEGER];
                ELSIF selected_rating = 3 THEN
                    selected_comment := neutral_comments[1 + floor(random() * array_length(neutral_comments, 1))::INTEGER];
                ELSE
                    selected_comment := negative_comments[1 + floor(random() * array_length(negative_comments, 1))::INTEGER];
                END IF;
            ELSE
                selected_comment := NULL;
            END IF;
            
            -- Rastgele geçmiş tarih
            days_ago := 1 + floor(random() * 60)::INTEGER;
            
            -- Review ekle (her review için farklı user_id kullan - unique constraint için)
            INSERT INTO reviews (
                id, product_id, user_id, user_name, rating, comment, 
                is_approved, helpful_count, created_at, updated_at
            ) VALUES (
                gen_random_uuid(),
                product_record.id,
                selected_user_id,
                selected_user_name || ' ' || i::TEXT,  -- Unique constraint için farklı isim
                selected_rating,
                selected_comment,
                true,
                floor(random() * 50)::INTEGER,
                CURRENT_TIMESTAMP - (days_ago || ' days')::INTERVAL,
                CURRENT_TIMESTAMP - (days_ago || ' days')::INTERVAL
            )
            ON CONFLICT (product_id, user_id) DO NOTHING;
        END LOOP;
    END LOOP;
    
    RAISE NOTICE 'Review ekleme tamamlandı!';
END $$;

-- Sonuçları kontrol et
SELECT 
    COUNT(*) as total_reviews,
    COUNT(DISTINCT product_id) as products_with_reviews,
    COUNT(comment) as reviews_with_comments,
    ROUND(AVG(rating), 2) as average_rating
FROM reviews;

