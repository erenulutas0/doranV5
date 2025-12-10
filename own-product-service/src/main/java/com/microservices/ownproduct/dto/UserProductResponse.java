package com.microservices.ownproduct.dto;

import com.microservices.ownproduct.model.UserProduct;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProductResponse {
    private UUID id;
    private UUID userId;
    private String name;
    private String description;
    private BigDecimal price;
    private String category;
    private String status;
    private String visibility;
    private String location;
    private String contactInfo;
    private UUID primaryImageId;
    private String primaryImageUrl; // Media Service'den gelen URL
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime publishedAt;
    
    // Rating information (from Review Service)
    private Double averageRating;
    private Integer totalReviews;
    
    public static UserProductResponse fromEntity(UserProduct product) {
        return UserProductResponse.builder()
                .id(product.getId())
                .userId(product.getUserId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .category(product.getCategory())
                .status(product.getStatus().name())
                .visibility(product.getVisibility().name())
                .location(product.getLocation())
                .contactInfo(product.getContactInfo())
                .primaryImageId(product.getPrimaryImageId())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .publishedAt(product.getPublishedAt())
                .build();
    }
}

