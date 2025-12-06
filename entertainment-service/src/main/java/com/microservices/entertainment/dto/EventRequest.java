package com.microservices.entertainment.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventRequest {
    
    @NotNull(message = "Venue ID is required")
    private UUID venueId;
    
    @NotBlank(message = "Event name is required")
    @Size(min = 3, max = 200, message = "Event name must be between 3 and 200 characters")
    private String name;
    
    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    private String description;
    
    @Size(max = 50, message = "Category must not exceed 50 characters")
    private String category;
    
    @NotNull(message = "Start date/time is required")
    private LocalDateTime startDateTime;
    
    private LocalDateTime endDateTime;
    
    @DecimalMin(value = "0.0", message = "Ticket price cannot be negative")
    private BigDecimal ticketPrice;
    
    @Min(value = 1, message = "Max capacity must be at least 1")
    private Integer maxCapacity;
    
    private UUID imageId;
}

