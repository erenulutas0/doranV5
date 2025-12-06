package com.microservices.jobs.dto;

import com.microservices.jobs.model.JobListing;
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
public class JobListingRequest {
    
    @NotBlank(message = "Job title is required")
    @Size(min = 3, max = 200, message = "Job title must be between 3 and 200 characters")
    private String title;
    
    @NotBlank(message = "Description is required")
    @Size(max = 5000, message = "Description must not exceed 5000 characters")
    private String description;
    
    @NotBlank(message = "Category is required")
    @Size(min = 2, max = 50, message = "Category must be between 2 and 50 characters")
    private String category;
    
    @NotNull(message = "Job type is required")
    private String jobType; // FULL_TIME, PART_TIME, etc.
    
    @DecimalMin(value = "0.0", message = "Salary cannot be negative")
    private BigDecimal salaryMin;
    
    @DecimalMin(value = "0.0", message = "Salary cannot be negative")
    private BigDecimal salaryMax;
    
    @Size(max = 10, message = "Salary currency must not exceed 10 characters")
    private String salaryCurrency;
    
    @Size(max = 200, message = "Location must not exceed 200 characters")
    private String location;
    
    @Size(max = 100, message = "City must not exceed 100 characters")
    private String city;
    
    private Boolean isRemote;
    
    private String experienceLevel; // ENTRY, JUNIOR, etc.
    
    private String requiredSkills; // JSON array string
    
    private String status; // DRAFT, PUBLISHED, etc.
    
    private Boolean isActive;
    
    private LocalDateTime applicationDeadline;
}

