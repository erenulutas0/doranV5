package com.microservices.order.Model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Order Entity
 * Sipariş bilgilerini tutar
 * 
 * Önemli Notlar:
 * - Her sipariş bir kullanıcıya aittir (userId)
 * - Bir siparişin birden fazla kalemi olabilir (OrderItem listesi)
 * - Toplam tutar otomatik hesaplanır (tüm OrderItem'ların subtotal'ları toplamı)
 * - Sipariş durumu enum ile yönetilir (OrderStatus)
 */
@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    
    @Id
    private UUID id;
    
    // Entity veritabanına kaydedilmeden önce UUID oluştur
    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        if (orderDate == null) {
            orderDate = LocalDateTime.now();
        }
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        // İlk oluşturulduğunda status PENDING
        if (status == null) {
            status = OrderStatus.PENDING;
        }
        // OrderItem listesi null ise boş liste oluştur
        if (orderItems == null) {
            orderItems = new ArrayList<>();
        }
        // Toplam tutarı hesapla (ilk kayıt için)
        if (totalAmount == null) {
            totalAmount = calculateTotalAmount();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        // Toplam tutarı güncelle
        totalAmount = calculateTotalAmount();
    }
    
    /**
     * User ID
     * User Service'deki kullanıcı ID'si
     * Foreign key olarak kullanılabilir (şimdilik sadece ID)
     */
    @NotNull(message = "User ID is required")
    private UUID userId;
    
    /**
     * Address ID (Opsiyonel)
     * User Service'deki adres ID'si
     * Kullanıcının birden fazla adresi varsa hangi adresin seçildiğini belirtir
     * 
     * ÖNEMLİ:
     * - Eğer null ise: Kullanıcının default adresi kullanılır veya manuel adres girilir
     * - Eğer dolu ise: User Service'den bu addressId'ye ait adres bilgileri çekilir
     * - Ama adres bilgileri Order'da snapshot olarak tutulur (kullanıcı adresini değiştirse bile)
     * 
     * Gelecekte Feign Client ile:
     * - User Service'den addressId'ye göre adres bilgileri çekilir
     * - Order'da snapshot olarak kaydedilir
     */
    private UUID addressId;
    
    /**
     * Sipariş Kalemleri
     * OneToMany: Bir Order'ın birden fazla OrderItem'ı olabilir
     * CascadeType.ALL: Order silindiğinde OrderItem'lar da silinir
     * FetchType.LAZY: OrderItem'lar sadece gerektiğinde yüklenir (performans)
     * orphanRemoval = true: OrderItem'lar Order'dan ayrıldığında silinir
     */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();
    
    /**
     * Sipariş Durumu
     * Enum kullanıyoruz (PENDING, CONFIRMED, SHIPPED, vb.)
     */
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    
    /**
     * Toplam Tutar
     * Tüm OrderItem'ların subtotal'ları toplamı
     * Otomatik hesaplanır (@PreUpdate)
     */
    private BigDecimal totalAmount;
    
    /**
     * Teslimat Adresi
     * Siparişin teslim edileceği adres
     * 
     * ÖNEMLİ: Snapshot Pattern
     * - Bu bilgiler Order'da snapshot olarak tutulur
     * - Kullanıcı adresini değiştirse bile eski siparişlerde eski adres görünür
     * - Kullanıcı farklı adresler seçebilir (ev, iş, hediye)
     * 
     * Nasıl Doldurulur:
     * 1. Eğer addressId dolu ise: User Service'den (Feign Client ile) bu addressId'ye ait
     *    adres bilgileri çekilir ve buraya yazılır
     * 2. Eğer addressId null ise: Frontend'den gelen request'teki adres bilgileri kullanılır
     *    (manuel adres girişi veya default adres)
     * 
     * Örnek Senaryo:
     * - Kullanıcının 3 adresi var: "Ev", "İş", "Hediye"
     * - Sipariş verirken "İş" adresini seçer (addressId = "iş-adresi-id")
     * - User Service'den "İş" adresinin bilgileri çekilir
     * - Order'da snapshot olarak kaydedilir
     * - Kullanıcı "İş" adresini sonradan değiştirse bile eski siparişte eski adres görünür
     */
    @NotBlank(message = "Shipping address is required")
    @Size(min = 10, max = 500, message = "Shipping address must be between 10 and 500 characters")
    private String shippingAddress;
    
    /**
     * Şehir
     * Snapshot olarak tutulur (kullanıcı bilgileri değişse bile)
     */
    @NotBlank(message = "City is required")
    @Size(min = 2, max = 50, message = "City must be between 2 and 50 characters")
    private String city;
    
    /**
     * Posta Kodu
     * Snapshot olarak tutulur (kullanıcı bilgileri değişse bile)
     */
    @NotBlank(message = "Zip code is required")
    @Size(min = 5, max = 10, message = "Zip code must be between 5 and 10 characters")
    private String zipCode;
    
    /**
     * Telefon Numarası
     * Teslimat için iletişim bilgisi
     * Snapshot olarak tutulur (kullanıcı bilgileri değişse bile)
     */
    @NotBlank(message = "Phone number is required")
    @Size(min = 10, max = 15, message = "Phone number must be between 10 and 15 characters")
    private String phoneNumber;
    
    /**
     * Sipariş Tarihi
     * Siparişin oluşturulduğu tarih
     */
    private LocalDateTime orderDate;
    
    /**
     * Teslim Tarihi
     * Siparişin teslim edildiği tarih (SHIPPED veya DELIVERED durumunda)
     */
    private LocalDateTime deliveryDate;
    
    /**
     * Oluşturulma Tarihi
     */
    private LocalDateTime createdAt;
    
    /**
     * Güncellenme Tarihi
     */
    private LocalDateTime updatedAt;
    
    /**
     * Notlar
     * Sipariş hakkında ek bilgiler
     */
    private String notes;
    
    /**
     * Toplam tutarı hesapla
     * Tüm OrderItem'ların subtotal'ları toplamı
     */
    public BigDecimal calculateTotalAmount() {
        if (orderItems == null || orderItems.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return orderItems.stream()
                .map(item -> item.getSubtotal() != null ? item.getSubtotal() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    /**
     * OrderItem ekle
     * OrderItem'a Order referansını da set eder ve subtotal'ı hesaplar
     */
    public void addOrderItem(OrderItem item) {
        if (orderItems == null) {
            orderItems = new ArrayList<>();
        }
        item.setOrder(this);
        // Subtotal'ı hesapla (eğer hesaplanmamışsa)
        if (item.getSubtotal() == null) {
            item.setSubtotal(item.calculateSubtotal());
        }
        orderItems.add(item);
    }
    
    /**
     * OrderItem kaldır
     */
    public void removeOrderItem(OrderItem item) {
        if (orderItems != null) {
            orderItems.remove(item);
            item.setOrder(null);
        }
    }
    
    /**
     * Sipariş durumunu güncelle
     * DELIVERED durumunda deliveryDate set edilir
     */
    public void updateStatus(OrderStatus newStatus) {
        this.status = newStatus;
        if (newStatus == OrderStatus.DELIVERED && deliveryDate == null) {
            deliveryDate = LocalDateTime.now();
        }
    }
}

