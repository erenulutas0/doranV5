-- Hobby Group Service - Test Hobby Groups
-- Migration: V4__Insert_test_hobby_groups.sql
-- Description: Insert test hobby group data (minimal schema-compliant data)

-- Sample hobby groups
INSERT INTO hobby_groups (id, creator_id, name, description, category, location, rules, tags, member_count, max_members, status, is_active, created_at, updated_at) VALUES
(gen_random_uuid(), gen_random_uuid(), 'Istanbul Photography Club', 'Weekly photo walks around Istanbul. All skill levels welcome.', 'Arts & Photography', 'Istanbul', '["Respect others","No spam","Share your work","Give constructive feedback"]', '["photography","istanbul","photo-walks","beginner-friendly"]', 45, 100, 'ACTIVE', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(gen_random_uuid(), gen_random_uuid(), 'Ankara Book Club', 'Monthly book discussions. Currently reading Turkish literature.', 'Books & Literature', 'Ankara', '["Read the book","Be respectful","No spoilers"]', '["books","reading","literature","discussion"]', 28, 50, 'ACTIVE', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(gen_random_uuid(), gen_random_uuid(), 'Izmir Hiking Group', 'Weekend hiking trips around Izmir and surroundings.', 'Sports & Fitness', 'Izmir', '["Be on time","Bring your own water","No littering"]', '["hiking","outdoors","fitness","nature"]', 67, 80, 'ACTIVE', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(gen_random_uuid(), gen_random_uuid(), 'Turkish Language Exchange', 'Practice Turkish and English. Online and in-person meetups.', 'Language & Culture', 'Istanbul', '["Speak both languages","Help each other","Be patient"]', '["language","turkish","english","exchange"]', 92, 150, 'ACTIVE', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(gen_random_uuid(), gen_random_uuid(), 'Board Game Night', 'Weekly board game sessions. Bring your favorite games!', 'Games & Gaming', 'Ankara', '["Bring a game","Clean up after yourself","Have fun"]', '["boardgames","gaming","social","fun"]', 34, 40, 'ACTIVE', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

