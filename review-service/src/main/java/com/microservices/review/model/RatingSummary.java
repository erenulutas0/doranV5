package com.microservices.review.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Rating Summary DTO
 * Ürün için özet rating bilgileri
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatingSummary implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private UUID productId;
    
    /**
     * Ortalama Rating
     */
    private BigDecimal averageRating;
    
    /**
     * Toplam Yorum Sayısı
     */
    private Integer totalReviews;
    
    /**
     * Yıldız Dağılımı (1-5)
     */
    private Integer star1Count = 0;
    private Integer star2Count = 0;
    private Integer star3Count = 0;
    private Integer star4Count = 0;
    private Integer star5Count = 0;
    
    /**
     * Yıldız yüzdesi hesapla
     * @JsonIgnore ile işaretlendi çünkü hesaplanan değerler, Redis cache'te saklanmamalı
     */
    @JsonIgnore
    public Double getStar1Percentage() {
        if (totalReviews == 0) return 0.0;
        return (star1Count * 100.0) / totalReviews;
    }
    
    @JsonIgnore
    public Double getStar2Percentage() {
        if (totalReviews == 0) return 0.0;
        return (star2Count * 100.0) / totalReviews;
    }
    
    @JsonIgnore
    public Double getStar3Percentage() {
        if (totalReviews == 0) return 0.0;
        return (star3Count * 100.0) / totalReviews;
    }
    
    @JsonIgnore
    public Double getStar4Percentage() {
        if (totalReviews == 0) return 0.0;
        return (star4Count * 100.0) / totalReviews;
    }
    
    @JsonIgnore
    public Double getStar5Percentage() {
        if (totalReviews == 0) return 0.0;
        return (star5Count * 100.0) / totalReviews;
    }
}

