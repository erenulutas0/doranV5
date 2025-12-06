package com.microservices.product.Model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Product Entity
 * E-ticaret sistemindeki ürünleri temsil eder
 */
@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    
    @Id
    private UUID id;
    
    // Entity veritabanına kaydedilmeden önce UUID oluştur
    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * Ürün Adı
     * - Zorunlu alan
     * - Minimum 3, maksimum 200 karakter
     * - Örnek: "MacBook Pro 16 inch"
     */
    @NotBlank(message = "Product name is required")
    @Size(min = 3, max = 200, message = "Product name must be between 3 and 200 characters")
    private String name;
    
    /**
     * Ürün Açıklaması
     * - Opsiyonel alan
     * - Maksimum 1000 karakter
     * - Örnek: "Apple'ın en güçlü laptop'u, M3 Max çip ile..."
     */
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;
    
    /**
     * Ürün Fiyatı
     * - Zorunlu alan
     * - Minimum 0.01 (1 kuruş)
     * - BigDecimal kullanıyoruz (para işlemleri için hassas)
     * - Örnek: 45000.00 (45.000 TL)
     */
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be at least 0.01")
    private BigDecimal price;
    
    /**
     * Ürün Kategorisi
     * - Zorunlu alan
     * - Örnek: "Electronics", "Clothing", "Books", "Home & Garden"
     */
    @NotBlank(message = "Category is required")
    @Size(min = 2, max = 50, message = "Category must be between 2 and 50 characters")
    private String category;
    
    /**
     * Stok Miktarı
     * - Zorunlu alan
     * - Minimum 0 (stokta yok)
     * - Integer kullanıyoruz (adet sayısı)
     * - Not: Gerçek stok yönetimi inventory-service'de yapılır
     * - Bu alan sadece ürün bilgisi için referans
     */
    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity cannot be negative")
    private Integer stockQuantity;
    
    /**
     * Ürün SKU (Stock Keeping Unit)
     * - Opsiyonel alan
     * - Ürünün benzersiz stok kodu
     * - Örnek: "MBP-16-M3-2024"
     */
    @Size(max = 50, message = "SKU must not exceed 50 characters")
    private String sku;
    
    /**
     * Ürün Markası
     * - Opsiyonel alan
     * - Örnek: "Apple", "Samsung", "Nike"
     */
    @Size(max = 100, message = "Brand must not exceed 100 characters")
    private String brand;

    
    /**
     * Ürün Resim URL'i
     * - Opsiyonel alan
     * - Ürün görselinin URL'i
     * - Örnek: "https://example.com/images/macbook-pro.jpg"
     */
    @Size(max = 500, message = "Image URL must not exceed 500 characters")
    private String imageUrl;
    
    /**
     * Ürün Aktif Mi?
     * - Varsayılan: true
     * - false ise ürün satışta değil (silinmiş gibi)
     * - Soft delete için kullanılabilir
     */
    private Boolean isActive = true;
    
    /**
     * Ortalama Rating (Review-service'den senkronize edilir)
     * - Opsiyonel alan
     * - 0.0 - 5.0 arası değer
     * - Review-service'den periyodik olarak güncellenir
     */
    @JsonProperty("averageRating")
    @Setter
    private BigDecimal averageRating;
    
    /**
     * Toplam Yorum Sayısı (Review-service'den senkronize edilir)
     * - Opsiyonel alan
     * - Review-service'den periyodik olarak güncellenir
     */
    @JsonProperty("reviewCount")
    @Setter
    private Integer reviewCount;
    
    /**
     * Son Rating Senkronizasyon Tarihi
     * - Review-service'den ne zaman güncellendiğini takip eder
     */
    @Setter
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lastRatingSync;
    
    /**
     * Oluşturulma Tarihi
     * - Otomatik oluşturulur (@PrePersist)
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    
    /**
     * Güncellenme Tarihi
     * - Her güncellemede otomatik güncellenir (@PreUpdate)
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
}

