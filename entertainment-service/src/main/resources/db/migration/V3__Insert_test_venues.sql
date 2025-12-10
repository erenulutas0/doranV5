-- Entertainment Service - Test Venues
-- Migration: V3__Insert_test_venues.sql
-- Description: Insert test venue data (minimal for now - will be expanded later)

-- Sample venues with only schema-compliant columns
INSERT INTO venues (id, name, description, venue_type, category, address, city, district, phone, email, website, latitude, longitude, opening_time, closing_time, working_days, average_rating, review_count, is_active, created_at, updated_at) VALUES
(gen_random_uuid(), 'Mikla Restaurant', 'Contemporary Turkish cuisine with panoramic Istanbul views', 'RESTAURANT', 'Fine Dining', 'The Marmara Pera Hotel', 'Istanbul', 'Beyoğlu', '+90 212 293 5656', 'info@mikla.com', 'www.mikla.com', 41.0336, 28.9779, '18:00', '23:00', '["Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"]', 4.5, 234, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(gen_random_uuid(), 'Nardis Jazz Club', 'Premier jazz venue with live performances', 'CLUB', 'Jazz Club', 'Galata Kulesi Sokak No:14', 'Istanbul', 'Galata', '+90 212 244 6327', 'info@nardisjazz.com', 'www.nardisjazz.com', 41.0257, 28.9741, '21:00', '02:00', '["Wednesday","Thursday","Friday","Saturday"]', 4.8, 456, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(gen_random_uuid(), 'Manifesto Smyrna', 'Rooftop bar with Izmir bay views', 'BAR', 'Rooftop Bar', 'Cumhuriyet Bulvarı No:124', 'Izmir', 'Alsancak', '+90 232 421 0777', 'info@manifestosmyrna.com', 'www.manifestosmyrna.com', 38.4382, 27.1436, '17:00', '02:00', '["Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"]', 4.3, 189, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(gen_random_uuid(), 'Babylon Bomonti', 'Music venue and cultural hub', 'CONCERT_HALL', 'Live Music', 'Birahane Sokak No:1', 'Istanbul', 'Bomonti', '+90 212 334 0190', 'info@babylon.com.tr', 'www.babylon.com.tr', 41.0588, 28.9808, '20:00', '03:00', '["Friday","Saturday"]', 4.6, 567, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(gen_random_uuid(), 'Sortie', 'Upscale nightclub with Bosphorus views', 'CLUB', 'Nightclub', 'Muallim Naci Caddesi', 'Istanbul', 'Ortaköy', '+90 212 327 8585', 'info@sortie.com.tr', 'www.sortie.com.tr', 41.0554, 29.0269, '23:00', '04:00', '["Thursday","Friday","Saturday"]', 4.4, 892, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

