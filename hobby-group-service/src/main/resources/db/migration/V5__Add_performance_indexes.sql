-- Hobby Group Service - Performance Indexes
-- Migration: V5__Add_performance_indexes.sql
-- Description: Hobby groups için performans indeksleri

-- Category filtering (Sports, Arts, Music, etc.)
CREATE INDEX IF NOT EXISTS idx_hobby_category ON hobby_groups(category) WHERE status = 'ACTIVE' AND is_active = true;

-- Status filtering (ACTIVE, FULL, etc.)
CREATE INDEX IF NOT EXISTS idx_hobby_status ON hobby_groups(status) WHERE is_active = true;

-- Location searching için
CREATE INDEX IF NOT EXISTS idx_hobby_location ON hobby_groups(location) WHERE status = 'ACTIVE' AND is_active = true;

-- Member count filtering için (groups with space available)
CREATE INDEX IF NOT EXISTS idx_hobby_members ON hobby_groups(member_count, max_members) WHERE status = 'ACTIVE' AND is_active = true;

-- Creator bazlı sorgular için
CREATE INDEX IF NOT EXISTS idx_hobby_creator ON hobby_groups(creator_id) WHERE is_active = true;

-- Tags için GIN index (TEXT column için full-text search)
-- Tags JSON array string üzerinde arama
CREATE INDEX IF NOT EXISTS idx_hobby_tags ON hobby_groups USING GIN(to_tsvector('english', COALESCE(tags, ''))) WHERE status = 'ACTIVE' AND is_active = true;

-- Full-text search için (name ve description)
CREATE INDEX IF NOT EXISTS idx_hobby_search ON hobby_groups USING GIN(to_tsvector('english', name || ' ' || COALESCE(description, ''))) WHERE status = 'ACTIVE';

-- Composite index for category + location (common query)
CREATE INDEX IF NOT EXISTS idx_hobby_category_location ON hobby_groups(category, location, status) WHERE is_active = true;

-- Created date için sorting
CREATE INDEX IF NOT EXISTS idx_hobby_created_at ON hobby_groups(created_at DESC);

-- Member availability için (groups not full)
CREATE INDEX IF NOT EXISTS idx_hobby_available ON hobby_groups(status, member_count, max_members) WHERE is_active = true AND member_count < max_members;

