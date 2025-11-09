package com.microservices.order.Model;

import java.math.BigDecimal;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * OrderItem Entity
 * Sipariş kalemlerini temsil eder
 * 
 * Önemli Notlar:
 * - Bir Order'ın birden fazla OrderItem'ı olabilir (OneToMany)
 * - Her OrderItem bir Product'ı temsil eder (productId)
 * - Fiyat snapshot olarak tutulur (ürün fiyatı değişse bile sipariş fiyatı değişmez)
 * - Subtotal = quantity * price (otomatik hesaplanır)
 */
@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    
    @Id
    private UUID id;
    
    // Entity veritabanına kaydedilmeden önce UUID oluştur
    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        // Subtotal'i otomatik hesapla
        if (quantity != null && price != null) {
            subtotal = price.multiply(BigDecimal.valueOf(quantity));
        }
    }
    
    /**
     * Order ile ilişki
     * ManyToOne: Bir OrderItem bir Order'a aittir
     * FetchType.LAZY: Order bilgisi sadece gerektiğinde yüklenir (performans)
     * @JsonIgnore: Circular reference'ı önlemek için JSON serialization'da ignore edilir
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @NotNull(message = "Order is required")
    @JsonIgnore  // Circular reference'ı önlemek için
    private Order order;
    
    /**
     * Product ID
     * Product Service'deki ürün ID'si
     * Foreign key olarak kullanılabilir (şimdilik sadece ID)
     */
    @NotNull(message = "Product ID is required")
    private UUID productId;
    
    /**
     * Ürün Adı (Snapshot)
     * Sipariş anındaki ürün adı
     * Ürün adı değişse bile siparişte eski ad görünür
     */
    private String productName;
    
    /**
     * Miktar
     * Kaç adet ürün sipariş edildi
     */
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
    
    /**
     * Birim Fiyat (Snapshot)
     * Sipariş anındaki ürün fiyatı
     * Ürün fiyatı değişse bile siparişte eski fiyat görünür
     */
    @NotNull(message = "Price is required")
    private BigDecimal price;
    
    /**
     * Ara Toplam
     * quantity * price
     * Otomatik hesaplanır (@PrePersist)
     */
    private BigDecimal subtotal;
    
    /**
     * Ara toplamı hesapla
     * quantity * price
     */
    public BigDecimal calculateSubtotal() {
        if (quantity != null && price != null) {
            return price.multiply(BigDecimal.valueOf(quantity));
        }
        return BigDecimal.ZERO;
    }
}

