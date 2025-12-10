-- Jobs Service - Performance Indexes
-- Migration: V5__Add_performance_indexes.sql
-- Description: Frequently queried columns için performans indeksleri

-- Category filtreleme için
CREATE INDEX IF NOT EXISTS idx_jobs_category ON job_listings(category) WHERE status = 'PUBLISHED' AND is_active = true;

-- City bazlı job search için
CREATE INDEX IF NOT EXISTS idx_jobs_city ON job_listings(city) WHERE status = 'PUBLISHED' AND is_active = true;

-- Job type filtreleme için (FULL_TIME, PART_TIME, etc.)
CREATE INDEX IF NOT EXISTS idx_jobs_type ON job_listings(job_type) WHERE status = 'PUBLISHED' AND is_active = true;

-- Experience level filtreleme için
CREATE INDEX IF NOT EXISTS idx_jobs_experience ON job_listings(experience_level) WHERE status = 'PUBLISHED' AND is_active = true;

-- Remote jobs filtering için
CREATE INDEX IF NOT EXISTS idx_jobs_remote ON job_listings(is_remote) WHERE status = 'PUBLISHED' AND is_active = true;

-- Status + published_at için composite index (most common query)
CREATE INDEX IF NOT EXISTS idx_jobs_status_published ON job_listings(status, published_at DESC, is_active);

-- Salary range filtering için
CREATE INDEX IF NOT EXISTS idx_jobs_salary ON job_listings(salary_min, salary_max, salary_currency) WHERE status = 'PUBLISHED' AND is_active = true;

-- Location text filtering için (city based)
CREATE INDEX IF NOT EXISTS idx_jobs_location ON job_listings(location) WHERE status = 'PUBLISHED' AND is_active = true;

-- Owner (company) bazlı sorgular için
CREATE INDEX IF NOT EXISTS idx_jobs_owner ON job_listings(owner_id, owner_type) WHERE is_active = true;

-- Application deadline için (expiring jobs query)
-- NOT: CURRENT_TIMESTAMP index predicate'de kullanılamaz (IMMUTABLE değil), sadece status ve is_active filtreledik
CREATE INDEX IF NOT EXISTS idx_jobs_deadline ON job_listings(application_deadline) WHERE status = 'PUBLISHED' AND is_active = true;

-- Full-text search için (title ve description)
CREATE INDEX IF NOT EXISTS idx_jobs_search ON job_listings USING GIN(to_tsvector('english', title || ' ' || COALESCE(description, ''))) WHERE status = 'PUBLISHED';

-- Composite index for category + city + type (very common filter combination)
CREATE INDEX IF NOT EXISTS idx_jobs_category_city_type ON job_listings(category, city, job_type, status) WHERE is_active = true;

-- Created date için sorting ve pagination
CREATE INDEX IF NOT EXISTS idx_jobs_created_at ON job_listings(created_at DESC);

