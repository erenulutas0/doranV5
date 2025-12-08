-- Entertainment Service - Add PostGIS Support
-- Migration: V2__Add_postgis_support.sql
-- Description: Adds PostGIS geography column and geospatial indexes for venues

-- Create PostGIS geography column for efficient geospatial queries
ALTER TABLE venues 
ADD COLUMN IF NOT EXISTS location_point GEOGRAPHY(POINT, 4326);

-- Create GIST index for geospatial queries (much faster than lat/lng comparison)
CREATE INDEX IF NOT EXISTS idx_venues_location_point ON venues USING GIST(location_point) 
WHERE location_point IS NOT NULL;

-- Function to update location_point when latitude/longitude changes
CREATE OR REPLACE FUNCTION update_venue_location_point()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.latitude IS NOT NULL AND NEW.longitude IS NOT NULL THEN
        NEW.location_point := ST_SetSRID(ST_MakePoint(NEW.longitude, NEW.latitude), 4326)::geography;
    ELSE
        NEW.location_point := NULL;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger to automatically update location_point
DROP TRIGGER IF EXISTS trigger_update_venue_location_point ON venues;
CREATE TRIGGER trigger_update_venue_location_point
    BEFORE INSERT OR UPDATE OF latitude, longitude ON venues
    FOR EACH ROW
    EXECUTE FUNCTION update_venue_location_point();

-- Update existing rows (if any) to populate location_point
UPDATE venues 
SET location_point = ST_SetSRID(ST_MakePoint(longitude, latitude), 4326)::geography
WHERE latitude IS NOT NULL AND longitude IS NOT NULL AND location_point IS NULL;





