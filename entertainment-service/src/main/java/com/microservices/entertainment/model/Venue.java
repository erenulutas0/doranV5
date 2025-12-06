package com.microservices.entertainment.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

/**
 * Venue Entity
 * Eğlence mekanlarını temsil eder (Kafe, Parti mekanı, vb.)
 */
@Entity
@Table(name = "venues")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Venue {
    
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
        if (isActive == null) {
            isActive = true;
        }
        if (averageRating == null) {
            averageRating = BigDecimal.ZERO;
        }
        if (reviewCount == null) {
            reviewCount = 0;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * Mekan Adı
     */
    @NotBlank(message = "Venue name is required")
    @Size(min = 2, max = 200, message = "Venue name must be between 2 and 200 characters")
    @Column(nullable = false, length = 200)
    private String name;
    
    /**
     * Mekan Açıklaması
     */
    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    @Column(columnDefinition = "TEXT")
    private String description;
    
    /**
     * Mekan Tipi (CAFE, BAR, CLUB, RESTAURANT, THEATER, CINEMA, etc.)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private VenueType venueType;
    
    /**
     * Kategori
     */
    @Size(max = 50, message = "Category must not exceed 50 characters")
    @Column(length = 50)
    private String category;
    
    /**
     * Adres
     */
    @NotBlank(message = "Address is required")
    @Size(max = 500, message = "Address must not exceed 500 characters")
    @Column(nullable = false, length = 500)
    private String address;
    
    /**
     * Şehir
     */
    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City must not exceed 100 characters")
    @Column(nullable = false, length = 100)
    private String city;
    
    /**
     * İlçe/Mahalle
     */
    @Size(max = 100, message = "District must not exceed 100 characters")
    @Column(length = 100)
    private String district;
    
    /**
     * Telefon
     */
    @Size(max = 20, message = "Phone must not exceed 20 characters")
    @Column(length = 20)
    private String phone;
    
    /**
     * Email
     */
    @Email(message = "Email should be valid")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    @Column(length = 255)
    private String email;
    
    /**
     * Website
     */
    @Size(max = 500, message = "Website must not exceed 500 characters")
    @Column(length = 500)
    private String website;
    
    /**
     * Enlem (Latitude)
     */
    @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
    @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
    @Column(precision = 10, scale = 7)
    private BigDecimal latitude;
    
    /**
     * Boylam (Longitude)
     */
    @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
    @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
    @Column(precision = 10, scale = 7)
    private BigDecimal longitude;
    
    /**
     * Açılış Saati
     */
    @Column
    private LocalTime openingTime;
    
    /**
     * Kapanış Saati
     */
    @Column
    private LocalTime closingTime;
    
    /**
     * Çalışma Günleri (JSON formatında)
     */
    @Column(columnDefinition = "TEXT")
    private String workingDays; // JSON array
    
    /**
     * Kapak Görseli Media ID
     */
    @Column
    private UUID coverImageId;
    
    /**
     * Ortalama Rating (Review Service'den senkronize edilir)
     */
    @Column(precision = 3, scale = 2)
    private BigDecimal averageRating;
    
    /**
     * Toplam Yorum Sayısı
     */
    @Column
    private Integer reviewCount;
    
    /**
     * Aktif Mi?
     */
    @Column(nullable = false)
    private Boolean isActive;
    
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
     * Silinme Tarihi (Soft delete)
     */
    @Column
    private LocalDateTime deletedAt;
    
    /**
     * Mekan Tipi Enum
     */
    public enum VenueType {
        CAFE,
        BAR,
        CLUB,
        RESTAURANT,
        THEATER,
        CINEMA,
        CONCERT_HALL,
        SPORTS_VENUE,
        PARK,
        MUSEUM,
        GALLERY,
        OTHER
    }
}

