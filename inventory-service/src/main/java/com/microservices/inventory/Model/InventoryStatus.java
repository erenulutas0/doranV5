package com.microservices.inventory.Model;

/**
 * Inventory Status Enum
 * Stok durumlarını temsil eder
 * 
 * Enum kullanmanın faydaları:
 * 1. Type Safety: Compile-time'da hata yakalama
 * 2. Magic String'lerden kaçınma: "IN_STOCK" yerine InventoryStatus.IN_STOCK
 * 3. IDE desteği: Autocomplete, refactoring kolaylığı
 * 4. Kod okunabilirliği: Daha anlaşılır kod
 * 5. Değer kontrolü: Sadece belirli değerler kullanılabilir
 */
public enum InventoryStatus {
    
    /**
     * Stokta var
     * Normal durum, sipariş verilebilir
     */
    IN_STOCK("Stokta mevcut"),
    
    /**
     * Stokta yok
     * Ürün tükenmiş, sipariş verilemez
     */
    OUT_OF_STOCK("Stokta yok"),
    
    /**
     * Düşük stok
     * Minimum stok seviyesinin altında
     * Uyarı verilmeli, yeniden stoklanmalı
     */
    LOW_STOCK("Düşük stok"),
    
    /**
     * Rezerve edilmiş
     * Sipariş için ayrılmış ama henüz gönderilmemiş
     */
    RESERVED("Rezerve edilmiş"),
    
    /**
     * Beklemede
     * Stok girişi bekleniyor
     */
    PENDING("Beklemede"),
    
    /**
     * Devre dışı
     * Stok yönetimi durdurulmuş
     */
    DISABLED("Devre dışı");
    
    private final String description;
    
    InventoryStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}

