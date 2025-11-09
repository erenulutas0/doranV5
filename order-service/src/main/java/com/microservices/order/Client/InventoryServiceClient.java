package com.microservices.order.Client;

import java.util.Map;
import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Inventory Service Client
 * Inventory Service ile iletişim kurmak için Feign Client
 * 
 * Order Service'in Inventory Service'e ihtiyacı:
 * 1. Stok kontrolü: Sipariş oluşturulmadan önce stok var mı?
 * 2. Stok rezerve etme: Sipariş onaylandığında stokları rezerve et
 * 3. Stok geri verme: Sipariş iptal edildiğinde stokları geri ver
 */
@FeignClient(
    name = "inventory-service", 
    url = "${inventory.service.url:}",
    fallback = InventoryServiceClientFallback.class  // Circuit Breaker açıldığında çağrılacak fallback
)
public interface InventoryServiceClient {
    
    /**
     * Product ID'ye göre stok bilgisi getir
     * 
     * @param productId Ürün ID'si
     * @return Inventory bilgisi (quantity, reservedQuantity, status, vb.)
     * 
     * Kullanım:
     * - Sipariş oluşturulmadan önce stok kontrolü
     * - Ürünün stokta olup olmadığını kontrol et
     */
    @GetMapping("/inventory/product/{productId}")
    InventoryResponse getInventoryByProductId(@PathVariable("productId") UUID productId);
    
    /**
     * Kullanılabilir stok miktarını getir
     * 
     * @param productId Ürün ID'si
     * @return Kullanılabilir miktar (quantity - reservedQuantity)
     * 
     * Kullanım:
     * - Hızlı stok kontrolü (sadece miktar gerekliyse)
     */
    @GetMapping("/inventory/product/{productId}/available")
    Integer getAvailableQuantity(@PathVariable("productId") UUID productId);
    
    /**
     * Toplu stok kontrolü
     * 
     * @param request Map<ProductId, Quantity> - Hangi ürünlerden kaç adet isteniyor
     * @return Map<ProductId, Boolean> - Her ürün için stok var mı?
     * 
     * Kullanım:
     * - Sepet kontrolü: Birden fazla ürünün stok kontrolü
     * - Sipariş oluşturulmadan önce tüm ürünlerin stok kontrolü
     * 
     * Örnek Request:
     * {
     *   "product-id-1": 2,
     *   "product-id-2": 1
     * }
     * 
     * Örnek Response:
     * {
     *   "product-id-1": true,
     *   "product-id-2": false
     * }
     */
    @PostMapping("/inventory/check")
    Map<UUID, Boolean> checkStockAvailability(@RequestBody Map<UUID, Integer> request);
    
    /**
     * Stok rezerve et
     * 
     * @param inventoryId Inventory ID'si
     * @param quantity Rezerve edilecek miktar
     * @return Güncellenmiş Inventory bilgisi
     * 
     * Kullanım:
     * - Sipariş onaylandığında stokları rezerve et
     * - Rezerve edilen stoklar başka siparişlerde kullanılamaz
     */
    @PatchMapping("/inventory/{inventoryId}/reserve")
    InventoryResponse reserveStock(
            @PathVariable("inventoryId") UUID inventoryId,
            @org.springframework.web.bind.annotation.RequestParam("quantity") Integer quantity);
    
    /**
     * Rezerve edilmiş stoku geri ver
     * 
     * @param inventoryId Inventory ID'si
     * @param quantity Geri verilecek miktar
     * @return Güncellenmiş Inventory bilgisi
     * 
     * Kullanım:
     * - Sipariş iptal edildiğinde stokları geri ver
     */
    @PatchMapping("/inventory/{inventoryId}/release")
    InventoryResponse releaseReservedStock(
            @PathVariable("inventoryId") UUID inventoryId,
            @org.springframework.web.bind.annotation.RequestParam("quantity") Integer quantity);
    
    /**
     * Inventory Response DTO
     */
    class InventoryResponse {
        private UUID id;
        private UUID productId;
        private Integer quantity;
        private Integer reservedQuantity;
        private String status;
        
        // Getters and Setters
        public UUID getId() {
            return id;
        }
        
        public void setId(UUID id) {
            this.id = id;
        }
        
        public UUID getProductId() {
            return productId;
        }
        
        public void setProductId(UUID productId) {
            this.productId = productId;
        }
        
        public Integer getQuantity() {
            return quantity;
        }
        
        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }
        
        public Integer getReservedQuantity() {
            return reservedQuantity;
        }
        
        public void setReservedQuantity(Integer reservedQuantity) {
            this.reservedQuantity = reservedQuantity;
        }
        
        public String getStatus() {
            return status;
        }
        
        public void setStatus(String status) {
            this.status = status;
        }
        
        /**
         * Kullanılabilir miktarı hesapla
         */
        public Integer getAvailableQuantity() {
            if (quantity == null || reservedQuantity == null) {
                return 0;
            }
            return quantity - reservedQuantity;
        }
    }
    
}

