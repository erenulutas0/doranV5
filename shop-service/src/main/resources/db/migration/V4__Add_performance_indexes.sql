-- Shop Service - Performance Indexes
-- Migration: V4__Add_performance_indexes.sql
-- Description: Frequently queried columns için performans indeksleri

-- Category bazlı sorgular için (filtreleme)
CREATE INDEX IF NOT EXISTS idx_shops_category ON shops(category) WHERE is_active = true AND deleted_at IS NULL;

-- City bazlı sorgular için (location filtering)
CREATE INDEX IF NOT EXISTS idx_shops_city ON shops(city) WHERE is_active = true AND deleted_at IS NULL;

-- Status column yok; sadece is_active + deleted_at
CREATE INDEX IF NOT EXISTS idx_shops_active ON shops(is_active, deleted_at);

-- Location-based queries için spatial index (PostGIS)
-- ST_DWithin ve ST_Distance fonksiyonları için optimize edilmiş
CREATE INDEX IF NOT EXISTS idx_shops_location_gist ON shops USING GIST(location_point) WHERE is_active = true AND deleted_at IS NULL;

-- Created date için sorting ve pagination
CREATE INDEX IF NOT EXISTS idx_shops_created_at ON shops(created_at DESC) WHERE deleted_at IS NULL;

-- Owner bazlı sorgular için
CREATE INDEX IF NOT EXISTS idx_shops_owner ON shops(owner_id) WHERE deleted_at IS NULL;

-- Rating sorting için (average_rating DESC sıralaması yaygın)
CREATE INDEX IF NOT EXISTS idx_shops_rating ON shops(average_rating DESC, review_count DESC) WHERE is_active = true AND deleted_at IS NULL;

-- Full-text search için (shop name ve description)
-- GIN index ile text search optimize edilir
CREATE INDEX IF NOT EXISTS idx_shops_search ON shops USING GIN(to_tsvector('turkish', name || ' ' || COALESCE(description, ''))) WHERE is_active = true;

-- Composite index for category + city queries (very common)
CREATE INDEX IF NOT EXISTS idx_shops_category_city ON shops(category, city, is_active) WHERE deleted_at IS NULL;

