package com.microservices.shop.dto;

import com.microservices.shop.model.Shop;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopResponse {
    private UUID id;
    private UUID ownerId;
    private String name;
    private String description;
    private String category;
    private String address;
    private String city;
    private String district;
    private String postalCode;
    private String phone;
    private String email;
    private String website;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private LocalTime openingTime;
    private LocalTime closingTime;
    private String workingDays;
    private UUID logoImageId;
    private String logoImageUrl;
    private UUID coverImageId;
    private String coverImageUrl;
    private BigDecimal averageRating;
    private Integer reviewCount;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public static ShopResponse fromEntity(Shop shop) {
        return ShopResponse.builder()
                .id(shop.getId())
                .ownerId(shop.getOwnerId())
                .name(shop.getName())
                .description(shop.getDescription())
                .category(shop.getCategory())
                .address(shop.getAddress())
                .city(shop.getCity())
                .district(shop.getDistrict())
                .postalCode(shop.getPostalCode())
                .phone(shop.getPhone())
                .email(shop.getEmail())
                .website(shop.getWebsite())
                .latitude(shop.getLatitude())
                .longitude(shop.getLongitude())
                .openingTime(shop.getOpeningTime())
                .closingTime(shop.getClosingTime())
                .workingDays(shop.getWorkingDays())
                .logoImageId(shop.getLogoImageId())
                .coverImageId(shop.getCoverImageId())
                .averageRating(shop.getAverageRating())
                .reviewCount(shop.getReviewCount())
                .isActive(shop.getIsActive())
                .createdAt(shop.getCreatedAt())
                .updatedAt(shop.getUpdatedAt())
                .build();
    }
}

