-- Media Service - Performance Indexes
-- Migration: V3__Add_performance_indexes.sql
-- Description: Media files için performans indeksleri

-- Entity bazlı sorgular için (en yaygın query)
-- Bir entity'nin tüm medyalarını getir
CREATE INDEX IF NOT EXISTS idx_media_entity ON media_files(entity_type, entity_id, uploaded_at DESC);

-- Uploader bazlı sorgular için (user'ın upload ettiği medyalar)
CREATE INDEX IF NOT EXISTS idx_media_uploader ON media_files(uploaded_by, uploaded_at DESC);

-- File type filtering için (images, videos, etc.)
CREATE INDEX IF NOT EXISTS idx_media_type ON media_files(file_type);

-- Status filtering için
CREATE INDEX IF NOT EXISTS idx_media_status ON media_files(status, uploaded_at DESC);

-- File size için (large files query)
CREATE INDEX IF NOT EXISTS idx_media_size ON media_files(file_size);

-- Original filename searching için
CREATE INDEX IF NOT EXISTS idx_media_filename ON media_files(original_filename);

-- Composite index for entity + type (e.g., "get all images for this shop")
CREATE INDEX IF NOT EXISTS idx_media_entity_type ON media_files(entity_type, entity_id, file_type, status);

-- Created/uploaded date için sorting
CREATE INDEX IF NOT EXISTS idx_media_uploaded_at ON media_files(uploaded_at DESC);

