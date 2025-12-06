-- Shop Service - Test Shops
-- Migration: V2__Insert_test_shops.sql
-- Description: Inserts test shops with realistic data

-- Test user IDs from user-service (shop owners)
-- Media IDs from media-service (shop logos)

INSERT INTO shops (id, owner_id, name, description, category, address, city, district, phone, email, website, latitude, longitude, logo_image_id, is_active, average_rating, review_count, created_at, updated_at)
VALUES
    -- Tech Shops
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '550e8400-e29b-41d4-a716-446655440007', 'TechZone İstanbul', 'İstanbul''un en büyük teknoloji mağazası. Telefon, laptop, tablet ve aksesuarlarda en iyi fiyat garantisi.', 'Electronics', 'İstiklal Caddesi No:123, Beyoğlu', 'Istanbul', 'Beyoğlu', '+90 212 555 0101', 'info@techzone.com', 'https://www.techzone.com', 41.0369, 28.9850, NULL, true, 4.5, 127, CURRENT_TIMESTAMP - INTERVAL '180 days', CURRENT_TIMESTAMP - INTERVAL '1 day'),
    
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '550e8400-e29b-41d4-a716-446655440008', 'Fashion Store Ankara', 'Ankara''nın en şık giyim mağazası. Erkek, kadın ve çocuk koleksiyonları. Sezon sonu indirimler devam ediyor!', 'Fashion', 'Tunalı Hilmi Caddesi No:45, Çankaya', 'Ankara', 'Çankaya', '+90 312 555 0202', 'info@fashionstore.com', 'https://www.fashionstore.com', 39.9208, 32.8541, NULL, true, 4.3, 89, CURRENT_TIMESTAMP - INTERVAL '150 days', CURRENT_TIMESTAMP - INTERVAL '2 days'),
    
    -- Book Shops
    ('cccccccc-cccc-cccc-cccc-cccccccccccc', '550e8400-e29b-41d4-a716-446655440009', 'Kitap Evi İzmir', 'İzmir''in en kapsamlı kitapçısı. Yeni çıkanlar, klasikler ve akademik kitaplar. Online sipariş ve kapıda ödeme.', 'Books', 'Kordon Boyu No:78, Konak', 'Izmir', 'Konak', '+90 232 555 0303', 'info@kitapevi.com', 'https://www.kitapevi.com', 38.4237, 27.1428, NULL, true, 4.7, 203, CURRENT_TIMESTAMP - INTERVAL '120 days', CURRENT_TIMESTAMP - INTERVAL '3 days'),
    
    -- Sports Equipment
    ('dddddddd-dddd-dddd-dddd-dddddddddddd', '550e8400-e29b-41d4-a716-446655440010', 'Spor Market Antalya', 'Tüm spor malzemeleri tek yerde. Fitness, futbol, basketbol, tenis ekipmanları. Profesyonel danışmanlık hizmeti.', 'Sports', 'Lara Caddesi No:234, Muratpaşa', 'Antalya', 'Muratpaşa', '+90 242 555 0404', 'info@spormarket.com', 'https://www.spormarket.com', 36.8969, 30.7133, NULL, true, 4.4, 156, CURRENT_TIMESTAMP - INTERVAL '90 days', CURRENT_TIMESTAMP - INTERVAL '4 days'),
    
    -- Home & Garden
    ('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', '550e8400-e29b-41d4-a716-446655440011', 'Ev Dekorasyon Bursa', 'Ev dekorasyonunda her şey! Mobilya, aksesuar, perde, halı. Ücretsiz keşif ve montaj hizmeti.', 'Home & Garden', 'Fomara Caddesi No:56, Nilüfer', 'Bursa', 'Nilüfer', '+90 224 555 0505', 'info@evdekor.com', 'https://www.evdekor.com', 40.1826, 29.0665, NULL, true, 4.2, 98, CURRENT_TIMESTAMP - INTERVAL '60 days', CURRENT_TIMESTAMP - INTERVAL '5 days'),
    
    -- Beauty & Cosmetics
    ('ffffffff-ffff-ffff-ffff-ffffffffffff', '550e8400-e29b-41d4-a716-446655440012', 'Güzellik Merkezi İstanbul', 'Kozmetik ve kişisel bakım ürünleri. Orijinal ve garantili ürünler. Hızlı kargo ve kapıda ödeme.', 'Beauty', 'Bağdat Caddesi No:789, Kadıköy', 'Istanbul', 'Kadıköy', '+90 216 555 0606', 'info@guzellikmerkezi.com', 'https://www.guzellikmerkezi.com', 40.9819, 29.0222, NULL, true, 4.6, 234, CURRENT_TIMESTAMP - INTERVAL '45 days', CURRENT_TIMESTAMP - INTERVAL '6 days'),
    
    -- Pet Supplies
    ('11111111-1111-1111-1111-111111111100', '550e8400-e29b-41d4-a716-446655440013', 'Pet Shop Ankara', 'Kedi, köpek ve diğer evcil hayvanlar için her şey. Yem, oyuncak, aksesuar ve veteriner ürünleri.', 'Pets', 'Kızılay Meydanı No:12, Çankaya', 'Ankara', 'Çankaya', '+90 312 555 0707', 'info@petshop.com', 'https://www.petshop.com', 39.9208, 32.8541, NULL, true, 4.8, 167, CURRENT_TIMESTAMP - INTERVAL '30 days', CURRENT_TIMESTAMP - INTERVAL '7 days'),
    
    -- Musical Instruments
    ('22222222-2222-2222-2222-222222222200', '550e8400-e29b-41d4-a716-446655440014', 'Müzik Aletleri İzmir', 'Profesyonel ve amatör müzisyenler için enstrümanlar. Gitar, piyano, davul ve daha fazlası. Deneme imkanı.', 'Musical Instruments', 'Alsancak Caddesi No:345, Konak', 'Izmir', 'Konak', '+90 232 555 0808', 'info@muzikaletleri.com', 'https://www.muzikaletleri.com', 38.4237, 27.1428, NULL, true, 4.5, 112, CURRENT_TIMESTAMP - INTERVAL '20 days', CURRENT_TIMESTAMP - INTERVAL '8 days'),
    
    -- Inactive shop
    ('33333333-3333-3333-3333-333333333300', '550e8400-e29b-41d4-a716-446655440015', 'Kapalı Mağaza', 'Bu mağaza şu anda aktif değil.', 'Electronics', 'Test Adresi', 'Istanbul', 'Test', '+90 212 555 9999', 'closed@test.com', NULL, NULL, NULL, NULL, false, 0.0, 0, CURRENT_TIMESTAMP - INTERVAL '10 days', CURRENT_TIMESTAMP - INTERVAL '9 days');
