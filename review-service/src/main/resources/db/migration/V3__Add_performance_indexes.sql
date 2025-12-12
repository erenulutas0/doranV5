-- Review Service - Performance Indexes
-- Migration: V3__Add_performance_indexes.sql
-- Description: Reviews için performans indeksleri

-- Product ID bazlı sorgular için (en yaygın query)
-- Approved reviews, created_at DESC sıralaması ile
CREATE INDEX IF NOT EXISTS idx_reviews_product ON reviews(product_id, is_approved, created_at DESC);

-- User ID bazlı sorgular için (user'ın tüm yorumları)
CREATE INDEX IF NOT EXISTS idx_reviews_user ON reviews(user_id, created_at DESC);

-- Product + User unique constraint index (duplicate review prevention)
CREATE UNIQUE INDEX IF NOT EXISTS idx_reviews_product_user ON reviews(product_id, user_id) WHERE is_approved = true;

-- Rating bazlı sorgular için (rating distribution queries)
CREATE INDEX IF NOT EXISTS idx_reviews_product_rating ON reviews(product_id, rating) WHERE is_approved = true;

-- Helpful count sorting için (most helpful reviews)
CREATE INDEX IF NOT EXISTS idx_reviews_helpful ON reviews(product_id, helpful_count DESC) WHERE is_approved = true;

-- Pending approval için (admin panel)
CREATE INDEX IF NOT EXISTS idx_reviews_pending ON reviews(is_approved, created_at DESC);

-- Composite index for product + approved + rating (rating summary queries)
CREATE INDEX IF NOT EXISTS idx_reviews_summary ON reviews(product_id, is_approved, rating);

-- Full-text search için (review content searching)
CREATE INDEX IF NOT EXISTS idx_reviews_search ON reviews USING GIN(to_tsvector('english', COALESCE(comment, ''))) WHERE is_approved = true;

-- Created date için sorting
CREATE INDEX IF NOT EXISTS idx_reviews_created_at ON reviews(created_at DESC);

