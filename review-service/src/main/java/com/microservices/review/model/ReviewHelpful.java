package com.microservices.review.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ReviewHelpful Entity
 * Hangi kullanıcıların hangi yorumları beğendiğini takip eder
 */
@Entity
@Table(name = "review_helpful")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewHelpful implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    private UUID id;
    
    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
    
    /**
     * Yorum ID (reviews tablosuna referans)
     */
    @NotNull(message = "Review ID is required")
    private UUID reviewId;
    
    /**
     * Kullanıcı ID (user-service'den)
     * Bu kullanıcı bu yorumu beğenmiş
     * String olarak saklanır - farklı auth sistemleriyle uyumluluk için
     */
    @NotNull(message = "User ID is required")
    private String visitorId;
    
    /**
     * Beğenme Tarihi
     */
    private LocalDateTime createdAt;
}



