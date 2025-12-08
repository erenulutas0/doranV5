-- Jobs Service - Add Location Coordinates
-- Migration: V3__Add_location_coordinates.sql
-- Description: Adds latitude and longitude columns to job_listings table for geospatial queries

-- Add latitude and longitude columns
ALTER TABLE job_listings 
ADD COLUMN IF NOT EXISTS latitude DECIMAL(10, 7),
ADD COLUMN IF NOT EXISTS longitude DECIMAL(10, 7);

-- Add constraints for valid coordinates
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'chk_job_latitude' AND conrelid = 'job_listings'::regclass
    ) THEN
        ALTER TABLE job_listings ADD CONSTRAINT chk_job_latitude CHECK (latitude IS NULL OR (latitude >= -90.0 AND latitude <= 90.0));
    END IF;
    
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'chk_job_longitude' AND conrelid = 'job_listings'::regclass
    ) THEN
        ALTER TABLE job_listings ADD CONSTRAINT chk_job_longitude CHECK (longitude IS NULL OR (longitude >= -180.0 AND longitude <= 180.0));
    END IF;
END $$;

-- Add index for location queries
CREATE INDEX IF NOT EXISTS idx_job_listings_location_coords ON job_listings(latitude, longitude) 
WHERE latitude IS NOT NULL AND longitude IS NOT NULL;

-- Update existing jobs with coordinates based on city
UPDATE job_listings 
SET latitude = CASE 
    WHEN city = 'Istanbul' THEN 41.0082
    WHEN city = 'Ankara' THEN 39.9334
    WHEN city = 'Izmir' THEN 38.4237
    ELSE NULL
END,
longitude = CASE 
    WHEN city = 'Istanbul' THEN 28.9784
    WHEN city = 'Ankara' THEN 32.8597
    WHEN city = 'Izmir' THEN 27.1428
    ELSE NULL
END
WHERE latitude IS NULL AND longitude IS NULL AND city IS NOT NULL AND city != 'Remote';


