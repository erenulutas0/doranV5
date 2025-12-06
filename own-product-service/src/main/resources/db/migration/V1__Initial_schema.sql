-- Own Product Service - Initial Schema
-- Migration: V1__Initial_schema.sql
-- Description: Creates the user_products table

CREATE TABLE IF NOT EXISTS user_products (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    price DECIMAL(19, 2) NOT NULL,
    category VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    visibility VARCHAR(20) NOT NULL DEFAULT 'PUBLIC',
    location VARCHAR(100),
    contact_info VARCHAR(255),
    primary_image_id UUID,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    published_at TIMESTAMP,
    deleted_at TIMESTAMP,
    CONSTRAINT chk_status CHECK (status IN ('DRAFT', 'PUBLISHED', 'SOLD', 'DELETED')),
    CONSTRAINT chk_visibility CHECK (visibility IN ('PUBLIC', 'PRIVATE')),
    CONSTRAINT chk_price CHECK (price >= 0.01)
);

-- Indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_user_products_user_id ON user_products(user_id);
CREATE INDEX IF NOT EXISTS idx_user_products_status ON user_products(status);
CREATE INDEX IF NOT EXISTS idx_user_products_category ON user_products(category);
CREATE INDEX IF NOT EXISTS idx_user_products_published_at ON user_products(published_at DESC);
CREATE INDEX IF NOT EXISTS idx_user_products_deleted_at ON user_products(deleted_at);
CREATE INDEX IF NOT EXISTS idx_user_products_status_visibility ON user_products(status, visibility);

-- Composite index for common queries
CREATE INDEX IF NOT EXISTS idx_user_products_published_public ON user_products(status, visibility, deleted_at) 
WHERE status = 'PUBLISHED' AND visibility = 'PUBLIC' AND deleted_at IS NULL;

