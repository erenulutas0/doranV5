package com.microservices.ownproduct.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * UserProduct Entity
 * Kullanıcıların paylaştığı ürünleri temsil eder
 */
@Entity
@Table(name = "user_products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProduct {
    
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
            status = ProductStatus.DRAFT;
        }
        if (visibility == null) {
            visibility = Visibility.PUBLIC;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * Ürün sahibi (User ID)
     */
    @NotNull(message = "User ID is required")
    @Column(nullable = false)
    private UUID userId;
    
    /**
     * Ürün Adı
     */
    @NotBlank(message = "Product name is required")
    @Size(min = 3, max = 200, message = "Product name must be between 3 and 200 characters")
    @Column(nullable = false, length = 200)
    private String name;
    
    /**
     * Ürün Açıklaması
     */
    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    @Column(columnDefinition = "TEXT")
    private String description;
    
    /**
     * Ürün Fiyatı
     */
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be at least 0.01")
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal price;
    
    /**
     * Ürün Kategorisi
     */
    @NotBlank(message = "Category is required")
    @Size(min = 2, max = 50, message = "Category must be between 2 and 50 characters")
    @Column(nullable = false, length = 50)
    private String category;
    
    /**
     * Ürün Durumu
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProductStatus status;
    
    /**
     * Görünürlük Ayarları
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Visibility visibility;
    
    /**
     * Ürün Konumu (Şehir)
     */
    @Size(max = 100, message = "Location must not exceed 100 characters")
    @Column(length = 100)
    private String location;
    
    /**
     * İletişim Bilgisi (Telefon veya Email)
     */
    @Size(max = 255, message = "Contact info must not exceed 255 characters")
    @Column(length = 255)
    private String contactInfo;
    
    /**
     * Ana Görsel Media ID (Media Service'den)
     */
    @Column
    private UUID primaryImageId;
    
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
     * Paylaşım Tarihi (PUBLISHED olduğunda)
     */
    @Column
    private LocalDateTime publishedAt;
    
    /**
     * Silinme Tarihi (Soft delete)
     */
    @Column
    private LocalDateTime deletedAt;
    
    /**
     * Ürün Durumu Enum
     */
    public enum ProductStatus {
        DRAFT,      // Taslak
        PUBLISHED,  // Yayında
        SOLD,       // Satıldı
        DELETED     // Silindi
    }
    
    /**
     * Görünürlük Enum
     */
    public enum Visibility {
        PUBLIC,     // Herkese açık
        PRIVATE     // Sadece sahibi görebilir
    }
}

