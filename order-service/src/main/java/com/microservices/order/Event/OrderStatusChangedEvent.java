package com.microservices.order.Event;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Order Status Changed Event
 * 
 * Sipariş durumu değiştiğinde RabbitMQ'ya gönderilecek event
 * Notification Service bu event'i dinleyerek kullanıcıya bildirim gönderecek
 * 
 * Örnek durumlar:
 * - PENDING → CONFIRMED: "Siparişiniz onaylandı"
 * - CONFIRMED → PROCESSING: "Siparişiniz hazırlanıyor"
 * - PROCESSING → SHIPPED: "Siparişiniz kargoya verildi"
 * - SHIPPED → DELIVERED: "Siparişiniz teslim edildi"
 */
public class OrderStatusChangedEvent {
    
    private UUID orderId;
    private UUID userId;
    private String userEmail;
    private String userName;
    private String oldStatus;
    private String newStatus;
    private LocalDateTime changedAt;

    // Default constructor (Jackson için gerekli)
    public OrderStatusChangedEvent() {
    }

    // Constructor
    public OrderStatusChangedEvent(UUID orderId, UUID userId, String userEmail, 
                                 String userName, String oldStatus, String newStatus,
                                 LocalDateTime changedAt) {
        this.orderId = orderId;
        this.userId = userId;
        this.userEmail = userEmail;
        this.userName = userName;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.changedAt = changedAt;
    }

    // Getters and Setters
    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getOldStatus() {
        return oldStatus;
    }

    public void setOldStatus(String oldStatus) {
        this.oldStatus = oldStatus;
    }

    public String getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(String newStatus) {
        this.newStatus = newStatus;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(LocalDateTime changedAt) {
        this.changedAt = changedAt;
    }
}

