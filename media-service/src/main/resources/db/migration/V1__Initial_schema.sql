-- Media Service Database Schema

CREATE TABLE IF NOT EXISTS media (
    id UUID PRIMARY KEY,
    file_name VARCHAR(255) NOT NULL,
    original_file_name VARCHAR(255) NOT NULL,
    content_type VARCHAR(100) NOT NULL,
    file_size BIGINT NOT NULL,
    file_extension VARCHAR(10) NOT NULL,
    media_type VARCHAR(20) NOT NULL,
    storage_type VARCHAR(20) NOT NULL,
    data TEXT,
    url VARCHAR(500),
    thumbnail_data TEXT,
    medium_data TEXT,
    uploaded_by UUID,
    uploaded_at TIMESTAMP NOT NULL,
    deleted_at TIMESTAMP,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT chk_media_type CHECK (media_type IN ('IMAGE', 'DOCUMENT', 'VIDEO', 'OTHER')),
    CONSTRAINT chk_storage_type CHECK (storage_type IN ('LOCAL', 'S3'))
);

-- Indexes for better query performance
CREATE INDEX idx_media_uploaded_by ON media(uploaded_by);
CREATE INDEX idx_media_media_type ON media(media_type);
CREATE INDEX idx_media_is_deleted ON media(is_deleted);
CREATE INDEX idx_media_uploaded_at ON media(uploaded_at DESC);
CREATE INDEX idx_media_storage_type ON media(storage_type);

-- Comments
COMMENT ON TABLE media IS 'Stores media files and metadata';
COMMENT ON COLUMN media.data IS 'Base64 encoded file data for local storage';
COMMENT ON COLUMN media.url IS 'S3 URL for cloud storage (future use)';
COMMENT ON COLUMN media.thumbnail_data IS 'Base64 encoded thumbnail for images';
COMMENT ON COLUMN media.medium_data IS 'Base64 encoded medium size for images';
COMMENT ON COLUMN media.is_deleted IS 'Soft delete flag';

