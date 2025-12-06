package com.microservices.jobs.dto;

import com.microservices.jobs.model.JobListing;
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
public class JobListingResponse {
    private UUID id;
    private UUID ownerId;
    private String ownerType;
    private String title;
    private String description;
    private String category;
    private String jobType;
    private BigDecimal salaryMin;
    private BigDecimal salaryMax;
    private String salaryCurrency;
    private String location;
    private String city;
    private Boolean isRemote;
    private String experienceLevel;
    private String requiredSkills;
    private String status;
    private Boolean isActive;
    private LocalDateTime applicationDeadline;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime publishedAt;
    
    public static JobListingResponse fromEntity(JobListing job) {
        return JobListingResponse.builder()
                .id(job.getId())
                .ownerId(job.getOwnerId())
                .ownerType(job.getOwnerType() != null ? job.getOwnerType().name() : null)
                .title(job.getTitle())
                .description(job.getDescription())
                .category(job.getCategory())
                .jobType(job.getJobType() != null ? job.getJobType().name() : null)
                .salaryMin(job.getSalaryMin())
                .salaryMax(job.getSalaryMax())
                .salaryCurrency(job.getSalaryCurrency())
                .location(job.getLocation())
                .city(job.getCity())
                .isRemote(job.getIsRemote())
                .experienceLevel(job.getExperienceLevel() != null ? job.getExperienceLevel().name() : null)
                .requiredSkills(job.getRequiredSkills())
                .status(job.getStatus() != null ? job.getStatus().name() : null)
                .isActive(job.getIsActive())
                .applicationDeadline(job.getApplicationDeadline())
                .createdAt(job.getCreatedAt())
                .updatedAt(job.getUpdatedAt())
                .publishedAt(job.getPublishedAt())
                .build();
    }
}

