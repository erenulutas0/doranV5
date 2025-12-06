-- Own Product Service - Test User Products
-- Migration: V2__Insert_test_user_products.sql
-- Description: Inserts test user products (second-hand items, personal sales)

-- Test user IDs from user-service
-- Media IDs from media-service (product images)

INSERT INTO user_products (id, user_id, name, description, price, category, status, visibility, main_image_id, created_at, updated_at)
VALUES
    -- Electronics (John Doe's products)
    ('11111111-1111-1111-1111-111111111111', '550e8400-e29b-41d4-a716-446655440002', 'MacBook Pro 2019 - İkinci El', 'MacBook Pro 13 inch, 256GB SSD, 8GB RAM. Çok iyi durumda, kutulu ve garantili. Sadece 2 yıl kullanıldı.', 8500.00, 'Electronics', 'PUBLISHED', 'PUBLIC', 'b1c2d3e4-f5g6-7890-bcde-fg1234567890', CURRENT_TIMESTAMP - INTERVAL '20 days', CURRENT_TIMESTAMP - INTERVAL '5 days'),
    ('11111111-1111-1111-1111-111111111112', '550e8400-e29b-41d4-a716-446655440002', 'iPhone 13 Pro - Az Kullanılmış', 'iPhone 13 Pro 128GB, Graphite renk. Ekran koruyucu ve kılıf dahil. %95 batarya sağlığı.', 12000.00, 'Electronics', 'PUBLISHED', 'PUBLIC', 'b1c2d3e4-f5g6-7890-bcde-fg1234567891', CURRENT_TIMESTAMP - INTERVAL '15 days', CURRENT_TIMESTAMP - INTERVAL '3 days'),
    
    -- Electronics (Jane Smith's products)
    ('22222222-2222-2222-2222-222222222221', '550e8400-e29b-41d4-a716-446655440003', 'Sony WH-1000XM4 Kulaklık', 'Sony noise cancelling kulaklık. Mükemmel durumda, tüm aksesuarları mevcut.', 2500.00, 'Electronics', 'PUBLISHED', 'PUBLIC', 'b1c2d3e4-f5g6-7890-bcde-fg1234567892', CURRENT_TIMESTAMP - INTERVAL '18 days', CURRENT_TIMESTAMP - INTERVAL '7 days'),
    ('22222222-2222-2222-2222-222222222222', '550e8400-e29b-41d4-a716-446655440003', 'iPad Air 4. Nesil', 'iPad Air 64GB, Wi-Fi. Çizim için kullanıldı, ekran koruyucu takılı. Apple Pencil ile birlikte satılıyor.', 5500.00, 'Electronics', 'PUBLISHED', 'PUBLIC', NULL, CURRENT_TIMESTAMP - INTERVAL '12 days', CURRENT_TIMESTAMP - INTERVAL '2 days'),
    
    -- Books (Alice Williams)
    ('33333333-3333-3333-3333-333333333331', '550e8400-e29b-41d4-a716-446655440006', 'Clean Code - Robert C. Martin', 'Yazılım geliştirme klasikleri. Çok iyi durumda, hiç not alınmamış.', 150.00, 'Books', 'PUBLISHED', 'PUBLIC', NULL, CURRENT_TIMESTAMP - INTERVAL '10 days', CURRENT_TIMESTAMP - INTERVAL '1 day'),
    ('33333333-3333-3333-3333-333333333332', '550e8400-e29b-41d4-a716-446655440006', 'Design Patterns Kitabı Seti', 'Gang of Four Design Patterns kitabı ve ek kaynaklar. Set halinde satılıyor.', 300.00, 'Books', 'PUBLISHED', 'PUBLIC', NULL, CURRENT_TIMESTAMP - INTERVAL '8 days', CURRENT_TIMESTAMP),
    
    -- Clothing (Bob Johnson)
    ('44444444-4444-4444-4444-444444444441', '550e8400-e29b-41d4-a716-446655440007', 'Nike Air Max 90 - 42 Numara', 'Nike Air Max 90, siyah-beyaz. Çok az giyildi, orijinal kutusu mevcut.', 1200.00, 'Clothing', 'PUBLISHED', 'PUBLIC', NULL, CURRENT_TIMESTAMP - INTERVAL '14 days', CURRENT_TIMESTAMP - INTERVAL '4 days'),
    ('44444444-4444-4444-4444-444444444442', '550e8400-e29b-41d4-a716-446655440007', 'Levi''s 501 Jeans - 32/32', 'Klasik Levi''s 501, mavi. Yıkanmış ama çok az giyilmiş.', 400.00, 'Clothing', 'PUBLISHED', 'PUBLIC', NULL, CURRENT_TIMESTAMP - INTERVAL '11 days', CURRENT_TIMESTAMP - INTERVAL '6 days'),
    
    -- Furniture (Charlie Brown)
    ('55555555-5555-5555-5555-555555555551', '550e8400-e29b-41d4-a716-446655440008', 'IKEA KALLAX Kitaplık', 'IKEA KALLAX 4x4 kitaplık, beyaz. Montajlı ve kullanıma hazır. Taşıma dahil.', 800.00, 'Furniture', 'PUBLISHED', 'PUBLIC', NULL, CURRENT_TIMESTAMP - INTERVAL '9 days', CURRENT_TIMESTAMP - INTERVAL '3 days'),
    ('55555555-5555-5555-5555-555555555552', '550e8400-e29b-41d4-a716-446655440008', 'Çalışma Masası - Ahşap', 'Ahşap çalışma masası, 120x60cm. Çok iyi durumda, küçük çizikler var.', 600.00, 'Furniture', 'PUBLISHED', 'PUBLIC', NULL, CURRENT_TIMESTAMP - INTERVAL '7 days', CURRENT_TIMESTAMP - INTERVAL '1 day'),
    
    -- Sports Equipment (Diana Prince)
    ('66666666-6666-6666-6666-666666666661', '550e8400-e29b-41d4-a716-446655440009', 'Yoga Matı - Premium', 'Premium yoga matı, kalın ve kaymaz. Çok az kullanıldı, temiz.', 200.00, 'Sports', 'PUBLISHED', 'PUBLIC', NULL, CURRENT_TIMESTAMP - INTERVAL '6 days', CURRENT_TIMESTAMP),
    ('66666666-6666-6666-6666-666666666662', '550e8400-e29b-41d4-a716-446655440009', 'Dumbbell Seti - 2x5kg', 'Çift taraflı dumbbell seti, 2 adet 5kg. Evde kullanım için ideal.', 350.00, 'Sports', 'PUBLISHED', 'PUBLIC', NULL, CURRENT_TIMESTAMP - INTERVAL '5 days', CURRENT_TIMESTAMP),
    
    -- Musical Instruments (Emma Watson)
    ('77777777-7777-7777-7777-777777777771', '550e8400-e29b-41d4-a716-446655440010', 'Yamaha PSR-E373 Klavye', 'Yamaha dijital klavye, 61 tuş. Başlangıç seviyesi için ideal. Kulaklık ve adaptör dahil.', 1800.00, 'Musical Instruments', 'PUBLISHED', 'PUBLIC', NULL, CURRENT_TIMESTAMP - INTERVAL '4 days', CURRENT_TIMESTAMP),
    ('77777777-7777-7777-7777-777777777772', '550e8400-e29b-41d4-a716-446655440010', 'Gitar - Klasik', 'Klasik gitar, yeni başlayanlar için. Çanta ve pena dahil.', 800.00, 'Musical Instruments', 'PUBLISHED', 'PUBLIC', NULL, CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP),
    
    -- Draft products (not published)
    ('88888888-8888-8888-8888-888888888881', '550e8400-e29b-41d4-a716-446655440011', 'Draft Ürün - Henüz Yayınlanmadı', 'Bu ürün henüz taslak durumunda.', 1000.00, 'Electronics', 'DRAFT', 'PRIVATE', NULL, CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP),
    
    -- Sold products
    ('99999999-9999-9999-9999-999999999991', '550e8400-e29b-41d4-a716-446655440012', 'Satılan Ürün Örneği', 'Bu ürün satıldı.', 500.00, 'Electronics', 'SOLD', 'PUBLIC', NULL, CURRENT_TIMESTAMP - INTERVAL '30 days', CURRENT_TIMESTAMP - INTERVAL '10 days');

