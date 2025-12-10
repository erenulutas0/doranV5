-- Entertainment Service - Initial Schema
-- Migration: V1__Initial_schema.sql
-- Description: Creates the venues and events tables

-- Enable PostGIS extension for geography type
CREATE EXTENSION IF NOT EXISTS postgis;

CREATE TABLE IF NOT EXISTS venues (
    id UUID PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    venue_type VARCHAR(50) NOT NULL,
    category VARCHAR(50),
    address VARCHAR(500) NOT NULL,
    city VARCHAR(100) NOT NULL,
    district VARCHAR(100),
    phone VARCHAR(20),
    email VARCHAR(255),
    website VARCHAR(500),
    latitude DECIMAL(10, 7),
    longitude DECIMAL(10, 7),
    opening_time TIME,
    closing_time TIME,
    working_days TEXT, -- JSON array string
    cover_image_id UUID,
    average_rating DECIMAL(3, 2) DEFAULT 0.00,
    review_count INTEGER DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    CONSTRAINT chk_venue_type CHECK (venue_type IN ('CAFE', 'BAR', 'CLUB', 'RESTAURANT', 'THEATER', 'CINEMA', 'CONCERT_HALL', 'SPORTS_VENUE', 'PARK', 'MUSEUM', 'GALLERY', 'OTHER')),
    CONSTRAINT chk_latitude CHECK (latitude IS NULL OR (latitude >= -90.0 AND latitude <= 90.0)),
    CONSTRAINT chk_longitude CHECK (longitude IS NULL OR (longitude >= -180.0 AND longitude <= 180.0)),
    CONSTRAINT chk_rating CHECK (average_rating IS NULL OR (average_rating >= 0.0 AND average_rating <= 5.0))
);

CREATE TABLE IF NOT EXISTS events (
    id UUID PRIMARY KEY,
    venue_id UUID NOT NULL,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    category VARCHAR(50),
    start_date_time TIMESTAMP NOT NULL,
    end_date_time TIMESTAMP,
    ticket_price DECIMAL(19, 2),
    max_capacity INTEGER,
    image_id UUID,
    status VARCHAR(20) NOT NULL DEFAULT 'UPCOMING',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    CONSTRAINT fk_venue FOREIGN KEY (venue_id) REFERENCES venues(id) ON DELETE CASCADE,
    CONSTRAINT chk_status CHECK (status IN ('UPCOMING', 'ONGOING', 'COMPLETED', 'CANCELLED', 'DELETED')),
    CONSTRAINT chk_ticket_price CHECK (ticket_price IS NULL OR ticket_price >= 0),
    CONSTRAINT chk_max_capacity CHECK (max_capacity IS NULL OR max_capacity >= 1)
);

-- Indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_venues_venue_type ON venues(venue_type);
CREATE INDEX IF NOT EXISTS idx_venues_city ON venues(city);
CREATE INDEX IF NOT EXISTS idx_venues_is_active ON venues(is_active);
CREATE INDEX IF NOT EXISTS idx_venues_average_rating ON venues(average_rating DESC);
CREATE INDEX IF NOT EXISTS idx_venues_deleted_at ON venues(deleted_at);
CREATE INDEX IF NOT EXISTS idx_venues_location ON venues(latitude, longitude) WHERE latitude IS NOT NULL AND longitude IS NOT NULL;

CREATE INDEX IF NOT EXISTS idx_events_venue_id ON events(venue_id);
CREATE INDEX IF NOT EXISTS idx_events_start_date_time ON events(start_date_time);
CREATE INDEX IF NOT EXISTS idx_events_status ON events(status);
CREATE INDEX IF NOT EXISTS idx_events_deleted_at ON events(deleted_at);

-- Composite index for common queries
CREATE INDEX IF NOT EXISTS idx_venues_active ON venues(is_active, deleted_at) 
WHERE is_active = true AND deleted_at IS NULL;

CREATE INDEX IF NOT EXISTS idx_events_upcoming ON events(status, deleted_at, start_date_time) 
WHERE status IN ('UPCOMING', 'ONGOING') AND deleted_at IS NULL;

-- Full-text search indexes (PostgreSQL)
CREATE INDEX IF NOT EXISTS idx_venues_search ON venues USING gin(to_tsvector('english', name || ' ' || COALESCE(description, '') || ' ' || COALESCE(category, '')));
CREATE INDEX IF NOT EXISTS idx_events_search ON events USING gin(to_tsvector('english', name || ' ' || COALESCE(description, '')));

