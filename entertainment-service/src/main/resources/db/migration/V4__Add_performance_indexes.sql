-- Entertainment Service - Performance Indexes
-- Migration: V4__Add_performance_indexes.sql
-- Description: Venues için performans indeksleri

-- Venue type filtering (RESTAURANT, CAFE, BAR, etc.)
CREATE INDEX IF NOT EXISTS idx_venues_type ON venues(venue_type) WHERE is_active = true;

-- Category filtering (Fine Dining, Coffee Shop, etc.)
CREATE INDEX IF NOT EXISTS idx_venues_category ON venues(category) WHERE is_active = true;

-- City bazlı venue search için
CREATE INDEX IF NOT EXISTS idx_venues_city ON venues(city) WHERE is_active = true;

-- District filtering için
CREATE INDEX IF NOT EXISTS idx_venues_district ON venues(district) WHERE is_active = true;

-- Rating sorting için (popular venues)
CREATE INDEX IF NOT EXISTS idx_venues_rating ON venues(average_rating DESC, review_count DESC) WHERE is_active = true;

-- Full-text search için (name ve description)
CREATE INDEX IF NOT EXISTS idx_venues_search ON venues USING GIN(to_tsvector('english', name || ' ' || COALESCE(description, ''))) WHERE is_active = true;

-- Composite index for type + city (very common query)
CREATE INDEX IF NOT EXISTS idx_venues_type_city ON venues(venue_type, city, is_active);

-- Composite index for category + rating (popular by category)
CREATE INDEX IF NOT EXISTS idx_venues_category_rating ON venues(category, average_rating DESC, is_active);

-- Created date için sorting
CREATE INDEX IF NOT EXISTS idx_venues_created_at ON venues(created_at DESC);

