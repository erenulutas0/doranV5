package com.microservices.notification.Model;

/**
 * Notification Status Enum
 * 
 * Bildirim durumlarını temsil eder:
 * - PENDING: Beklemede, henüz gönderilmedi
 * - SENT: Gönderildi
 * - FAILED: Gönderilemedi (hata oluştu)
 * - DELIVERED: Teslim edildi (e-posta/SMS için)
 * - READ: Okundu (in-app için)
 */
public enum NotificationStatus {
    PENDING,
    SENT,
    FAILED,
    DELIVERED,
    READ
}

