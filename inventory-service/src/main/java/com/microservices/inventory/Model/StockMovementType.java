package com.microservices.inventory.Model;

/**
 * Stock Movement Type Enum
 * Stok hareket tiplerini temsil eder
 * 
 * Enum kullanmanın faydaları:
 * - Stok hareketlerini kategorize etmek için
 * - Raporlama ve analiz için
 * - İş kurallarını uygulamak için
 */
public enum StockMovementType {
    /**
     * Stok Girişi
     * Yeni ürün geldi, stok artırıldı
     */
    IN("Stok Girişi"),
    
    /**
     * Stok Çıkışı
     * Ürün satıldı veya kullanıldı, stok azaldı
     */
    OUT("Stok Çıkışı"),
    
    /**
     * Stok Transferi
     * Bir depodan diğerine transfer
     */
    TRANSFER("Stok Transferi"),
    
    /**
     * Stok Düzeltmesi
     * Sayım sonrası düzeltme
     */
    ADJUSTMENT("Stok Düzeltmesi"),
    
    /**
     * Stok Rezervasyonu
     * Sipariş için rezerve edildi
     */
    RESERVATION("Stok Rezervasyonu"),
    
    /**
     * Rezervasyon İptali
     * Rezerve edilen stok iptal edildi
     */
    RESERVATION_CANCELLED("Rezervasyon İptali");
    
    private final String description;
    
    StockMovementType(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}

