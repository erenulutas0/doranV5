package com.microservices.notification.Model;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Notification Entity
 * 
 * Bildirim bilgilerini tutar:
 * - Kime gönderilecek (recipient)
 * - Ne tür bildirim (type: EMAIL, SMS, PUSH, IN_APP)
 * - Başlık ve içerik
 * - Durum (status: PENDING, SENT, FAILED, vb.)
 * - Gönderim zamanı
 * 
 * Özellikler:
 * - UUID ile unique ID
 * - Otomatik timestamp (createdAt, updatedAt, sentAt)
 * - Validation ile veri bütünlüğü
 */
@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @NotBlank(message = "Recipient is required")
    @Size(min = 3, max = 255, message = "Recipient must be between 3 and 255 characters")
    @Column(name = "recipient", nullable = false, length = 255)
    private String recipient;  // E-posta adresi, telefon numarası veya user ID
    // Not: @Email validation'ı kaldırıldı çünkü SMS için telefon numarası da geçerli

    @NotNull(message = "Type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private NotificationType type;  // EMAIL, SMS, PUSH, IN_APP

    @NotBlank(message = "Subject is required")
    @Size(min = 3, max = 255, message = "Subject must be between 3 and 255 characters")
    @Column(name = "subject", nullable = false, length = 255)
    private String subject;  // Bildirim başlığı

    @NotBlank(message = "Message is required")
    @Size(min = 10, max = 5000, message = "Message must be between 10 and 5000 characters")
    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;  // Bildirim içeriği

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private NotificationStatus status;  // PENDING, SENT, FAILED, DELIVERED, READ

    @Column(name = "user_id")
    private UUID userId;  // Hangi kullanıcıya gönderildi (opsiyonel)

    @Column(name = "related_entity_type", length = 50)
    private String relatedEntityType;  // ORDER, PRODUCT, USER, vb.

    @Column(name = "related_entity_id")
    private UUID relatedEntityId;  // İlgili entity'nin ID'si

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;  // Hata mesajı (FAILED durumunda)

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;  // Gönderim zamanı

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;  // Teslim zamanı

    @Column(name = "read_at")
    private LocalDateTime readAt;  // Okunma zamanı (in-app için)

    /**
     * Entity veritabanına kaydedilmeden önce UUID ve timestamp oluştur
     */
    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        // İlk oluşturulduğunda status PENDING
        if (status == null) {
            status = NotificationStatus.PENDING;
        }
    }

    /**
     * Entity güncellenmeden önce updatedAt'i güncelle
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public NotificationStatus getStatus() {
        return status;
    }

    public void setStatus(NotificationStatus status) {
        this.status = status;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getRelatedEntityType() {
        return relatedEntityType;
    }

    public void setRelatedEntityType(String relatedEntityType) {
        this.relatedEntityType = relatedEntityType;
    }

    public UUID getRelatedEntityId() {
        return relatedEntityId;
    }

    public void setRelatedEntityId(UUID relatedEntityId) {
        this.relatedEntityId = relatedEntityId;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public LocalDateTime getDeliveredAt() {
        return deliveredAt;
    }

    public void setDeliveredAt(LocalDateTime deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    public LocalDateTime getReadAt() {
        return readAt;
    }

    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }
}

