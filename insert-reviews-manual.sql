-- Manual Review Insert Script
-- Her ürün için 5-15 arası review ve 5-10 yorum ekler
-- Product ID'leri product-service'den alınmıştır

-- Test kullanıcı ID'leri
\set admin_user '550e8400-e29b-41d4-a716-446655440001'
\set john_user '550e8400-e29b-41d4-a716-446655440002'
\set jane_user '550e8400-e29b-41d4-a716-446655440003'
\set premium_user '550e8400-e29b-41d4-a716-446655440004'
\set test_user '550e8400-e29b-41d4-a716-446655440005'

-- Önce mevcut review'ları temizle (opsiyonel)
-- DELETE FROM reviews;

-- Her ürün için review ekle
-- iPhone 15 Pro Max (385898fe-79f0-498f-9fc1-957902ba3dd1) - 12 reviews, 8 comments
INSERT INTO reviews (id, product_id, user_id, user_name, rating, comment, is_approved, helpful_count, created_at, updated_at) VALUES
(gen_random_uuid(), '385898fe-79f0-498f-9fc1-957902ba3dd1', '550e8400-e29b-41d4-a716-446655440002', 'John Doe', 5, 'Harika bir ürün! Çok memnun kaldım, kesinlikle tavsiye ederim.', true, 16, CURRENT_TIMESTAMP - INTERVAL '5 days', CURRENT_TIMESTAMP - INTERVAL '5 days'),
(gen_random_uuid(), '385898fe-79f0-498f-9fc1-957902ba3dd1', '550e8400-e29b-41d4-a716-446655440003', 'Jane Smith', 5, 'Beklediğimden çok daha iyi çıktı. Kalite ve performans mükemmel.', true, 12, CURRENT_TIMESTAMP - INTERVAL '12 days', CURRENT_TIMESTAMP - INTERVAL '12 days'),
(gen_random_uuid(), '385898fe-79f0-498f-9fc1-957902ba3dd1', '550e8400-e29b-41d4-a716-446655440004', 'Premium Customer', 4, 'Çok beğendim, herkese öneririm. Fiyatına göre çok değerli.', true, 8, CURRENT_TIMESTAMP - INTERVAL '8 days', CURRENT_TIMESTAMP - INTERVAL '8 days'),
(gen_random_uuid(), '385898fe-79f0-498f-9fc1-957902ba3dd1', '550e8400-e29b-41d4-a716-446655440005', 'Test User', 5, 'Mükemmel kalite, uzun süre kullanacağım. Çok memnun kaldım.', true, 15, CURRENT_TIMESTAMP - INTERVAL '15 days', CURRENT_TIMESTAMP - INTERVAL '15 days'),
(gen_random_uuid(), '385898fe-79f0-498f-9fc1-957902ba3dd1', '550e8400-e29b-41d4-a716-446655440001', 'Admin User', 4, 'Tasarımı çok şık ve kullanışlı. Beklentilerimi aştı.', true, 10, CURRENT_TIMESTAMP - INTERVAL '20 days', CURRENT_TIMESTAMP - INTERVAL '20 days'),
(gen_random_uuid(), '385898fe-79f0-498f-9fc1-957902ba3dd1', '550e8400-e29b-41d4-a716-446655440002', 'John Doe', 5, 'Kalite ve performans harika. Kesinlikle tekrar alırım.', true, 20, CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP - INTERVAL '3 days'),
(gen_random_uuid(), '385898fe-79f0-498f-9fc1-957902ba3dd1', '550e8400-e29b-41d4-a716-446655440003', 'Jane Smith', 3, 'Ürün iyi ama fiyat biraz yüksek. Yine de memnunum.', true, 5, CURRENT_TIMESTAMP - INTERVAL '25 days', CURRENT_TIMESTAMP - INTERVAL '25 days'),
(gen_random_uuid(), '385898fe-79f0-498f-9fc1-957902ba3dd1', '550e8400-e29b-41d4-a716-446655440004', 'Premium Customer', 5, 'Çok iyi bir satın alma oldu. Ürün tam olarak açıklamada belirtildiği gibi.', true, 18, CURRENT_TIMESTAMP - INTERVAL '7 days', CURRENT_TIMESTAMP - INTERVAL '7 days'),
(gen_random_uuid(), '385898fe-79f0-498f-9fc1-957902ba3dd1', '550e8400-e29b-41d4-a716-446655440005', 'Test User', 4, NULL, true, 0, CURRENT_TIMESTAMP - INTERVAL '18 days', CURRENT_TIMESTAMP - INTERVAL '18 days'),
(gen_random_uuid(), '385898fe-79f0-498f-9fc1-957902ba3dd1', '550e8400-e29b-41d4-a716-446655440001', 'Admin User', 5, NULL, true, 0, CURRENT_TIMESTAMP - INTERVAL '10 days', CURRENT_TIMESTAMP - INTERVAL '10 days'),
(gen_random_uuid(), '385898fe-79f0-498f-9fc1-957902ba3dd1', '550e8400-e29b-41d4-a716-446655440002', 'John Doe', 4, NULL, true, 0, CURRENT_TIMESTAMP - INTERVAL '30 days', CURRENT_TIMESTAMP - INTERVAL '30 days'),
(gen_random_uuid(), '385898fe-79f0-498f-9fc1-957902ba3dd1', '550e8400-e29b-41d4-a716-446655440003', 'Jane Smith', 5, NULL, true, 0, CURRENT_TIMESTAMP - INTERVAL '1 days', CURRENT_TIMESTAMP - INTERVAL '1 days')
ON CONFLICT (product_id, user_id) DO NOTHING;

-- Samsung Galaxy S24 Ultra (57bb1451-d63d-4daa-8d23-8fb315f6da21) - 10 reviews, 7 comments
INSERT INTO reviews (id, product_id, user_id, user_name, rating, comment, is_approved, helpful_count, created_at, updated_at) VALUES
(gen_random_uuid(), '57bb1451-d63d-4daa-8d23-8fb315f6da21', '550e8400-e29b-41d4-a716-446655440002', 'John Doe', 5, 'Hızlı teslimat ve kaliteli ürün. Çok memnun kaldım.', true, 14, CURRENT_TIMESTAMP - INTERVAL '6 days', CURRENT_TIMESTAMP - INTERVAL '6 days'),
(gen_random_uuid(), '57bb1451-d63d-4daa-8d23-8fb315f6da21', '550e8400-e29b-41d4-a716-446655440003', 'Jane Smith', 4, 'İyi bir seçim yaptım. Ürün çok kaliteli ve dayanıklı.', true, 9, CURRENT_TIMESTAMP - INTERVAL '14 days', CURRENT_TIMESTAMP - INTERVAL '14 days'),
(gen_random_uuid(), '57bb1451-d63d-4daa-8d23-8fb315f6da21', '550e8400-e29b-41d4-a716-446655440004', 'Premium Customer', 5, 'Beklentilerimi karşıladı ve daha fazlasını sundu. Teşekkürler.', true, 11, CURRENT_TIMESTAMP - INTERVAL '9 days', CURRENT_TIMESTAMP - INTERVAL '9 days'),
(gen_random_uuid(), '57bb1451-d63d-4daa-8d23-8fb315f6da21', '550e8400-e29b-41d4-a716-446655440005', 'Test User', 4, 'Ürün çok güzel, paketleme de mükemmeldi. Kesinlikle öneririm.', true, 7, CURRENT_TIMESTAMP - INTERVAL '22 days', CURRENT_TIMESTAMP - INTERVAL '22 days'),
(gen_random_uuid(), '57bb1451-d63d-4daa-8d23-8fb315f6da21', '550e8400-e29b-41d4-a716-446655440001', 'Admin User', 3, 'Güzel ürün, memnunum. Belki biraz daha uygun fiyatlı olabilirdi.', true, 4, CURRENT_TIMESTAMP - INTERVAL '16 days', CURRENT_TIMESTAMP - INTERVAL '16 days'),
(gen_random_uuid(), '57bb1451-d63d-4daa-8d23-8fb315f6da21', '550e8400-e29b-41d4-a716-446655440002', 'John Doe', 5, 'Kaliteli malzeme kullanılmış, işçilik çok iyi. Memnun kaldım.', true, 13, CURRENT_TIMESTAMP - INTERVAL '4 days', CURRENT_TIMESTAMP - INTERVAL '4 days'),
(gen_random_uuid(), '57bb1451-d63d-4daa-8d23-8fb315f6da21', '550e8400-e29b-41d4-a716-446655440003', 'Jane Smith', 4, 'Çok pratik ve kullanışlı. Günlük hayatta çok işime yarıyor.', true, 6, CURRENT_TIMESTAMP - INTERVAL '11 days', CURRENT_TIMESTAMP - INTERVAL '11 days'),
(gen_random_uuid(), '57bb1451-d63d-4daa-8d23-8fb315f6da21', '550e8400-e29b-41d4-a716-446655440004', 'Premium Customer', 5, NULL, true, 0, CURRENT_TIMESTAMP - INTERVAL '19 days', CURRENT_TIMESTAMP - INTERVAL '19 days'),
(gen_random_uuid(), '57bb1451-d63d-4daa-8d23-8fb315f6da21', '550e8400-e29b-41d4-a716-446655440005', 'Test User', 4, NULL, true, 0, CURRENT_TIMESTAMP - INTERVAL '28 days', CURRENT_TIMESTAMP - INTERVAL '28 days'),
(gen_random_uuid(), '57bb1451-d63d-4daa-8d23-8fb315f6da21', '550e8400-e29b-41d4-a716-446655440001', 'Admin User', 5, NULL, true, 0, CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP - INTERVAL '2 days')
ON CONFLICT (product_id, user_id) DO NOTHING;

-- NOT: Bu script sadece 2 ürün için örnek. Tüm ürünler için review eklemek için:
-- 1. Product-service'den tüm product ID'lerini alın
-- 2. Her ürün için 5-15 arası review ekleyin
-- 3. Her review için 5-10 arası yorum ekleyin



