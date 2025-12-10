package com.microservices.ownproduct.dto;

import lombok.Data;

import java.util.UUID;

/**
 * Rating Summary DTO
 * Review Service'ten dönen rating bilgileri
 */
@Data
public class RatingSummary {
    private UUID productId;
    private Double averageRating;
    private Integer totalReviews;
    private Integer star1Count;
    private Integer star2Count;
    private Integer star3Count;
    private Integer star4Count;
    private Integer star5Count;
}

