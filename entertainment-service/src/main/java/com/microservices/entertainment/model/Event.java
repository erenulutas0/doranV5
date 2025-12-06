package com.microservices.entertainment.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Event Entity
 * Mekanlarda düzenlenen etkinlikleri temsil eder
 */
@Entity
@Table(name = "events")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    
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
            status = EventStatus.UPCOMING;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * Mekan ID (Venue ID)
     */
    @NotNull(message = "Venue ID is required")
    @Column(nullable = false)
    private UUID venueId;
    
    /**
     * Etkinlik Adı
     */
    @NotBlank(message = "Event name is required")
    @Size(min = 3, max = 200, message = "Event name must be between 3 and 200 characters")
    @Column(nullable = false, length = 200)
    private String name;
    
    /**
     * Etkinlik Açıklaması
     */
    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    @Column(columnDefinition = "TEXT")
    private String description;
    
    /**
     * Etkinlik Kategorisi
     */
    @Size(max = 50, message = "Category must not exceed 50 characters")
    @Column(length = 50)
    private String category;
    
    /**
     * Başlangıç Tarihi ve Saati
     */
    @NotNull(message = "Start date/time is required")
    @Column(nullable = false)
    private LocalDateTime startDateTime;
    
    /**
     * Bitiş Tarihi ve Saati
     */
    @Column
    private LocalDateTime endDateTime;
    
    /**
     * Bilet Fiyatı (opsiyonel, ücretsiz etkinlikler için null)
     */
    @DecimalMin(value = "0.0", message = "Ticket price cannot be negative")
    @Column(precision = 19, scale = 2)
    private BigDecimal ticketPrice;
    
    /**
     * Maksimum Katılımcı Sayısı
     */
    @Min(value = 1, message = "Max capacity must be at least 1")
    @Column
    private Integer maxCapacity;
    
    /**
     * Etkinlik Görseli Media ID
     */
    @Column
    private UUID imageId;
    
    /**
     * Etkinlik Durumu
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EventStatus status;
    
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
     * Etkinlik Durumu Enum
     */
    public enum EventStatus {
        UPCOMING,   // Yaklaşan
        ONGOING,    // Devam eden
        COMPLETED,  // Tamamlanmış
        CANCELLED,  // İptal edilmiş
        DELETED     // Silinmiş
    }
}

