-- Media Service - Test Media Files
-- Migration: V2__Insert_test_media.sql
-- Description: Inserts test media files (Base64 encoded placeholder images)
-- Note: In production, these would be actual uploaded files

-- Base64 encoded 1x1 transparent PNG (minimal size for testing)
-- This is a placeholder - real images would be much larger
INSERT INTO media (id, file_name, original_file_name, content_type, file_size, file_extension, media_type, storage_type, data, uploaded_by, uploaded_at, is_deleted)
VALUES
    -- Profile pictures and avatars
    ('a1b2c3d4-e5f6-7890-abcd-ef1234567890', 'avatar1.jpg', 'profile-john.jpg', 'image/jpeg', 45678, 'jpg', 'IMAGE', 'LOCAL', 'iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg==', '550e8400-e29b-41d4-a716-446655440002', CURRENT_TIMESTAMP - INTERVAL '30 days', false),
    ('a1b2c3d4-e5f6-7890-abcd-ef1234567891', 'avatar2.jpg', 'profile-jane.jpg', 'image/jpeg', 52341, 'jpg', 'IMAGE', 'LOCAL', 'iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg==', '550e8400-e29b-41d4-a716-446655440003', CURRENT_TIMESTAMP - INTERVAL '25 days', false),
    ('a1b2c3d4-e5f6-7890-abcd-ef1234567892', 'avatar3.jpg', 'profile-alice.jpg', 'image/jpeg', 48923, 'jpg', 'IMAGE', 'LOCAL', 'iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg==', '550e8400-e29b-41d4-a716-446655440006', CURRENT_TIMESTAMP - INTERVAL '20 days', false),
    
    -- Product images
    ('b1c2d3e4-f5g6-7890-bcde-fg1234567890', 'product1.jpg', 'laptop-image.jpg', 'image/jpeg', 234567, 'jpg', 'IMAGE', 'LOCAL', 'iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg==', '550e8400-e29b-41d4-a716-446655440002', CURRENT_TIMESTAMP - INTERVAL '15 days', false),
    ('b1c2d3e4-f5g6-7890-bcde-fg1234567891', 'product2.jpg', 'phone-image.jpg', 'image/jpeg', 198234, 'jpg', 'IMAGE', 'LOCAL', 'iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg==', '550e8400-e29b-41d4-a716-446655440003', CURRENT_TIMESTAMP - INTERVAL '12 days', false),
    ('b1c2d3e4-f5g6-7890-bcde-fg1234567892', 'product3.jpg', 'headphones-image.jpg', 'image/jpeg', 156789, 'jpg', 'IMAGE', 'LOCAL', 'iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg==', '550e8400-e29b-41d4-a716-446655440004', CURRENT_TIMESTAMP - INTERVAL '10 days', false),
    
    -- Shop logos
    ('c1d2e3f4-g5h6-7890-cdef-gh1234567890', 'shop-logo1.png', 'tech-shop-logo.png', 'image/png', 34567, 'png', 'IMAGE', 'LOCAL', 'iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg==', '550e8400-e29b-41d4-a716-446655440007', CURRENT_TIMESTAMP - INTERVAL '8 days', false),
    ('c1d2e3f4-g5h6-7890-cdef-gh1234567891', 'shop-logo2.png', 'fashion-store-logo.png', 'image/png', 41234, 'png', 'IMAGE', 'LOCAL', 'iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg==', '550e8400-e29b-41d4-a716-446655440008', CURRENT_TIMESTAMP - INTERVAL '7 days', false),
    
    -- Event images
    ('d1e2f3g4-h5i6-7890-defg-hi1234567890', 'event1.jpg', 'concert-poster.jpg', 'image/jpeg', 567890, 'jpg', 'IMAGE', 'LOCAL', 'iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg==', '550e8400-e29b-41d4-a716-446655440009', CURRENT_TIMESTAMP - INTERVAL '5 days', false),
    ('d1e2f3g4-h5i6-7890-defg-hi1234567891', 'event2.jpg', 'workshop-banner.jpg', 'image/jpeg', 445678, 'jpg', 'IMAGE', 'LOCAL', 'iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg==', '550e8400-e29b-41d4-a716-446655440010', CURRENT_TIMESTAMP - INTERVAL '4 days', false),
    
    -- Hobby group images
    ('e1f2g3h4-i5j6-7890-efgh-ij1234567890', 'group1.jpg', 'photography-club.jpg', 'image/jpeg', 334567, 'jpg', 'IMAGE', 'LOCAL', 'iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg==', '550e8400-e29b-41d4-a716-446655440011', CURRENT_TIMESTAMP - INTERVAL '3 days', false),
    ('e1f2g3h4-i5j6-7890-efgh-ij1234567891', 'group2.jpg', 'music-band.jpg', 'image/jpeg', 389012, 'jpg', 'IMAGE', 'LOCAL', 'iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg==', '550e8400-e29b-41d4-a716-446655440012', CURRENT_TIMESTAMP - INTERVAL '2 days', false),
    
    -- Documents (CVs, portfolios)
    ('f1g2h3i4-j5k6-7890-fghi-jk1234567890', 'cv1.pdf', 'john-doe-cv.pdf', 'application/pdf', 234567, 'pdf', 'DOCUMENT', 'LOCAL', 'JVBERi0xLjQKJeLjz9MKMSAwIG9iago8PC9UeXBlL0NhdGFsb2cvUGFnZXMgMiAwIFI+PgplbmRvYmoKMiAwIG9iago8PC9UeXBlL1BhZ2VzL0tpZHNbMyAwIFJdL0NvdW50IDE+PgplbmRvYmoKMyAwIG9iago8PC9UeXBlL1BhZ2UvTWVkaWFCb3hbMCAwIDYxMiA3OTJdL1Jlc291cmNlczw8L1Byb2NTZXRbL1BERi9UZXh0XT4+L0NvbnRlbnRzIDQgMCBSPj4KZW5kb2JqCjQgMCBvYmoKPDwvTGVuZ3RoIDQ4Pj4Kc3RyZWFtCkJUCi9GMSAxMiBUZgoxMCA3MDAgVGQKKEhlbGxvIFdvcmxkKSBUagpFVAplbmRzdHJlYW0KZW5kb2JqCnhyZWYKMCA1CjAwMDAwMDAwMDAgNjU1MzUgZiAKMDAwMDAwMDAwOSAwMDAwMCBuIAowMDAwMDAwMDU4IDAwMDAwIG4gCjAwMDAwMDAxMTUgMDAwMDAgbiAKMDAwMDAwMDI3MCAwMDAwMCBuIAp0cmFpbGVyCjw8L1NpemUgNS9Sb290IDEgMCBSPj4Kc3RhcnR4cmVmCjM0NQolJUVPRgo=', '550e8400-e29b-41d4-a716-446655440002', CURRENT_TIMESTAMP - INTERVAL '1 day', false),
    ('f1g2h3i4-j5k6-7890-fghi-jk1234567891', 'cv2.pdf', 'jane-smith-cv.pdf', 'application/pdf', 198765, 'pdf', 'DOCUMENT', 'LOCAL', 'JVBERi0xLjQKJeLjz9MKMSAwIG9iago8PC9UeXBlL0NhdGFsb2cvUGFnZXMgMiAwIFI+PgplbmRvYmoKMiAwIG9iago8PC9UeXBlL1BhZ2VzL0tpZHNbMyAwIFJdL0NvdW50IDE+PgplbmRvYmoKMyAwIG9iago8PC9UeXBlL1BhZ2UvTWVkaWFCb3hbMCAwIDYxMiA3OTJdL1Jlc291cmNlczw8L1Byb2NTZXRbL1BERi9UZXh0XT4+L0NvbnRlbnRzIDQgMCBSPj4KZW5kb2JqCjQgMCBvYmoKPDwvTGVuZ3RoIDQ4Pj4Kc3RyZWFtCkJUCi9GMSAxMiBUZgoxMCA3MDAgVGQKKEhlbGxvIFdvcmxkKSBUagpFVAplbmRzdHJlYW0KZW5kb2JqCnhyZWYKMCA1CjAwMDAwMDAwMDAgNjU1MzUgZiAKMDAwMDAwMDAwOSAwMDAwMCBuIAowMDAwMDAwMDU4IDAwMDAwIG4gCjAwMDAwMDAxMTUgMDAwMDAgbiAKMDAwMDAwMDI3MCAwMDAwMCBuIAp0cmFpbGVyCjw8L1NpemUgNS9Sb290IDEgMCBSPj4Kc3RhcnR4cmVmCjM0NQolJUVPRgo=', '550e8400-e29b-41d4-a716-446655440003', CURRENT_TIMESTAMP - INTERVAL '1 day', false);

-- Note: Thumbnail and medium data would be generated automatically by the service
-- These are just placeholder records for testing

