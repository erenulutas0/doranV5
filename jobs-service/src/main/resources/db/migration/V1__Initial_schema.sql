-- Jobs Service - Initial Schema
-- Migration: V1__Initial_schema.sql
-- Description: Creates the job_listings table

CREATE TABLE IF NOT EXISTS job_listings (
    id UUID PRIMARY KEY,
    owner_id UUID NOT NULL,
    owner_type VARCHAR(20) NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    category VARCHAR(50) NOT NULL,
    job_type VARCHAR(20) NOT NULL,
    salary_min DECIMAL(19, 2),
    salary_max DECIMAL(19, 2),
    salary_currency VARCHAR(10),
    location VARCHAR(200),
    city VARCHAR(100),
    is_remote BOOLEAN DEFAULT false,
    experience_level VARCHAR(20),
    required_skills TEXT, -- JSON array string
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    is_active BOOLEAN NOT NULL DEFAULT true,
    application_deadline TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    published_at TIMESTAMP,
    deleted_at TIMESTAMP,
    CONSTRAINT chk_owner_type CHECK (owner_type IN ('USER', 'SHOP')),
    CONSTRAINT chk_job_type CHECK (job_type IN ('FULL_TIME', 'PART_TIME', 'CONTRACT', 'INTERNSHIP', 'FREELANCE')),
    CONSTRAINT chk_experience_level CHECK (experience_level IS NULL OR experience_level IN ('ENTRY', 'JUNIOR', 'MID', 'SENIOR', 'LEAD')),
    CONSTRAINT chk_status CHECK (status IN ('DRAFT', 'PUBLISHED', 'CLOSED', 'DELETED')),
    CONSTRAINT chk_salary CHECK (salary_min IS NULL OR salary_min >= 0),
    CONSTRAINT chk_salary_max CHECK (salary_max IS NULL OR salary_max >= 0),
    CONSTRAINT chk_salary_range CHECK (salary_min IS NULL OR salary_max IS NULL OR salary_min <= salary_max)
);

-- Indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_job_listings_owner_id ON job_listings(owner_id);
CREATE INDEX IF NOT EXISTS idx_job_listings_category ON job_listings(category);
CREATE INDEX IF NOT EXISTS idx_job_listings_job_type ON job_listings(job_type);
CREATE INDEX IF NOT EXISTS idx_job_listings_city ON job_listings(city);
CREATE INDEX IF NOT EXISTS idx_job_listings_status ON job_listings(status);
CREATE INDEX IF NOT EXISTS idx_job_listings_is_active ON job_listings(is_active);
CREATE INDEX IF NOT EXISTS idx_job_listings_published_at ON job_listings(published_at DESC);
CREATE INDEX IF NOT EXISTS idx_job_listings_application_deadline ON job_listings(application_deadline);
CREATE INDEX IF NOT EXISTS idx_job_listings_deleted_at ON job_listings(deleted_at);

-- Composite index for common queries
CREATE INDEX IF NOT EXISTS idx_job_listings_published_active ON job_listings(status, is_active, deleted_at, application_deadline) 
WHERE status = 'PUBLISHED' AND is_active = true AND deleted_at IS NULL;

-- Full-text search index (PostgreSQL)
CREATE INDEX IF NOT EXISTS idx_job_listings_search ON job_listings USING gin(to_tsvector('english', title || ' ' || description || ' ' || category));

