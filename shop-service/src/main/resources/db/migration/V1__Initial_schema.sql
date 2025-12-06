-- Shop Service - Initial Schema
-- Migration: V1__Initial_schema.sql
-- Description: Creates the shops table

CREATE TABLE IF NOT EXISTS shops (
    id UUID PRIMARY KEY,
    owner_id UUID NOT NULL,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    category VARCHAR(50) NOT NULL,
    address VARCHAR(500) NOT NULL,
    city VARCHAR(100) NOT NULL,
    district VARCHAR(100),
    postal_code VARCHAR(10),
    phone VARCHAR(20),
    email VARCHAR(255),
    website VARCHAR(500),
    latitude DECIMAL(10, 7),
    longitude DECIMAL(10, 7),
    opening_time TIME,
    closing_time TIME,
    working_days TEXT, -- JSON array string
    logo_image_id UUID,
    cover_image_id UUID,
    average_rating DECIMAL(3, 2) DEFAULT 0.00,
    review_count INTEGER DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    CONSTRAINT chk_latitude CHECK (latitude IS NULL OR (latitude >= -90.0 AND latitude <= 90.0)),
    CONSTRAINT chk_longitude CHECK (longitude IS NULL OR (longitude >= -180.0 AND longitude <= 180.0)),
    CONSTRAINT chk_rating CHECK (average_rating IS NULL OR (average_rating >= 0.0 AND average_rating <= 5.0))
);

-- Indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_shops_owner_id ON shops(owner_id);
CREATE INDEX IF NOT EXISTS idx_shops_category ON shops(category);
CREATE INDEX IF NOT EXISTS idx_shops_city ON shops(city);
CREATE INDEX IF NOT EXISTS idx_shops_is_active ON shops(is_active);
CREATE INDEX IF NOT EXISTS idx_shops_average_rating ON shops(average_rating DESC);
CREATE INDEX IF NOT EXISTS idx_shops_deleted_at ON shops(deleted_at);
CREATE INDEX IF NOT EXISTS idx_shops_location ON shops(latitude, longitude) WHERE latitude IS NOT NULL AND longitude IS NOT NULL;

-- Composite index for common queries
CREATE INDEX IF NOT EXISTS idx_shops_active_category ON shops(is_active, category, deleted_at) 
WHERE is_active = true AND deleted_at IS NULL;

-- Full-text search index (PostgreSQL)
CREATE INDEX IF NOT EXISTS idx_shops_search ON shops USING gin(to_tsvector('english', name || ' ' || COALESCE(description, '') || ' ' || category));

