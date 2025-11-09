package com.microservices.inventory.Model;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Inventory Entity
 * Ürün stok bilgilerini tutar
 * 
 * Önemli Notlar:
 * - Her ürün için bir Inventory kaydı olmalı
 * - productId unique olmalı (her ürünün tek stok kaydı)
 * - Gerçek zamanlı stok takibi yapılır
 */
@Entity
@Table(name = "inventory", 
       uniqueConstraints = @UniqueConstraint(columnNames = "productId"))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Inventory {
    
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
        // İlk oluşturulduğunda status belirlenir
        if (status == null) {
            status = calculateStatus();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        // Stok miktarı değiştiğinde status güncellenir
        status = calculateStatus();
    }
    
    /**
     * Ürün ID'si
     * - Product Service'deki ürün ID'si
     * - Unique olmalı (her ürünün tek stok kaydı)
     * - Foreign key olarak kullanılabilir (şimdilik sadece ID)
     */
    @NotNull(message = "Product ID is required")
    private UUID productId;
    
    /**
     * Mevcut Stok Miktarı
     * - Şu anda stokta bulunan ürün sayısı
     * - Minimum 0 (stokta yok)
     * - Sipariş verildiğinde azalır, yeni ürün geldiğinde artar
     */
    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity cannot be negative")
    private Integer quantity;
    
    /**
     * Rezerve Edilmiş Stok
     * - Sipariş için ayrılmış ama henüz gönderilmemiş stok
     * - Mevcut stoktan ayrı tutulur
     * - Örnek: quantity=10, reservedQuantity=3 → Kullanılabilir: 7
     */
    @Min(value = 0, message = "Reserved quantity cannot be negative")
    private Integer reservedQuantity = 0;
    
    /**
     * Minimum Stok Seviyesi
     * - Bu seviyenin altına düşerse LOW_STOCK durumuna geçer
     * - Uyarı verilmeli, yeniden stoklanmalı
     * - Örnek: minStockLevel=5, quantity=3 → LOW_STOCK
     */
    @Min(value = 0, message = "Minimum stock level cannot be negative")
    private Integer minStockLevel = 0;
    
    /**
     * Maksimum Stok Seviyesi
     * - Depo kapasitesi için
     * - Bu seviyeyi aşmamalı
     * - Örnek: maxStockLevel=100
     */
    @Min(value = 0, message = "Maximum stock level cannot be negative")
    private Integer maxStockLevel;
    
    /**
     * Stok Durumu
     * - Enum kullanıyoruz (IN_STOCK, OUT_OF_STOCK, LOW_STOCK, vb.)
     * - Otomatik hesaplanır (quantity'a göre)
     * - Raporlama ve filtreleme için kullanılır
     */
    @Enumerated(EnumType.STRING)
    private InventoryStatus status;
    
    /**
     * Depo/Lokasyon
     * - Ürünün bulunduğu depo veya lokasyon
     * - Opsiyonel (tek depo varsa gerekmez)
     * - Örnek: "Warehouse-A", "Store-1", "Distribution-Center"
     */
    @Enumerated(EnumType.STRING)
    private Location location;
    
    /**
     * Oluşturulma Tarihi
     */
    private LocalDateTime createdAt;
    
    /**
     * Güncellenme Tarihi
     */
    private LocalDateTime updatedAt;
    
    /**
     * Stok durumunu otomatik hesapla
     * - quantity'a göre status belirlenir
     */
    private InventoryStatus calculateStatus() {
        if (quantity == null || quantity <= 0) {
            return InventoryStatus.OUT_OF_STOCK;
        }
        
        if (minStockLevel != null && quantity <= minStockLevel) {
            return InventoryStatus.LOW_STOCK;
        }
        
        if (reservedQuantity != null && reservedQuantity > 0) {
            int available = quantity - reservedQuantity;
            if (available <= 0) {
                return InventoryStatus.RESERVED;
            }
        }
        
        return InventoryStatus.IN_STOCK;
    }
    
    /**
     * Kullanılabilir stok miktarını hesapla
     * - Mevcut stok - Rezerve edilmiş stok
     */
    public Integer getAvailableQuantity() {
        if (quantity == null) return 0;
        if (reservedQuantity == null) return quantity;
        return Math.max(0, quantity - reservedQuantity);
    }
    
    /**
     * Stok yeterli mi kontrolü
     */
    public boolean hasEnoughStock(Integer requiredQuantity) {
        return getAvailableQuantity() >= requiredQuantity;
    }
}

