-- Review Service - Insert Test Reviews
-- Migration: V7__Insert_test_reviews.sql
-- Description: Inserts test reviews for existing products

-- Test kullanıcı ID'leri (user-service'den - genel test kullanıcıları)
-- Bu ID'ler user-service migration'ındaki ID'lerle eşleşmeli
-- Eğer user-service'de farklı ID'ler varsa, burayı güncelleyin

-- Test reviews for products (her ürüne 2-5 arası review ekliyoruz)
-- Product ID'leri product-service'den alınan gerçek ID'ler

INSERT INTO reviews (id, product_id, user_id, user_name, rating, comment, is_approved, helpful_count, created_at, updated_at)
VALUES
    -- iPhone 15 Pro Max (385898fe-79f0-498f-9fc1-957902ba3dd1)
    (gen_random_uuid(), '385898fe-79f0-498f-9fc1-957902ba3dd1', gen_random_uuid(), 'Ahmet Yılmaz', 5, 'Harika bir telefon! Kamera kalitesi mükemmel, performans çok iyi. Kesinlikle tavsiye ederim.', true, 12, CURRENT_TIMESTAMP - INTERVAL '5 days', CURRENT_TIMESTAMP - INTERVAL '5 days'),
    (gen_random_uuid(), '385898fe-79f0-498f-9fc1-957902ba3dd1', gen_random_uuid(), 'Mehmet Demir', 5, 'Çok memnun kaldım. Hızlı, güvenilir ve kullanımı kolay. Fiyatına değer.', true, 8, CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP - INTERVAL '3 days'),
    (gen_random_uuid(), '385898fe-79f0-498f-9fc1-957902ba3dd1', gen_random_uuid(), 'Ayşe Kaya', 4, 'Güzel telefon ama fiyat biraz yüksek. Yine de kaliteli bir ürün.', true, 5, CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP - INTERVAL '2 days'),
    (gen_random_uuid(), '385898fe-79f0-498f-9fc1-957902ba3dd1', gen_random_uuid(), 'Fatma Öz', 5, 'Mükemmel! Her şeyi beğendim. Özellikle kamera ve ekran kalitesi harika.', true, 15, CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day'),
    
    -- Samsung Galaxy S24 Ultra (57bb1451-d63d-4daa-8d23-8fb315f6da21)
    (gen_random_uuid(), '57bb1451-d63d-4daa-8d23-8fb315f6da21', gen_random_uuid(), 'Ali Çelik', 5, 'S Pen özelliği harika! Çok kullanışlı bir telefon. Samsung kalitesi.', true, 10, CURRENT_TIMESTAMP - INTERVAL '4 days', CURRENT_TIMESTAMP - INTERVAL '4 days'),
    (gen_random_uuid(), '57bb1451-d63d-4daa-8d23-8fb315f6da21', gen_random_uuid(), 'Zeynep Arslan', 4, 'İyi bir telefon ama batarya biraz zayıf. Genel olarak memnunum.', true, 6, CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP - INTERVAL '2 days'),
    (gen_random_uuid(), '57bb1451-d63d-4daa-8d23-8fb315f6da21', gen_random_uuid(), 'Can Yıldız', 5, 'Kamera kalitesi muhteşem! 200MP gerçekten fark ediyor. Çok beğendim.', true, 9, CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day'),
    
    -- MacBook Pro 16" M3 Max (9601c1ec-79eb-41a6-9467-459498df0c43)
    (gen_random_uuid(), '9601c1ec-79eb-41a6-9467-459498df0c43', gen_random_uuid(), 'Burak Şahin', 5, 'Profesyonel işler için mükemmel! Performans harika, ekran kalitesi çok iyi.', true, 20, CURRENT_TIMESTAMP - INTERVAL '7 days', CURRENT_TIMESTAMP - INTERVAL '7 days'),
    (gen_random_uuid(), '9601c1ec-79eb-41a6-9467-459498df0c43', gen_random_uuid(), 'Elif Doğan', 5, 'Çok güçlü bir laptop. Video editing ve tasarım işleri için ideal.', true, 14, CURRENT_TIMESTAMP - INTERVAL '5 days', CURRENT_TIMESTAMP - INTERVAL '5 days'),
    (gen_random_uuid(), '9601c1ec-79eb-41a6-9467-459498df0c43', gen_random_uuid(), 'Emre Koç', 4, 'Fiyatı yüksek ama değer. M3 Max gerçekten güçlü bir işlemci.', true, 7, CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP - INTERVAL '3 days'),
    
    -- Sony WH-1000XM5 Headphones (86651487-863f-4dd8-9897-9f047187f8c9)
    (gen_random_uuid(), '86651487-863f-4dd8-9897-9f047187f8c9', gen_random_uuid(), 'Deniz Aydın', 5, 'Ses kalitesi harika! Noise cancellation özelliği çok etkili. Uzun yolculuklar için ideal.', true, 18, CURRENT_TIMESTAMP - INTERVAL '6 days', CURRENT_TIMESTAMP - INTERVAL '6 days'),
    (gen_random_uuid(), '86651487-863f-4dd8-9897-9f047187f8c9', gen_random_uuid(), 'Selin Yıldız', 5, 'Çok rahat ve kaliteli. Müzik dinlemek için mükemmel bir kulaklık.', true, 11, CURRENT_TIMESTAMP - INTERVAL '4 days', CURRENT_TIMESTAMP - INTERVAL '4 days'),
    (gen_random_uuid(), '86651487-863f-4dd8-9897-9f047187f8c9', gen_random_uuid(), 'Kerem Özdemir', 4, 'İyi bir kulaklık ama fiyat biraz yüksek. Yine de kalitesi fiyatına değer.', true, 5, CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP - INTERVAL '2 days'),
    
    -- iPad Pro 12.9" M2 (a3d30014-f31d-4f91-8fb8-a1459d008bcc)
    (gen_random_uuid(), 'a3d30014-f31d-4f91-8fb8-a1459d008bcc', gen_random_uuid(), 'Gizem Aktaş', 5, 'Tablet olarak mükemmel! Ekran kalitesi harika, Apple Pencil ile çok iyi çalışıyor.', true, 13, CURRENT_TIMESTAMP - INTERVAL '5 days', CURRENT_TIMESTAMP - INTERVAL '5 days'),
    (gen_random_uuid(), 'a3d30014-f31d-4f91-8fb8-a1459d008bcc', gen_random_uuid(), 'Onur Güneş', 5, 'Tasarım ve performans harika. Grafik tasarım işleri için çok uygun.', true, 9, CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP - INTERVAL '3 days'),
    
    -- Samsung 55" QLED TV (b66eee8d-3112-455f-8120-29c0fa47de06)
    (gen_random_uuid(), 'b66eee8d-3112-455f-8120-29c0fa47de06', gen_random_uuid(), 'Murat Kılıç', 5, 'Görüntü kalitesi muhteşem! 4K içerikler harika görünüyor. Çok memnunum.', true, 16, CURRENT_TIMESTAMP - INTERVAL '8 days', CURRENT_TIMESTAMP - INTERVAL '8 days'),
    (gen_random_uuid(), 'b66eee8d-3112-455f-8120-29c0fa47de06', gen_random_uuid(), 'Seda Yılmaz', 4, 'İyi bir TV ama ses kalitesi biraz zayıf. Görüntü kalitesi harika.', true, 6, CURRENT_TIMESTAMP - INTERVAL '4 days', CURRENT_TIMESTAMP - INTERVAL '4 days'),
    
    -- Nintendo Switch OLED (1a840a12-d2b6-46e9-8e5e-83fff7d872ea)
    (gen_random_uuid(), '1a840a12-d2b6-46e9-8e5e-83fff7d872ea', gen_random_uuid(), 'Berkay Çetin', 5, 'Harika bir konsol! OLED ekran gerçekten fark ediyor. Oyun deneyimi mükemmel.', true, 22, CURRENT_TIMESTAMP - INTERVAL '10 days', CURRENT_TIMESTAMP - INTERVAL '10 days'),
    (gen_random_uuid(), '1a840a12-d2b6-46e9-8e5e-83fff7d872ea', gen_random_uuid(), 'Ece Demir', 5, 'Çocuklar çok sevdi! Taşınabilir olması çok pratik. Kesinlikle tavsiye ederim.', true, 17, CURRENT_TIMESTAMP - INTERVAL '6 days', CURRENT_TIMESTAMP - INTERVAL '6 days'),
    (gen_random_uuid(), '1a840a12-d2b6-46e9-8e5e-83fff7d872ea', gen_random_uuid(), 'Tolga Özkan', 4, 'İyi bir konsol ama oyun fiyatları biraz yüksek. Yine de kaliteli bir ürün.', true, 8, CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP - INTERVAL '3 days'),
    
    -- PlayStation 5 Console (e261a332-161a-401b-8dc8-6be40d295f94)
    (gen_random_uuid(), 'e261a332-161a-401b-8dc8-6be40d295f94', gen_random_uuid(), 'Okan Yıldırım', 5, 'Grafikler harika! 4K gaming deneyimi muhteşem. PS5 gerçekten güçlü bir konsol.', true, 25, CURRENT_TIMESTAMP - INTERVAL '12 days', CURRENT_TIMESTAMP - INTERVAL '12 days'),
    (gen_random_uuid(), 'e261a332-161a-401b-8dc8-6be40d295f94', gen_random_uuid(), 'Derya Şen', 5, 'Çok memnun kaldım! DualSense kontrolcü çok iyi, oyun deneyimi harika.', true, 19, CURRENT_TIMESTAMP - INTERVAL '7 days', CURRENT_TIMESTAMP - INTERVAL '7 days'),
    (gen_random_uuid(), 'e261a332-161a-401b-8dc8-6be40d295f94', gen_random_uuid(), 'Hakan Aydın', 4, 'İyi bir konsol ama SSD kapasitesi biraz küçük. Yine de performans harika.', true, 10, CURRENT_TIMESTAMP - INTERVAL '4 days', CURRENT_TIMESTAMP - INTERVAL '4 days'),
    
    -- Xbox Series X (00e3c2a7-ce3e-4009-a792-8e73a6479566)
    (gen_random_uuid(), '00e3c2a7-ce3e-4009-a792-8e73a6479566', gen_random_uuid(), 'Serkan Kaya', 5, 'Game Pass ile birlikte harika bir değer! Performans çok iyi, geriye dönük uyumluluk mükemmel.', true, 21, CURRENT_TIMESTAMP - INTERVAL '9 days', CURRENT_TIMESTAMP - INTERVAL '9 days'),
    (gen_random_uuid(), '00e3c2a7-ce3e-4009-a792-8e73a6479566', gen_random_uuid(), 'Pınar Öztürk', 5, 'Çok hızlı yükleme süreleri! Quick Resume özelliği çok kullanışlı.', true, 14, CURRENT_TIMESTAMP - INTERVAL '5 days', CURRENT_TIMESTAMP - INTERVAL '5 days'),
    
    -- AirPods Pro 2 (5cde000d-af21-4966-8d6e-8cd25a0acd32)
    (gen_random_uuid(), '5cde000d-af21-4966-8d6e-8cd25a0acd32', gen_random_uuid(), 'Mert Aslan', 5, 'Ses kalitesi harika! Noise cancellation çok etkili. Apple ekosistemi ile mükemmel uyum.', true, 16, CURRENT_TIMESTAMP - INTERVAL '6 days', CURRENT_TIMESTAMP - INTERVAL '6 days'),
    (gen_random_uuid(), '5cde000d-af21-4966-8d6e-8cd25a0acd32', gen_random_uuid(), 'Nazlı Çakır', 4, 'İyi kulaklıklar ama fiyat biraz yüksek. Yine de kalite fiyatına değer.', true, 7, CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP - INTERVAL '3 days'),
    
    -- Diğer ürünlere de birkaç review ekleyelim
    -- DJI Mini 4 Pro Drone (8fd8c3a5-6572-45a3-aff3-8941223447ab)
    (gen_random_uuid(), '8fd8c3a5-6572-45a3-aff3-8941223447ab', gen_random_uuid(), 'Cem Özdemir', 5, 'Harika bir drone! 4K video kalitesi mükemmel, uçuş süresi yeterli.', true, 11, CURRENT_TIMESTAMP - INTERVAL '4 days', CURRENT_TIMESTAMP - INTERVAL '4 days'),
    
    -- Canon EOS R6 Mark II (1d9e0f90-07e8-46c5-b4e7-b4dc23ccfb03)
    (gen_random_uuid(), '1d9e0f90-07e8-46c5-b4e7-b4dc23ccfb03', gen_random_uuid(), 'Aslı Yılmaz', 5, 'Profesyonel fotoğrafçılık için mükemmel! Görüntü kalitesi harika.', true, 13, CURRENT_TIMESTAMP - INTERVAL '5 days', CURRENT_TIMESTAMP - INTERVAL '5 days'),
    
    -- GoPro Hero 12 (0c886b5f-62e8-4e3c-8b94-7cda94fd1400)
    (gen_random_uuid(), '0c886b5f-62e8-4e3c-8b94-7cda94fd1400', gen_random_uuid(), 'Barış Çelik', 5, 'Aksiyon kamerası olarak mükemmel! Su geçirmezlik özelliği harika çalışıyor.', true, 9, CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP - INTERVAL '3 days'),
    
    -- Samsung Galaxy Watch 6 (c373a688-3742-4fc5-8ef2-d30c780b8a70)
    (gen_random_uuid(), 'c373a688-3742-4fc5-8ef2-d30c780b8a70', gen_random_uuid(), 'İrem Şahin', 4, 'Sağlık takibi özellikleri çok iyi. Batarya ömrü yeterli.', true, 6, CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP - INTERVAL '2 days'),
    
    -- Apple Watch Series 9 (2c0e2b64-4701-418d-ba36-af5e7004a69b)
    (gen_random_uuid(), '2c0e2b64-4701-418d-ba36-af5e7004a69b', gen_random_uuid(), 'Kaan Doğan', 5, 'Mükemmel bir akıllı saat! Tüm özellikler harika çalışıyor.', true, 12, CURRENT_TIMESTAMP - INTERVAL '4 days', CURRENT_TIMESTAMP - INTERVAL '4 days');

