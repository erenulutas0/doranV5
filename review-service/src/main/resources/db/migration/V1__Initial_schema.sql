-- Review Service - Initial Schema
-- Migration: V1__Initial_schema.sql
-- Description: Creates the reviews table

-- UUID extension'ı etkinleştir (gen_random_uuid() için)
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE IF NOT EXISTS reviews (
    id UUID PRIMARY KEY,
    product_id UUID NOT NULL,
    user_id UUID NOT NULL,
    user_name VARCHAR(100) NOT NULL,
    rating INTEGER NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment VARCHAR(1000),
    is_approved BOOLEAN DEFAULT TRUE,
    helpful_count INTEGER DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- Indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_reviews_product_id ON reviews(product_id);
CREATE INDEX IF NOT EXISTS idx_reviews_user_id ON reviews(user_id);
CREATE INDEX IF NOT EXISTS idx_reviews_product_approved ON reviews(product_id, is_approved);
CREATE INDEX IF NOT EXISTS idx_reviews_rating ON reviews(rating);
CREATE INDEX IF NOT EXISTS idx_reviews_created_at ON reviews(created_at DESC);

-- Unique constraint: Bir kullanıcı bir ürün için sadece bir yorum yapabilir
CREATE UNIQUE INDEX IF NOT EXISTS idx_reviews_product_user_unique ON reviews(product_id, user_id);

