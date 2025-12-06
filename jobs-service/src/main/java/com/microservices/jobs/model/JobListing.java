package com.microservices.jobs.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * JobListing Entity
 * İş ilanlarını temsil eder
 */
@Entity
@Table(name = "job_listings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobListing {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = JobStatus.DRAFT;
        }
        if (isActive == null) {
            isActive = true;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * İlan sahibi (User ID veya Shop ID)
     */
    @NotNull(message = "Owner ID is required")
    @Column(nullable = false)
    private UUID ownerId;
    
    /**
     * İlan sahibi tipi (USER veya SHOP)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OwnerType ownerType;
    
    /**
     * İş Başlığı
     */
    @NotBlank(message = "Job title is required")
    @Size(min = 3, max = 200, message = "Job title must be between 3 and 200 characters")
    @Column(nullable = false, length = 200)
    private String title;
    
    /**
     * İş Açıklaması
     */
    @NotBlank(message = "Description is required")
    @Size(max = 5000, message = "Description must not exceed 5000 characters")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;
    
    /**
     * İş Kategorisi
     */
    @NotBlank(message = "Category is required")
    @Size(min = 2, max = 50, message = "Category must be between 2 and 50 characters")
    @Column(nullable = false, length = 50)
    private String category;
    
    /**
     * İş Tipi (FULL_TIME, PART_TIME, CONTRACT, INTERNSHIP, FREELANCE)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private JobType jobType;
    
    /**
     * Maaş (opsiyonel)
     */
    @DecimalMin(value = "0.0", message = "Salary cannot be negative")
    @Column(precision = 19, scale = 2)
    private BigDecimal salaryMin;
    
    @DecimalMin(value = "0.0", message = "Salary cannot be negative")
    @Column(precision = 19, scale = 2)
    private BigDecimal salaryMax;
    
    /**
     * Maaş birimi (TRY, USD, EUR)
     */
    @Column(length = 10)
    private String salaryCurrency;
    
    /**
     * Konum
     */
    @Size(max = 200, message = "Location must not exceed 200 characters")
    @Column(length = 200)
    private String location;
    
    /**
     * Şehir
     */
    @Size(max = 100, message = "City must not exceed 100 characters")
    @Column(length = 100)
    private String city;
    
    /**
     * Uzaktan çalışma (true/false)
     */
    @Column
    private Boolean isRemote;
    
    /**
     * Deneyim seviyesi (ENTRY, JUNIOR, MID, SENIOR, LEAD)
     */
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ExperienceLevel experienceLevel;
    
    /**
     * Gerekli beceriler (JSON array string)
     */
    @Column(columnDefinition = "TEXT")
    private String requiredSkills; // JSON array
    
    /**
     * İlan durumu
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private JobStatus status;
    
    /**
     * Aktif mi?
     */
    @Column(nullable = false)
    private Boolean isActive;
    
    /**
     * Son başvuru tarihi
     */
    @Column
    private LocalDateTime applicationDeadline;
    
    /**
     * Oluşturulma Tarihi
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * Güncellenme Tarihi
     */
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    /**
     * Yayınlanma Tarihi
     */
    @Column
    private LocalDateTime publishedAt;
    
    /**
     * Silinme Tarihi (Soft delete)
     */
    @Column
    private LocalDateTime deletedAt;
    
    /**
     * İlan sahibi tipi
     */
    public enum OwnerType {
        USER,
        SHOP
    }
    
    /**
     * İş tipi
     */
    public enum JobType {
        FULL_TIME,
        PART_TIME,
        CONTRACT,
        INTERNSHIP,
        FREELANCE
    }
    
    /**
     * Deneyim seviyesi
     */
    public enum ExperienceLevel {
        ENTRY,
        JUNIOR,
        MID,
        SENIOR,
        LEAD
    }
    
    /**
     * İlan durumu
     */
    public enum JobStatus {
        DRAFT,
        PUBLISHED,
        CLOSED,
        DELETED
    }
}

