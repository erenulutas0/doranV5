package com.microservices.entertainment.dto;

import com.microservices.entertainment.model.Event;
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
public class EventResponse {
    private UUID id;
    private UUID venueId;
    private String venueName;
    private String name;
    private String description;
    private String category;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private BigDecimal ticketPrice;
    private Integer maxCapacity;
    private UUID imageId;
    private String imageUrl;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public static EventResponse fromEntity(Event event) {
        return EventResponse.builder()
                .id(event.getId())
                .venueId(event.getVenueId())
                .name(event.getName())
                .description(event.getDescription())
                .category(event.getCategory())
                .startDateTime(event.getStartDateTime())
                .endDateTime(event.getEndDateTime())
                .ticketPrice(event.getTicketPrice())
                .maxCapacity(event.getMaxCapacity())
                .imageId(event.getImageId())
                .status(event.getStatus() != null ? event.getStatus().name() : null)
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .build();
    }
}

