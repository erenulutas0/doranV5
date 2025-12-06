-- Hobby Group Service - Initial Schema
-- Migration: V1__Initial_schema.sql
-- Description: Creates the hobby_groups and group_memberships tables

CREATE TABLE IF NOT EXISTS hobby_groups (
    id UUID PRIMARY KEY,
    creator_id UUID NOT NULL,
    name VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    category VARCHAR(50) NOT NULL,
    location VARCHAR(100),
    rules TEXT, -- JSON array string
    tags TEXT, -- JSON array string
    image_id UUID,
    member_count INTEGER NOT NULL DEFAULT 0,
    max_members INTEGER,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    CONSTRAINT chk_status CHECK (status IN ('ACTIVE', 'INACTIVE', 'DELETED')),
    CONSTRAINT chk_max_members CHECK (max_members IS NULL OR max_members >= 1),
    CONSTRAINT chk_member_count CHECK (member_count >= 0)
);

CREATE TABLE IF NOT EXISTS group_memberships (
    id UUID PRIMARY KEY,
    group_id UUID NOT NULL,
    user_id UUID NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'MEMBER',
    joined_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    left_at TIMESTAMP,
    CONSTRAINT fk_group FOREIGN KEY (group_id) REFERENCES hobby_groups(id) ON DELETE CASCADE,
    CONSTRAINT chk_role CHECK (role IN ('CREATOR', 'ADMIN', 'MODERATOR', 'MEMBER')),
    CONSTRAINT uk_group_user UNIQUE (group_id, user_id)
);

-- Indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_hobby_groups_creator_id ON hobby_groups(creator_id);
CREATE INDEX IF NOT EXISTS idx_hobby_groups_category ON hobby_groups(category);
CREATE INDEX IF NOT EXISTS idx_hobby_groups_location ON hobby_groups(location);
CREATE INDEX IF NOT EXISTS idx_hobby_groups_status ON hobby_groups(status);
CREATE INDEX IF NOT EXISTS idx_hobby_groups_is_active ON hobby_groups(is_active);
CREATE INDEX IF NOT EXISTS idx_hobby_groups_member_count ON hobby_groups(member_count DESC);
CREATE INDEX IF NOT EXISTS idx_hobby_groups_deleted_at ON hobby_groups(deleted_at);

CREATE INDEX IF NOT EXISTS idx_group_memberships_group_id ON group_memberships(group_id);
CREATE INDEX IF NOT EXISTS idx_group_memberships_user_id ON group_memberships(user_id);
CREATE INDEX IF NOT EXISTS idx_group_memberships_left_at ON group_memberships(left_at);

-- Composite index for common queries
CREATE INDEX IF NOT EXISTS idx_hobby_groups_active ON hobby_groups(status, is_active, deleted_at) 
WHERE status = 'ACTIVE' AND is_active = true AND deleted_at IS NULL;

-- Full-text search index (PostgreSQL)
CREATE INDEX IF NOT EXISTS idx_hobby_groups_search ON hobby_groups USING gin(to_tsvector('english', name || ' ' || description || ' ' || category));

