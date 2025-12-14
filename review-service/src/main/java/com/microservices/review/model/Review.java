package com.microservices.review.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Review Entity
 * Ürün yorumları ve değerlendirmeleri için entity
 */
@Entity
@Table(name = "reviews")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Review implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    private UUID id;
    
    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * Ürün ID (product-service'den)
     */
    @NotNull(message = "Product ID is required")
    private UUID productId;
    
    /**
     * Kullanıcı ID (user-service'den)
     */
    @NotNull(message = "User ID is required")
    private UUID userId;
    
    /**
     * Kullanıcı Adı (cache için - user-service'den çekilebilir)
     */
    @NotBlank(message = "User name is required")
    @Size(max = 100, message = "User name must not exceed 100 characters")
    private String userName;
    
    /**
     * Rating (1-5 arası)
     */
    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private Integer rating;
    
    /**
     * Yorum Metni
     */
    @Size(max = 1000, message = "Comment must not exceed 1000 characters")
    private String comment;
    
    /**
     * Yorum Onaylandı mı? (Moderation için)
     */
    private Boolean isApproved = true;
    
    /**
     * Yardımcı Oldu Sayısı (Helpful votes)
     */
    private Integer helpfulCount = 0;
    
    /**
     * Oluşturulma Tarihi
     */
    private LocalDateTime createdAt;
    
    /**
     * Güncellenme Tarihi
     */
    private LocalDateTime updatedAt;
    
    /**
     * Kullanıcı bu yorumu beğenmiş mi? (Transient - database'de saklanmaz)
     * Sadece API response'unda kullanılır
     */
    @Transient
    private Boolean likedByUser = false;
}

