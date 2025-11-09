package com.microservices.notification.Event;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Order Status Changed Event
 * 
 * Order Service'den gelen sipariş durumu değişikliği event'i
 * Notification Service bu event'i dinleyerek kullanıcıya bildirim gönderecek
 * 
 * Not: Order Service'deki OrderStatusChangedEvent ile aynı yapıda olmalı
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

