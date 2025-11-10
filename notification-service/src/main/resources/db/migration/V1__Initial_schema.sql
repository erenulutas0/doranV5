-- Notification Service - Initial Schema
-- Migration: V1__Initial_schema.sql
-- Description: Creates the notifications table

CREATE TABLE IF NOT EXISTS notifications (
    id UUID PRIMARY KEY,
    recipient VARCHAR(255) NOT NULL,
    type VARCHAR(20) NOT NULL,
    subject VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    status VARCHAR(20) NOT NULL,
    user_id UUID,
    related_entity_type VARCHAR(50),
    related_entity_id UUID,
    error_message TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    sent_at TIMESTAMP,
    delivered_at TIMESTAMP,
    read_at TIMESTAMP
);

-- Indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_notifications_recipient ON notifications(recipient);
CREATE INDEX IF NOT EXISTS idx_notifications_user_id ON notifications(user_id) WHERE user_id IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_notifications_status ON notifications(status);
CREATE INDEX IF NOT EXISTS idx_notifications_type ON notifications(type);
CREATE INDEX IF NOT EXISTS idx_notifications_related_entity ON notifications(related_entity_type, related_entity_id) WHERE related_entity_type IS NOT NULL AND related_entity_id IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_notifications_created_at ON notifications(created_at);

