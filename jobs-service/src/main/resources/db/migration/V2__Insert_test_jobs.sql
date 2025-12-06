-- Jobs Service - Test Job Listings
-- Migration: V2__Insert_test_jobs.sql
-- Description: Inserts test job listings with realistic data

-- Test user IDs from user-service (employers)
-- Shop IDs from shop-service (for shop-owned jobs)

INSERT INTO job_listings (id, owner_id, owner_type, title, description, job_type, category, location, city, is_remote, salary_min, salary_max, salary_currency, experience_level, status, application_deadline, created_at, updated_at)
VALUES
    -- Full-time Software Developer Jobs
    ('11111111-1111-1111-1111-111111111111', '550e8400-e29b-41d4-a716-446655440002', 'USER', 'Senior Full Stack Developer', 'React, Node.js ve PostgreSQL deneyimi olan senior developer arıyoruz. Remote çalışma imkanı. Startup ortamında hızlı gelişim fırsatı.', 'FULL_TIME', 'Software Development', 'Istanbul, Turkey', 'Istanbul', true, 50000, 80000, 'TRY', 'SENIOR', 'PUBLISHED', CURRENT_TIMESTAMP + INTERVAL '30 days', CURRENT_TIMESTAMP - INTERVAL '5 days', CURRENT_TIMESTAMP),
    
    ('22222222-2222-2222-2222-222222222222', '550e8400-e29b-41d4-a716-446655440003', 'USER', 'Frontend Developer - React', 'React ve TypeScript bilen frontend developer. Modern UI/UX tasarımları geliştirecek. Hybrid çalışma modeli.', 'FULL_TIME', 'Software Development', 'Ankara, Turkey', 'Ankara', false, 35000, 55000, 'TRY', 'MID', 'PUBLISHED', CURRENT_TIMESTAMP + INTERVAL '25 days', CURRENT_TIMESTAMP - INTERVAL '4 days', CURRENT_TIMESTAMP),
    
    -- Part-time Jobs
    ('33333333-3333-3333-3333-333333333333', '550e8400-e29b-41d4-a716-446655440004', 'USER', 'Part-time Satış Danışmanı', 'Hafta sonları çalışacak satış danışmanı arıyoruz. Müşteri ilişkileri güçlü, dinamik kişiler. Esnek çalışma saatleri.', 'PART_TIME', 'Sales', 'Ankara, Turkey', 'Ankara', false, 8000, 12000, 'TRY', 'ENTRY', 'PUBLISHED', CURRENT_TIMESTAMP + INTERVAL '20 days', CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP),
    
    ('44444444-4444-4444-4444-444444444444', '550e8400-e29b-41d4-a716-446655440005', 'USER', 'Yarı Zamanlı Kasiyer', 'Hafta içi akşamları çalışacak kasiyer. Öğrenciler için ideal. Hızlı öğrenen, sorumluluk sahibi.', 'PART_TIME', 'Retail', 'Istanbul, Turkey', 'Istanbul', false, 7000, 10000, 'TRY', 'ENTRY', 'PUBLISHED', CURRENT_TIMESTAMP + INTERVAL '15 days', CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP),
    
    -- Contract Jobs
    ('55555555-5555-5555-5555-555555555555', '550e8400-e29b-41d4-a716-446655440006', 'USER', 'Freelance Grafik Tasarımcı', 'Logo ve marka kimliği tasarımı yapacak freelance tasarımcı. 3 aylık proje. Remote çalışma.', 'CONTRACT', 'Design', 'Remote', 'Remote', true, 15000, 25000, 'TRY', 'MID', 'PUBLISHED', CURRENT_TIMESTAMP + INTERVAL '45 days', CURRENT_TIMESTAMP - INTERVAL '6 days', CURRENT_TIMESTAMP),
    
    -- Internship
    ('66666666-6666-6666-6666-666666666666', '550e8400-e29b-41d4-a716-446655440007', 'USER', 'Yazılım Stajyeri', 'Yazılım geliştirme alanında staj yapmak isteyen öğrenciler. React ve Node.js öğrenecek. Mentor desteği sağlanacak.', 'INTERNSHIP', 'Software Development', 'Istanbul, Turkey', 'Istanbul', false, 5000, 8000, 'TRY', 'ENTRY', 'PUBLISHED', CURRENT_TIMESTAMP + INTERVAL '60 days', CURRENT_TIMESTAMP - INTERVAL '7 days', CURRENT_TIMESTAMP),
    
    -- Marketing Jobs
    ('77777777-7777-7777-7777-777777777777', '550e8400-e29b-41d4-a716-446655440008', 'USER', 'Digital Marketing Specialist', 'Sosyal medya yönetimi ve dijital pazarlama kampanyaları yürütecek uzman. Google Ads ve Facebook Ads deneyimi gerekli.', 'FULL_TIME', 'Marketing', 'Ankara, Turkey', 'Ankara', true, 30000, 45000, 'TRY', 'MID', 'PUBLISHED', CURRENT_TIMESTAMP + INTERVAL '28 days', CURRENT_TIMESTAMP - INTERVAL '8 days', CURRENT_TIMESTAMP),
    
    -- Customer Service
    ('88888888-8888-8888-8888-888888888888', '550e8400-e29b-41d4-a716-446655440009', 'USER', 'Müşteri Hizmetleri Temsilcisi', 'Müşteri sorunlarını çözecek, telefon ve e-posta desteği sağlayacak temsilci. İyi iletişim becerileri gerekli.', 'FULL_TIME', 'Customer Service', 'Izmir, Turkey', 'Izmir', true, 20000, 30000, 'TRY', 'ENTRY', 'PUBLISHED', CURRENT_TIMESTAMP + INTERVAL '22 days', CURRENT_TIMESTAMP - INTERVAL '9 days', CURRENT_TIMESTAMP),
    
    -- Shop-owned Jobs
    ('99999999-9999-9999-9999-999999999999', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'SHOP', 'Mağaza Müdürü', 'TechZone İstanbul mağazamız için deneyimli mağaza müdürü arıyoruz. Ekip yönetimi ve satış deneyimi gerekli.', 'FULL_TIME', 'Management', 'Istanbul, Turkey', 'Istanbul', false, 40000, 60000, 'TRY', 'SENIOR', 'PUBLISHED', CURRENT_TIMESTAMP + INTERVAL '35 days', CURRENT_TIMESTAMP - INTERVAL '10 days', CURRENT_TIMESTAMP),
    
    -- Expired Job
    ('00000000-0000-0000-0000-000000000000', '550e8400-e29b-41d4-a716-446655440010', 'USER', 'Süresi Dolmuş İlan', 'Bu ilanın başvuru süresi dolmuş.', 'FULL_TIME', 'Software Development', 'Istanbul, Turkey', 'Istanbul', false, 30000, 50000, 'TRY', 'MID', 'CLOSED', CURRENT_TIMESTAMP - INTERVAL '5 days', CURRENT_TIMESTAMP - INTERVAL '30 days', CURRENT_TIMESTAMP - INTERVAL '1 day');
