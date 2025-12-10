-- Own Product Service - Performance Indexes
-- Migration: V3__Add_performance_indexes.sql
-- Description: User products için performans indeksleri

-- User ID bazlı sorgular için (user'ın ürünleri)
CREATE INDEX IF NOT EXISTS idx_products_user ON user_products(user_id, deleted_at, created_at DESC);

-- Status filtering için
CREATE INDEX IF NOT EXISTS idx_products_status ON user_products(status, deleted_at) WHERE deleted_at IS NULL;

-- Visibility filtering için
CREATE INDEX IF NOT EXISTS idx_products_visibility ON user_products(visibility, deleted_at) WHERE deleted_at IS NULL;

-- Category filtering için
CREATE INDEX IF NOT EXISTS idx_products_category ON user_products(category, status, visibility, deleted_at) WHERE deleted_at IS NULL;

-- Published products için composite index (most common query)
CREATE INDEX IF NOT EXISTS idx_products_published ON user_products(status, visibility, published_at DESC) 
    WHERE status = 'PUBLISHED' AND visibility = 'PUBLIC' AND deleted_at IS NULL;

-- Location searching için
CREATE INDEX IF NOT EXISTS idx_products_location ON user_products(location, status, visibility) 
    WHERE status = 'PUBLISHED' AND visibility = 'PUBLIC' AND deleted_at IS NULL;

-- Price range filtering için
CREATE INDEX IF NOT EXISTS idx_products_price ON user_products(price) 
    WHERE status = 'PUBLISHED' AND visibility = 'PUBLIC' AND deleted_at IS NULL;

-- Full-text search için (name ve description)
CREATE INDEX IF NOT EXISTS idx_products_search ON user_products USING GIN(to_tsvector('english', name || ' ' || COALESCE(description, ''))) 
    WHERE status = 'PUBLISHED' AND visibility = 'PUBLIC' AND deleted_at IS NULL;

-- Composite index for category + location
CREATE INDEX IF NOT EXISTS idx_products_category_location ON user_products(category, location, status, visibility) WHERE deleted_at IS NULL;

-- Published date için sorting
CREATE INDEX IF NOT EXISTS idx_products_published_at ON user_products(published_at DESC) WHERE deleted_at IS NULL;

-- Created date için sorting
CREATE INDEX IF NOT EXISTS idx_products_created_at ON user_products(created_at DESC) WHERE deleted_at IS NULL;

-- Soft delete check için
CREATE INDEX IF NOT EXISTS idx_products_deleted ON user_products(deleted_at);

