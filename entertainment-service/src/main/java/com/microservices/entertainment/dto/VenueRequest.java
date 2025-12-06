package com.microservices.entertainment.dto;

import com.microservices.entertainment.model.Venue;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VenueRequest {
    
    @NotBlank(message = "Venue name is required")
    @Size(min = 2, max = 200, message = "Venue name must be between 2 and 200 characters")
    private String name;
    
    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    private String description;
    
    @NotNull(message = "Venue type is required")
    private String venueType; // CAFE, BAR, CLUB, etc.
    
    @Size(max = 50, message = "Category must not exceed 50 characters")
    private String category;
    
    @NotBlank(message = "Address is required")
    @Size(max = 500, message = "Address must not exceed 500 characters")
    private String address;
    
    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City must not exceed 100 characters")
    private String city;
    
    @Size(max = 100, message = "District must not exceed 100 characters")
    private String district;
    
    @Size(max = 20, message = "Phone must not exceed 20 characters")
    private String phone;
    
    @Email(message = "Email should be valid")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String email;
    
    @Size(max = 500, message = "Website must not exceed 500 characters")
    private String website;
    
    @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
    @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
    private BigDecimal latitude;
    
    @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
    @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
    private BigDecimal longitude;
    
    private LocalTime openingTime;
    
    private LocalTime closingTime;
    
    private String workingDays; // JSON array string
    
    private UUID coverImageId;
}

