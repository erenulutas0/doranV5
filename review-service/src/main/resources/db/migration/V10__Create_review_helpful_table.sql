-- Migration: Create review_helpful table to track which users marked which reviews as helpful
-- This allows us to track real user accounts that liked reviews
-- visitor_id: String format user ID (e.g., "user_123456")

-- Create review_helpful table
CREATE TABLE IF NOT EXISTS review_helpful (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    review_id UUID NOT NULL,
    visitor_id VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- A user can only mark a review as helpful once
    CONSTRAINT uk_review_helpful_review_visitor UNIQUE (review_id, visitor_id),
    
    -- Foreign key to reviews table
    CONSTRAINT fk_review_helpful_review 
        FOREIGN KEY (review_id) 
        REFERENCES reviews(id) 
        ON DELETE CASCADE
);

-- Create index for faster lookups
CREATE INDEX IF NOT EXISTS idx_review_helpful_review_id 
    ON review_helpful(review_id);
CREATE INDEX IF NOT EXISTS idx_review_helpful_visitor_id 
    ON review_helpful(visitor_id);
CREATE INDEX IF NOT EXISTS idx_review_helpful_created_at 
    ON review_helpful(created_at DESC);

-- Update helpful_count in reviews table based on review_helpful table
-- This ensures data consistency
CREATE OR REPLACE FUNCTION update_review_helpful_count()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE reviews
    SET helpful_count = (
        SELECT COUNT(*) 
        FROM review_helpful 
        WHERE review_id = COALESCE(NEW.review_id, OLD.review_id)
    )
    WHERE id = COALESCE(NEW.review_id, OLD.review_id);
    RETURN COALESCE(NEW, OLD);
END;
$$ LANGUAGE plpgsql;

-- Create trigger to automatically update helpful_count
DROP TRIGGER IF EXISTS trg_update_review_helpful_count ON review_helpful;
CREATE TRIGGER trg_update_review_helpful_count
    AFTER INSERT OR DELETE ON review_helpful
    FOR EACH ROW
    EXECUTE FUNCTION update_review_helpful_count();

-- Note: visitor_id stores string format user IDs from the frontend
-- This allows compatibility with different authentication systems

COMMENT ON TABLE review_helpful IS 'Tracks which users marked which reviews as helpful';
COMMENT ON COLUMN review_helpful.review_id IS 'Reference to the review that was marked as helpful';
COMMENT ON COLUMN review_helpful.visitor_id IS 'User/visitor ID in string format (e.g., user_123456)';
COMMENT ON COLUMN review_helpful.created_at IS 'When the user marked the review as helpful';

