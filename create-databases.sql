-- ============================================
-- PostgreSQL Database Creation Script
-- Microservices için veritabanları oluşturur
-- ============================================
-- 
-- Kullanım (pgAdmin):
-- ⚠️ ÖNEMLİ: CREATE DATABASE komutları sadece SERVER SEVİYESİNDE çalışır!
-- 
-- 1. pgAdmin'de sol panelde "PostgreSQL 17" server'ına SAĞ TIKLAYIN
-- 2. "Query Tool" seçeneğini seçin (Tools > Query Tool DEĞİL!)
-- 3. Yeni açılan Query Tool'da tab başlığı "PostgreSQL 17" olmalı (veritabanı adı OLMAMALI)
-- 4. Query Tool'da "Auto-commit" modunu açın (✓ işaretli olmalı)
--    - Query Tool penceresinin alt kısmında "Auto-commit" checkbox'ını işaretleyin
-- 5. Bu script'i yapıştırın ve çalıştırın (F5)
-- 
-- ❌ YANLIŞ: Bir veritabanına bağlıyken (örn: postgres) Query Tool açmak
-- ✅ DOĞRU: Server seviyesinde (PostgreSQL 17'ye sağ tıklayarak) Query Tool açmak
-- 
-- Alternatif (psql komut satırı):
-- psql -U postgres -f create-databases.sql
-- 
-- ============================================

-- User Service Database
CREATE DATABASE user_db
    WITH 
    OWNER = postgres
    ENCODING = 'UTF8'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;

COMMENT ON DATABASE user_db IS 'User Service için veritabanı - Kullanıcı yönetimi';

-- Product Service Database
CREATE DATABASE product_db
    WITH 
    OWNER = postgres
    ENCODING = 'UTF8'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;

COMMENT ON DATABASE product_db IS 'Product Service için veritabanı - Ürün yönetimi';

-- Order Service Database
CREATE DATABASE order_db
    WITH 
    OWNER = postgres
    ENCODING = 'UTF8'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;

COMMENT ON DATABASE order_db IS 'Order Service için veritabanı - Sipariş yönetimi';

-- Inventory Service Database
CREATE DATABASE inventory_db
    WITH 
    OWNER = postgres
    ENCODING = 'UTF8'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;

COMMENT ON DATABASE inventory_db IS 'Inventory Service için veritabanı - Stok yönetimi';

-- Notification Service Database
CREATE DATABASE notification_db
    WITH 
    OWNER = postgres
    ENCODING = 'UTF8'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;

COMMENT ON DATABASE notification_db IS 'Notification Service için veritabanı - Bildirim yönetimi';

-- Review Service Database
CREATE DATABASE review_db
    WITH 
    OWNER = postgres
    ENCODING = 'UTF8'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;

COMMENT ON DATABASE review_db IS 'Review Service için veritabanı - Ürün/Servis değerlendirme';

-- Media Service Database
CREATE DATABASE media_db
    WITH 
    OWNER = postgres
    ENCODING = 'UTF8'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;

COMMENT ON DATABASE media_db IS 'Media Service için veritabanı - Dosya ve medya yönetimi';

-- Own Product Service Database
CREATE DATABASE own_product_db
    WITH 
    OWNER = postgres
    ENCODING = 'UTF8'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;

COMMENT ON DATABASE own_product_db IS 'Own Product Service için veritabanı - Kullanıcı ürün paylaşımı';

-- Shop Service Database
CREATE DATABASE shop_db
    WITH 
    OWNER = postgres
    ENCODING = 'UTF8'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;

COMMENT ON DATABASE shop_db IS 'Shop Service için veritabanı - Dükkan yönetimi';

-- Jobs Service Database
CREATE DATABASE jobs_db
    WITH 
    OWNER = postgres
    ENCODING = 'UTF8'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;

COMMENT ON DATABASE jobs_db IS 'Jobs Service için veritabanı - İş ilanları yönetimi';

-- Hobby Group Service Database
CREATE DATABASE hobby_group_db
    WITH 
    OWNER = postgres
    ENCODING = 'UTF8'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;

COMMENT ON DATABASE hobby_group_db IS 'Hobby Group Service için veritabanı - Hobi grupları yönetimi';

-- Entertainment Service Database
CREATE DATABASE entertainment_db
    WITH 
    OWNER = postgres
    ENCODING = 'UTF8'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;

COMMENT ON DATABASE entertainment_db IS 'Entertainment Service için veritabanı - Eğlence mekanları ve etkinlikler';

-- ============================================
-- Veritabanları başarıyla oluşturuldu!
-- ============================================
-- 
-- Oluşturulan veritabanları:
--   ✓ user_db
--   ✓ product_db
--   ✓ order_db
--   ✓ inventory_db
--   ✓ notification_db
--   ✓ review_db
--   ✓ media_db
-- 
-- Sonraki adımlar:
--   1. Her servisin pom.xml'ine PostgreSQL dependency ekle
--   2. Her servisin application.yaml'ını PostgreSQL için güncelle
--   3. Servisleri başlat ve test et
-- 
-- ============================================

