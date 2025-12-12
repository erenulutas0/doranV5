-- Review Service - Comprehensive Reviews for All Products
-- Migration: V9__Add_comprehensive_reviews_for_all_products.sql
-- Description: Her ürün için 5-15 arası değerlendirme ve 5-10 yorum ekler
-- NOT: Bu migration, mevcut review'ları temizleyip yeniden oluşturur

-- UUID extension'ı etkinleştir (eğer yoksa)
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Önce mevcut review'ları temizle
DELETE FROM reviews;

-- Test kullanıcı ID'leri (user-service'den)
-- Bu ID'ler insert-test-users.sql dosyasındaki ID'lerle eşleşmeli
DO $$
DECLARE
    test_user_ids UUID[] := ARRAY[
        '550e8400-e29b-41d4-a716-446655440001'::UUID, -- admin
        '550e8400-e29b-41d4-a716-446655440002'::UUID, -- john_doe
        '550e8400-e29b-41d4-a716-446655440003'::UUID, -- jane_smith
        '550e8400-e29b-41d4-a716-446655440004'::UUID, -- premium_user
        '550e8400-e29b-41d4-a716-446655440005'::UUID  -- test_user
    ];
    user_names TEXT[] := ARRAY['Admin User', 'John Doe', 'Jane Smith', 'Premium Customer', 'Test User'];
    
    -- Gerçekçi Türkçe yorum metinleri
    positive_comments TEXT[] := ARRAY[
        'Harika bir ürün! Çok memnun kaldım, kesinlikle tavsiye ederim.',
        'Beklediğimden çok daha iyi çıktı. Kalite ve performans mükemmel.',
        'Çok beğendim, herkese öneririm. Fiyatına göre çok değerli.',
        'Mükemmel kalite, uzun süre kullanacağım. Çok memnun kaldım.',
        'Tasarımı çok şık ve kullanışlı. Beklentilerimi aştı.',
        'Kalite ve performans harika. Kesinlikle tekrar alırım.',
        'Çok iyi bir satın alma oldu. Ürün tam olarak açıklamada belirtildiği gibi.',
        'Hızlı teslimat ve kaliteli ürün. Çok memnun kaldım.',
        'İyi bir seçim yaptım. Ürün çok kaliteli ve dayanıklı.',
        'Beklentilerimi karşıladı ve daha fazlasını sundu. Teşekkürler.',
        'Ürün çok güzel, paketleme de mükemmeldi. Kesinlikle öneririm.',
        'Kaliteli malzeme kullanılmış, işçilik çok iyi. Memnun kaldım.',
        'Çok pratik ve kullanışlı. Günlük hayatta çok işime yarıyor.',
        'Fiyatına göre çok değerli bir ürün. Kalite mükemmel.',
        'Ürün tam istediğim gibi. Çok memnun kaldım, teşekkürler.'
    ];
    
    neutral_comments TEXT[] := ARRAY[
        'Ürün iyi ama fiyat biraz yüksek. Yine de memnunum.',
        'Güzel ürün, memnunum. Belki biraz daha uygun fiyatlı olabilirdi.',
        'Ürün beklentilerimi karşıladı. Orta seviye bir kalite.',
        'İyi bir ürün ama bazı özellikler daha iyi olabilirdi.',
        'Genel olarak memnunum. Fiyat performans dengesi iyi.',
        'Ürün kullanışlı ama tasarımı biraz daha modern olabilirdi.',
        'Kalite iyi ama teslimat biraz geç oldu. Yine de memnunum.',
        'Ürün beklentilerimi karşıladı. Bazı küçük iyileştirmeler yapılabilir.',
        'Genel olarak iyi bir ürün. Fiyatına göre makul.',
        'Ürün kullanışlı, memnunum. Belki daha fazla renk seçeneği olabilirdi.'
    ];
    
    negative_comments TEXT[] := ARRAY[
        'Ürün beklentilerimi karşılamadı. Kalite düşük geldi.',
        'Fiyatına göre kalite düşük. Memnun kalmadım.',
        'Ürün çabuk bozuldu. Beklentilerimi karşılamadı.',
        'Kalite beklentilerimin altında kaldı. Memnun değilim.',
        'Ürün açıklamada belirtildiği gibi değil. Hayal kırıklığı.',
        'Teslimat gecikti ve ürün hasarlı geldi. Memnun değilim.',
        'Ürün çok küçük geldi. Boyut bilgisi yanlış olabilir.',
        'Kalite düşük, fiyatına göre değmez. Memnun kalmadım.',
        'Ürün kısa sürede bozuldu. Garanti kapsamında değilmiş.',
        'Beklentilerimi karşılamadı. Ürün kalitesi düşük.'
    ];
    
    product_record RECORD;
    review_count INTEGER;
    comment_count INTEGER;
    i INTEGER;
    j INTEGER;
    selected_user_id UUID;
    selected_user_name TEXT;
    selected_rating INTEGER;
    selected_comment TEXT;
    days_ago INTEGER;
    user_counter INTEGER := 0;
    product_count INTEGER := 0;
BEGIN
    -- Önce mevcut reviews tablosundaki farklı product_id'leri say
    SELECT COUNT(DISTINCT product_id) INTO product_count 
    FROM reviews 
    WHERE product_id IS NOT NULL;
    
    -- Eğer hiç product_id yoksa, V8'deki product ID'lerini kullan
    -- Veya product-service'den product ID'lerini almak gerekir
    IF product_count = 0 THEN
        RAISE NOTICE 'Reviews tablosunda hiç product_id bulunamadı. Product-service''den product ID''lerini alıp manuel olarak eklemeniz gerekir.';
        RAISE NOTICE 'Alternatif olarak, V8 migration''ındaki product ID''lerini kullanabilirsiniz.';
        RETURN;
    END IF;
    
    -- Her ürün için 5-15 arası review ekle
    FOR product_record IN 
        SELECT DISTINCT product_id 
        FROM reviews 
        WHERE product_id IS NOT NULL
    LOOP
        -- Her ürün için 5-15 arası rastgele review sayısı
        review_count := 5 + floor(random() * 11)::INTEGER; -- 5-15 arası
        comment_count := 5 + floor(random() * 6)::INTEGER;  -- 5-10 arası
        
        -- Review'ları ekle
        FOR i IN 1..review_count LOOP
            -- Rastgele kullanıcı seç (her review için farklı kullanıcı)
            user_counter := user_counter + 1;
            j := 1 + (user_counter % array_length(test_user_ids, 1));
            selected_user_id := test_user_ids[j];
            selected_user_name := user_names[j];
            
            -- Rating dağılımı: %60 4-5, %30 3, %10 1-2 (gerçekçi dağılım)
            IF random() < 0.6 THEN
                selected_rating := 4 + floor(random() * 2)::INTEGER; -- 4 veya 5
            ELSIF random() < 0.9 THEN
                selected_rating := 3;
            ELSE
                selected_rating := 1 + floor(random() * 2)::INTEGER; -- 1 veya 2
            END IF;
            
            -- Yorum ekle (comment_count kadar review'a yorum ekle)
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
            
            -- Rastgele geçmiş tarih (1-60 gün önce)
            days_ago := 1 + floor(random() * 60)::INTEGER;
            
            -- Review ekle
            -- NOT: Unique constraint (product_id, user_id) var, bu yüzden her review için farklı user_id kullanıyoruz
            -- Gerçek kullanımda, aynı kullanıcı aynı ürüne sadece bir review yapabilir
            INSERT INTO reviews (
                id, 
                product_id, 
                user_id, 
                user_name, 
                rating, 
                comment, 
                is_approved, 
                helpful_count, 
                created_at, 
                updated_at
            ) VALUES (
                gen_random_uuid()::UUID,
                product_record.product_id,
                selected_user_id, -- Her review için farklı kullanıcı seçiyoruz
                selected_user_name,
                selected_rating,
                selected_comment,
                true,
                floor(random() * 50)::INTEGER, -- 0-50 arası helpful count
                CURRENT_TIMESTAMP - (days_ago || ' days')::INTERVAL,
                CURRENT_TIMESTAMP - (days_ago || ' days')::INTERVAL
            )
            ON CONFLICT (product_id, user_id) DO NOTHING; -- Unique constraint hatası durumunda atla
        END LOOP;
    END LOOP;
    
    RAISE NOTICE 'Review migration tamamlandı. % ürün için review eklendi.', product_count;
END $$;

-- NOT: Bu migration çalıştığında, eğer reviews tablosunda hiç product_id yoksa
-- hiçbir review eklenmeyecektir. Bu durumda:
-- 1. Product-service'den tüm product ID'lerini alın (SELECT id FROM products;)
-- 2. Aşağıdaki gibi manuel INSERT statement'ları oluşturun
-- 3. Veya product-service API'sini kullanarak review'ları ekleyin

-- Alternatif: Eğer reviews tablosunda hiç product_id yoksa, 
-- product-service'den product ID'lerini alıp aşağıdaki gibi ekleyebilirsiniz:

/*
-- Örnek: Product-service'den product ID'lerini aldıktan sonra
-- Aşağıdaki gibi bir DO bloğu oluşturun:

DO $$
DECLARE
    -- Product-service'den aldığınız product ID'lerini buraya ekleyin
    product_ids UUID[] := ARRAY[
        'product-id-1'::UUID,
        'product-id-2'::UUID,
        -- ... tüm product ID'leri buraya ekleyin
    ];
    -- ... yukarıdaki kodun devamı (test_user_ids, comments, vs.)
BEGIN
    FOREACH product_id IN ARRAY product_ids
    LOOP
        -- Her product_id için review ekleme kodu
        -- (yukarıdaki kodun aynısı)
    END LOOP;
END $$;
*/

