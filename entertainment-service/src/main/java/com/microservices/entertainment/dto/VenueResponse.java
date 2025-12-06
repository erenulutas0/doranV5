package com.microservices.entertainment.dto;

import com.microservices.entertainment.model.Venue;
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
public class VenueResponse {
    private UUID id;
    private String name;
    private String description;
    private String venueType;
    private String category;
    private String address;
    private String city;
    private String district;
    private String phone;
    private String email;
    private String website;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private LocalTime openingTime;
    private LocalTime closingTime;
    private String workingDays;
    private UUID coverImageId;
    private String coverImageUrl;
    private BigDecimal averageRating;
    private Integer reviewCount;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public static VenueResponse fromEntity(Venue venue) {
        return VenueResponse.builder()
                .id(venue.getId())
                .name(venue.getName())
                .description(venue.getDescription())
                .venueType(venue.getVenueType() != null ? venue.getVenueType().name() : null)
                .category(venue.getCategory())
                .address(venue.getAddress())
                .city(venue.getCity())
                .district(venue.getDistrict())
                .phone(venue.getPhone())
                .email(venue.getEmail())
                .website(venue.getWebsite())
                .latitude(venue.getLatitude())
                .longitude(venue.getLongitude())
                .openingTime(venue.getOpeningTime())
                .closingTime(venue.getClosingTime())
                .workingDays(venue.getWorkingDays())
                .coverImageId(venue.getCoverImageId())
                .averageRating(venue.getAverageRating())
                .reviewCount(venue.getReviewCount())
                .isActive(venue.getIsActive())
                .createdAt(venue.getCreatedAt())
                .updatedAt(venue.getUpdatedAt())
                .build();
    }
}

