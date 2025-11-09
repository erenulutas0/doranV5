package com.microservices.inventory.Controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.microservices.inventory.Model.Inventory;
import com.microservices.inventory.Model.InventoryStatus;
import com.microservices.inventory.Model.Location;
import com.microservices.inventory.Service.InventoryService;

/**
 * Inventory Controller
 * E-ticaret ve Getir tarzı uygulamalar için stok yönetimi
 * 
 * Önemli Endpoint'ler:
 * - GET /inventory/product/{productId} → Product ID'ye göre stok (EN ÖNEMLİ!)
 * - GET /inventory/check → Toplu stok kontrolü (sepet için)
 * - PATCH /inventory/{id}/reserve → Stok rezerve et (sipariş için)
 * - GET /inventory/location/{location} → Lokasyona göre stok (Getir için)
 */
@RestController
@RequestMapping("/inventory")  // Gateway zaten /api/inventory/** alıyor
public class InventoryController {
    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    /**
     * Tüm stok kayıtlarını getir
     * GET /inventory
     * Admin paneli için kullanılır
     */
    @GetMapping
    public ResponseEntity<List<Inventory>> getAllInventories() {
        List<Inventory> inventories = inventoryService.getAllInventories();
        return ResponseEntity.ok(inventories);
    }

    /**
     * ID'ye göre stok detayı getir
     * GET /inventory/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Inventory> getInventoryById(@PathVariable("id") UUID id) {
        Inventory inventory = inventoryService.getInventoryById(id);
        return ResponseEntity.ok(inventory);
    }

    /**
     * Product ID'ye göre stok getir
     * GET /inventory/product/{productId}
     * 
     * EN ÖNEMLİ ENDPOINT!
     * - Ürün detay sayfasında stok bilgisi gösterilir
     * - Sepete ekleme öncesi stok kontrolü yapılır
     * - Order Service bu endpoint'i kullanır
     */
    @GetMapping("/product/{productId}")
    public ResponseEntity<Inventory> getInventoryByProductId(@PathVariable("productId") UUID productId) {
        Inventory inventory = inventoryService.getInventoryByProductId(productId);
        return ResponseEntity.ok(inventory);
    }

    /**
     * Kullanılabilir stok miktarını getir
     * GET /inventory/product/{productId}/available
     * 
     * Sadece kullanılabilir miktarı döner (quantity - reservedQuantity)
     * Frontend'de hızlı kontrol için kullanılır
     */
    @GetMapping("/product/{productId}/available")
    public ResponseEntity<Integer> getAvailableQuantity(@PathVariable("productId") UUID productId) {
        Integer availableQuantity = inventoryService.getAvailableQuantity(productId);
        return ResponseEntity.ok(availableQuantity);
    }

    /**
     * Stok durumuna göre filtrele
     * GET /inventory/status?status=OUT_OF_STOCK
     * 
     * Örnek kullanımlar:
     * - LOW_STOCK → Düşük stoklu ürünleri listele (admin için)
     * - OUT_OF_STOCK → Tükenen ürünleri listele
     * - IN_STOCK → Stokta olan ürünleri listele
     */
    @GetMapping("/status")
    public ResponseEntity<List<Inventory>> getInventoriesByStatus(
            @RequestParam("status") InventoryStatus status) {
        List<Inventory> inventories = inventoryService.getInventoriesByStatus(status);
        return ResponseEntity.ok(inventories);
    }

    /**
     * Lokasyona göre stok filtrele
     * GET /inventory/location?location=BESIKTAS
     * 
     * Getir tarzı uygulamalar için önemli!
     * - Hangi depoda hangi ürünler var?
     * - Kullanıcının konumuna en yakın depoda stok var mı?
     */
    @GetMapping("/location")
    public ResponseEntity<List<Inventory>> getInventoriesByLocation(
            @RequestParam("location") Location location) {
        List<Inventory> inventories = inventoryService.getInventoriesByLocation(location);
        return ResponseEntity.ok(inventories);
    }

    /**
     * Toplu stok kontrolü
     * POST /inventory/check
     * 
     * Sepet için kullanılır!
     * Request body: { "productId1": quantity1, "productId2": quantity2, ... }
     * Response: Hangi ürünlerin stokta olduğu/olmadığı bilgisi
     * 
     * Örnek Request:
     * {
     *   "product-id-1": 2,
     *   "product-id-2": 5
     * }
     */
    @PostMapping("/check")
    public ResponseEntity<Map<UUID, Boolean>> checkStockAvailability(
            @RequestBody Map<UUID, Integer> productQuantities) {
        Map<UUID, Boolean> availability = inventoryService.checkStockAvailability(productQuantities);
        return ResponseEntity.ok(availability);
    }

    /**
     * Yeni stok kaydı oluştur
     * POST /inventory
     * 
     * Product oluşturulduğunda Inventory Service'e çağrılır
     */
    @PostMapping
    public ResponseEntity<Inventory> createInventory(@RequestBody Inventory inventory) {
        Inventory createdInventory = inventoryService.createInventory(inventory);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdInventory);
    }

    /**
     * Stok kaydını tamamen güncelle
     * PUT /inventory/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Inventory> updateInventory(
            @PathVariable("id") UUID id,
            @RequestBody Inventory inventory) {
        Inventory updatedInventory = inventoryService.updateInventory(id, inventory);
        return ResponseEntity.ok(updatedInventory);
    }

    /**
     * Sadece stok miktarını güncelle
     * PATCH /inventory/{id}/quantity?quantity=100
     * 
     * Yeni ürün geldiğinde veya stok azaldığında kullanılır
     */
    @PatchMapping("/{id}/quantity")
    public ResponseEntity<Inventory> updateQuantity(
            @PathVariable("id") UUID id,
            @RequestParam("quantity") Integer quantity) {
        Inventory updatedInventory = inventoryService.updateQuantity(id, quantity);
        return ResponseEntity.ok(updatedInventory);
    }

    /**
     * Stok rezerve et (sipariş için)
     * PATCH /inventory/{id}/reserve?quantity=2
     * 
     * Sipariş verildiğinde Order Service bu endpoint'i çağırır
     * Stok rezerve edilir, böylece başka siparişler için kullanılamaz
     */
    @PatchMapping("/{id}/reserve")
    public ResponseEntity<Inventory> reserveStock(
            @PathVariable("id") UUID id,
            @RequestParam("quantity") Integer quantity) {
        Inventory updatedInventory = inventoryService.reserveStock(id, quantity);
        return ResponseEntity.ok(updatedInventory);
    }

    /**
     * Rezerve edilmiş stoku serbest bırak
     * PATCH /inventory/{id}/release?quantity=2
     * 
     * Sipariş iptal edildiğinde veya başarısız olduğunda kullanılır
     */
    @PatchMapping("/{id}/release")
    public ResponseEntity<Inventory> releaseReservedStock(
            @PathVariable("id") UUID id,
            @RequestParam("quantity") Integer quantity) {
        Inventory updatedInventory = inventoryService.releaseReservedStock(id, quantity);
        return ResponseEntity.ok(updatedInventory);
    }

    /**
     * Stok kaydını sil
     * DELETE /inventory/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInventory(@PathVariable("id") UUID id) {
        inventoryService.deleteInventory(id);
        return ResponseEntity.noContent().build();
    }
}
