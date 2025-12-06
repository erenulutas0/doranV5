package com.microservices.hobbygroup.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * HobbyGroup Entity
 * Hobi gruplarını temsil eder
 */
@Entity
@Table(name = "hobby_groups")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HobbyGroup {
    
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
        if (memberCount == null) {
            memberCount = 0;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * Grup oluşturan kullanıcı (Creator ID)
     */
    @NotNull(message = "Creator ID is required")
    @Column(nullable = false)
    private UUID creatorId;
    
    /**
     * Grup Adı
     */
    @NotBlank(message = "Group name is required")
    @Size(min = 3, max = 200, message = "Group name must be between 3 and 200 characters")
    @Column(nullable = false, length = 200)
    private String name;
    
    /**
     * Grup Açıklaması
     */
    @NotBlank(message = "Description is required")
    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;
    
    /**
     * Hobi Kategorisi
     */
    @NotBlank(message = "Category is required")
    @Size(min = 2, max = 50, message = "Category must be between 2 and 50 characters")
    @Column(nullable = false, length = 50)
    private String category;
    
    /**
     * Konum (Şehir)
     */
    @Size(max = 100, message = "Location must not exceed 100 characters")
    @Column(length = 100)
    private String location;
    
    /**
     * Grup Kuralları (JSON formatında)
     */
    @Column(columnDefinition = "TEXT")
    private String rules; // JSON array string
    
    /**
     * Grup Etiketleri (JSON formatında)
     */
    @Column(columnDefinition = "TEXT")
    private String tags; // JSON array string
    
    /**
     * Grup Görseli Media ID (Media Service'den)
     */
    @Column
    private UUID imageId;
    
    /**
     * Üye Sayısı
     */
    @Column(nullable = false)
    private Integer memberCount;
    
    /**
     * Maksimum Üye Sayısı (null ise sınırsız)
     */
    @Min(value = 1, message = "Max members must be at least 1")
    @Column
    private Integer maxMembers;
    
    /**
     * Grup Durumu
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private GroupStatus status;
    
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
     * Grup Durumu Enum
     */
    public enum GroupStatus {
        ACTIVE,     // Aktif
        INACTIVE,   // Pasif
        DELETED     // Silindi
    }
}

