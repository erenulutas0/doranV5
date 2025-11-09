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
-- 
-- Sonraki adımlar:
--   1. Her servisin pom.xml'ine PostgreSQL dependency ekle
--   2. Her servisin application.yaml'ını PostgreSQL için güncelle
--   3. Servisleri başlat ve test et
-- 
-- ============================================

