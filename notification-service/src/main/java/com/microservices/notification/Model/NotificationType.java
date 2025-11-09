package com.microservices.notification.Model;

/**
 * Notification Type Enum
 * 
 * Bildirim türlerini temsil eder:
 * - EMAIL: E-posta bildirimi
 * - SMS: SMS bildirimi
 * - PUSH: Push notification (mobil uygulama)
 * - IN_APP: Uygulama içi bildirim
 */
public enum NotificationType {
    EMAIL,
    SMS,
    PUSH,
    IN_APP
}

