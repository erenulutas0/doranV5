package com.microservices.inventory;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.microservices.inventory.Exception.DuplicateResourceException;
import com.microservices.inventory.Exception.ResourceNotFoundException;
import com.microservices.inventory.Model.Inventory;
import com.microservices.inventory.Model.InventoryStatus;
import com.microservices.inventory.Model.Location;
import com.microservices.inventory.Repository.InventoryRepository;
import com.microservices.inventory.Service.InventoryService;

/**
 * InventoryService için Unit Test
 * @DataJpaTest: Sadece JPA katmanını test eder, veritabanı işlemleri için
 */
@DataJpaTest
@Import(InventoryService.class)  // InventoryService'i test context'ine ekle
class InventoryServiceTest {

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private InventoryRepository inventoryRepository;

    private Inventory testInventory;
    private UUID testProductId;

    @BeforeEach
    void setUp() {
        // Her test öncesi çalışır
        testProductId = UUID.randomUUID();
        testInventory = new Inventory();
        testInventory.setProductId(testProductId);
        testInventory.setQuantity(100);
        testInventory.setReservedQuantity(0);
        testInventory.setMinStockLevel(10);
        testInventory.setMaxStockLevel(500);
        testInventory.setLocation(Location.BESIKTAS);
    }

    @Test
    void testCreateInventory() {
        // Given: testInventory hazır
        // When: Stok kaydı oluşturuluyor
        Inventory createdInventory = inventoryService.createInventory(testInventory);

        // Then: Stok kaydı başarıyla oluşturuldu
        assertNotNull(createdInventory.getId());
        assertEquals(testProductId, createdInventory.getProductId());
        assertEquals(100, createdInventory.getQuantity());
        assertEquals(0, createdInventory.getReservedQuantity());
        assertEquals(Location.BESIKTAS, createdInventory.getLocation());
        // Status otomatik hesaplanır
        assertEquals(InventoryStatus.IN_STOCK, createdInventory.getStatus());
    }

    @Test
    void testCreateInventoryDuplicateProductId() {
        // Given: Bir stok kaydı zaten var
        inventoryService.createInventory(testInventory);

        // When & Then: Aynı productId ile tekrar oluşturulmaya çalışılırsa exception fırlatılmalı
        Inventory duplicateInventory = new Inventory();
        duplicateInventory.setProductId(testProductId);
        duplicateInventory.setQuantity(50);

        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class, () -> {
            inventoryService.createInventory(duplicateInventory);
        });
        assertTrue(exception.getMessage().contains("Inventory already exists"));
    }

    @Test
    void testGetAllInventories() {
        // Given: Birkaç stok kaydı oluşturuluyor
        inventoryService.createInventory(testInventory);
        
        Inventory inventory2 = new Inventory();
        inventory2.setProductId(UUID.randomUUID());
        inventory2.setQuantity(50);
        inventory2.setMinStockLevel(5);
        inventoryService.createInventory(inventory2);

        // When: Tüm stok kayıtları getiriliyor
        List<Inventory> inventories = inventoryService.getAllInventories();

        // Then: 2 kayıt olmalı
        assertEquals(2, inventories.size());
    }

    @Test
    void testGetInventoryById() {
        // Given: Bir stok kaydı oluşturuluyor
        Inventory createdInventory = inventoryService.createInventory(testInventory);
        UUID inventoryId = createdInventory.getId();

        // When: ID ile stok kaydı getiriliyor
        Inventory foundInventory = inventoryService.getInventoryById(inventoryId);

        // Then: Doğru stok kaydı bulundu
        assertNotNull(foundInventory);
        assertEquals(inventoryId, foundInventory.getId());
        assertEquals(testProductId, foundInventory.getProductId());
        assertEquals(100, foundInventory.getQuantity());
    }

    @Test
    void testGetInventoryByIdNotFound() {
        // Given: Var olmayan bir ID
        UUID nonExistentId = UUID.randomUUID();

        // When & Then: Exception fırlatılmalı
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            inventoryService.getInventoryById(nonExistentId);
        });
        assertTrue(exception.getMessage().contains("Inventory not found"));
    }

    @Test
    void testGetInventoryByProductId() {
        // Given: Bir stok kaydı oluşturuluyor
        inventoryService.createInventory(testInventory);

        // When: Product ID ile stok kaydı getiriliyor
        Inventory foundInventory = inventoryService.getInventoryByProductId(testProductId);

        // Then: Doğru stok kaydı bulundu
        assertNotNull(foundInventory);
        assertEquals(testProductId, foundInventory.getProductId());
        assertEquals(100, foundInventory.getQuantity());
    }

    @Test
    void testGetInventoryByProductIdNotFound() {
        // Given: Var olmayan bir productId
        UUID nonExistentProductId = UUID.randomUUID();

        // When & Then: Exception fırlatılmalı
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            inventoryService.getInventoryByProductId(nonExistentProductId);
        });
        assertTrue(exception.getMessage().contains("Inventory not found"));
    }

    @Test
    void testGetAvailableQuantity() {
        // Given: Rezerve edilmiş stoklu bir kayıt
        testInventory.setQuantity(100);
        testInventory.setReservedQuantity(30);
        inventoryService.createInventory(testInventory);

        // When: Kullanılabilir stok miktarı getiriliyor
        Integer availableQuantity = inventoryService.getAvailableQuantity(testProductId);

        // Then: Kullanılabilir stok = 100 - 30 = 70
        assertEquals(70, availableQuantity);
    }

    @Test
    void testGetInventoriesByStatus() {
        // Given: Farklı durumlarda stok kayıtları
        inventoryService.createInventory(testInventory); // IN_STOCK (quantity=100, minStockLevel=10)
        
        Inventory outOfStock = new Inventory();
        outOfStock.setProductId(UUID.randomUUID());
        outOfStock.setQuantity(0);
        outOfStock.setMinStockLevel(10);
        inventoryService.createInventory(outOfStock); // OUT_OF_STOCK
        
        Inventory lowStock = new Inventory();
        lowStock.setProductId(UUID.randomUUID());
        lowStock.setQuantity(5);
        lowStock.setMinStockLevel(10);
        inventoryService.createInventory(lowStock); // LOW_STOCK

        // When: OUT_OF_STOCK durumundaki kayıtlar getiriliyor
        List<Inventory> outOfStockInventories = inventoryService.getInventoriesByStatus(InventoryStatus.OUT_OF_STOCK);

        // Then: Sadece OUT_OF_STOCK kayıtları bulundu
        assertEquals(1, outOfStockInventories.size());
        assertEquals(InventoryStatus.OUT_OF_STOCK, outOfStockInventories.get(0).getStatus());
    }

    @Test
    void testGetInventoriesByLocation() {
        // Given: Farklı lokasyonlarda stok kayıtları
        inventoryService.createInventory(testInventory); // BESIKTAS
        
        Inventory inventory2 = new Inventory();
        inventory2.setProductId(UUID.randomUUID());
        inventory2.setQuantity(50);
        inventory2.setLocation(Location.KADIKOY);
        inventoryService.createInventory(inventory2); // KADIKOY

        // When: BESIKTAS lokasyonundaki kayıtlar getiriliyor
        List<Inventory> besiktasInventories = inventoryService.getInventoriesByLocation(Location.BESIKTAS);

        // Then: Sadece BESIKTAS kayıtları bulundu
        assertEquals(1, besiktasInventories.size());
        assertEquals(Location.BESIKTAS, besiktasInventories.get(0).getLocation());
    }

    @Test
    void testCheckStockAvailability() {
        // Given: Stok kayıtları oluşturuluyor
        inventoryService.createInventory(testInventory); // quantity=100, available=100
        
        UUID productId2 = UUID.randomUUID();
        Inventory inventory2 = new Inventory();
        inventory2.setProductId(productId2);
        inventory2.setQuantity(50);
        inventory2.setReservedQuantity(10); // available=40
        inventoryService.createInventory(inventory2);

        UUID productId3 = UUID.randomUUID(); // Stok kaydı yok

        // When: Toplu stok kontrolü yapılıyor
        Map<UUID, Integer> productQuantities = Map.of(
            testProductId, 50,  // Yeterli (100 available)
            productId2, 30,     // Yeterli (40 available)
            productId3, 10      // Stok kaydı yok
        );
        Map<UUID, Boolean> availability = inventoryService.checkStockAvailability(productQuantities);

        // Then: Doğru sonuçlar dönmeli
        assertEquals(3, availability.size());
        assertTrue(availability.get(testProductId));   // Yeterli
        assertTrue(availability.get(productId2));     // Yeterli
        assertFalse(availability.get(productId3));    // Stok kaydı yok
    }

    @Test
    void testCheckStockAvailabilityInsufficient() {
        // Given: Yetersiz stoklu bir kayıt
        testInventory.setQuantity(20);
        testInventory.setReservedQuantity(10); // available=10
        inventoryService.createInventory(testInventory);

        // When: Daha fazla miktar isteniyor
        Map<UUID, Integer> productQuantities = Map.of(testProductId, 50);
        Map<UUID, Boolean> availability = inventoryService.checkStockAvailability(productQuantities);

        // Then: Yetersiz stok
        assertFalse(availability.get(testProductId));
    }

    @Test
    void testUpdateInventory() {
        // Given: Bir stok kaydı oluşturuluyor
        Inventory createdInventory = inventoryService.createInventory(testInventory);
        UUID inventoryId = createdInventory.getId();

        // When: Stok kaydı güncelleniyor
        Inventory updateData = new Inventory();
        updateData.setQuantity(200);
        updateData.setReservedQuantity(20);
        updateData.setMinStockLevel(15);
        updateData.setLocation(Location.KADIKOY);

        Inventory updatedInventory = inventoryService.updateInventory(inventoryId, updateData);

        // Then: Stok kaydı güncellendi
        assertEquals(inventoryId, updatedInventory.getId());
        assertEquals(200, updatedInventory.getQuantity());
        assertEquals(20, updatedInventory.getReservedQuantity());
        assertEquals(15, updatedInventory.getMinStockLevel());
        assertEquals(Location.KADIKOY, updatedInventory.getLocation());
        // ProductId değişmemeli
        assertEquals(testProductId, updatedInventory.getProductId());
    }

    @Test
    void testUpdateInventoryPartial() {
        // Given: Bir stok kaydı oluşturuluyor
        Inventory createdInventory = inventoryService.createInventory(testInventory);
        UUID inventoryId = createdInventory.getId();

        // When: Sadece quantity güncelleniyor
        Inventory updateData = new Inventory();
        updateData.setQuantity(150);

        Inventory updatedInventory = inventoryService.updateInventory(inventoryId, updateData);

        // Then: Sadece quantity güncellendi, diğer alanlar korundu
        assertEquals(150, updatedInventory.getQuantity());
        assertEquals(Location.BESIKTAS, updatedInventory.getLocation()); // Değişmedi
        // minStockLevel korunmalı (testInventory'de 10 olarak set edilmişti)
        assertEquals(testInventory.getMinStockLevel(), updatedInventory.getMinStockLevel());
    }

    @Test
    void testUpdateQuantity() {
        // Given: Bir stok kaydı oluşturuluyor
        Inventory createdInventory = inventoryService.createInventory(testInventory);
        UUID inventoryId = createdInventory.getId();

        // When: Sadece miktar güncelleniyor
        Inventory updatedInventory = inventoryService.updateQuantity(inventoryId, 200);

        // Then: Miktar güncellendi
        assertEquals(200, updatedInventory.getQuantity());
        // Status otomatik hesaplanır
        assertEquals(InventoryStatus.IN_STOCK, updatedInventory.getStatus());
    }

    @Test
    void testUpdateQuantityNegative() {
        // Given: Bir stok kaydı oluşturuluyor
        Inventory createdInventory = inventoryService.createInventory(testInventory);
        UUID inventoryId = createdInventory.getId();

        // When & Then: Negatif miktar verilirse exception fırlatılmalı
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            inventoryService.updateQuantity(inventoryId, -10);
        });
        assertTrue(exception.getMessage().contains("Quantity cannot be negative"));
    }

    @Test
    void testReserveStock() {
        // Given: Bir stok kaydı oluşturuluyor
        testInventory.setQuantity(100);
        testInventory.setReservedQuantity(0);
        Inventory createdInventory = inventoryService.createInventory(testInventory);
        UUID inventoryId = createdInventory.getId();

        // When: 30 adet rezerve ediliyor
        Inventory updatedInventory = inventoryService.reserveStock(inventoryId, 30);

        // Then: Rezerve miktarı artırıldı
        assertEquals(30, updatedInventory.getReservedQuantity());
        assertEquals(100, updatedInventory.getQuantity()); // Toplam miktar değişmedi
        assertEquals(70, updatedInventory.getAvailableQuantity()); // Kullanılabilir azaldı
    }

    @Test
    void testReserveStockInsufficient() {
        // Given: Yetersiz stoklu bir kayıt
        testInventory.setQuantity(20);
        testInventory.setReservedQuantity(10); // available=10
        Inventory createdInventory = inventoryService.createInventory(testInventory);
        UUID inventoryId = createdInventory.getId();

        // When & Then: Daha fazla rezerve edilmeye çalışılırsa exception fırlatılmalı
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            inventoryService.reserveStock(inventoryId, 50);
        });
        assertTrue(exception.getMessage().contains("Insufficient stock"));
    }

    @Test
    void testReleaseReservedStock() {
        // Given: Rezerve edilmiş stoklu bir kayıt
        testInventory.setQuantity(100);
        testInventory.setReservedQuantity(30);
        Inventory createdInventory = inventoryService.createInventory(testInventory);
        UUID inventoryId = createdInventory.getId();

        // When: 20 adet serbest bırakılıyor
        Inventory updatedInventory = inventoryService.releaseReservedStock(inventoryId, 20);

        // Then: Rezerve miktarı azaldı
        assertEquals(10, updatedInventory.getReservedQuantity());
        assertEquals(100, updatedInventory.getQuantity()); // Toplam miktar değişmedi
        assertEquals(90, updatedInventory.getAvailableQuantity()); // Kullanılabilir arttı
    }

    @Test
    void testReleaseReservedStockExceedsReserved() {
        // Given: Rezerve edilmiş stoklu bir kayıt
        testInventory.setQuantity(100);
        testInventory.setReservedQuantity(30);
        Inventory createdInventory = inventoryService.createInventory(testInventory);
        UUID inventoryId = createdInventory.getId();

        // When & Then: Rezerve edilmiş miktardan fazla serbest bırakılmaya çalışılırsa exception fırlatılmalı
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            inventoryService.releaseReservedStock(inventoryId, 50);
        });
        assertTrue(exception.getMessage().contains("Cannot release more than reserved"));
    }

    @Test
    void testDeleteInventory() {
        // Given: Bir stok kaydı oluşturuluyor
        Inventory createdInventory = inventoryService.createInventory(testInventory);
        UUID inventoryId = createdInventory.getId();

        // When: Stok kaydı siliniyor
        inventoryService.deleteInventory(inventoryId);

        // Then: Stok kaydı artık bulunamaz
        assertFalse(inventoryRepository.existsById(inventoryId));
    }

    @Test
    void testDeleteInventoryNotFound() {
        // Given: Var olmayan bir ID
        UUID nonExistentId = UUID.randomUUID();

        // When & Then: Exception fırlatılmalı
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            inventoryService.deleteInventory(nonExistentId);
        });
        assertTrue(exception.getMessage().contains("Inventory not found"));
    }

    @Test
    void testStatusCalculation() {
        // Given: Farklı miktarlarda stok kayıtları
        // OUT_OF_STOCK
        Inventory outOfStock = new Inventory();
        outOfStock.setProductId(UUID.randomUUID());
        outOfStock.setQuantity(0);
        outOfStock.setMinStockLevel(10);
        Inventory created1 = inventoryService.createInventory(outOfStock);
        assertEquals(InventoryStatus.OUT_OF_STOCK, created1.getStatus());

        // LOW_STOCK
        Inventory lowStock = new Inventory();
        lowStock.setProductId(UUID.randomUUID());
        lowStock.setQuantity(5);
        lowStock.setMinStockLevel(10);
        Inventory created2 = inventoryService.createInventory(lowStock);
        assertEquals(InventoryStatus.LOW_STOCK, created2.getStatus());

        // IN_STOCK
        Inventory inStock = new Inventory();
        inStock.setProductId(UUID.randomUUID());
        inStock.setQuantity(100);
        inStock.setMinStockLevel(10);
        Inventory created3 = inventoryService.createInventory(inStock);
        assertEquals(InventoryStatus.IN_STOCK, created3.getStatus());
    }

    @Test
    void testReservedStatus() {
        // Given: Tüm stok rezerve edilmiş
        Inventory inventory = new Inventory();
        inventory.setProductId(UUID.randomUUID());
        inventory.setQuantity(100);
        inventory.setReservedQuantity(100); // Tümü rezerve
        inventory.setMinStockLevel(10);
        Inventory created = inventoryService.createInventory(inventory);

        // Then: Status RESERVED olmalı
        assertEquals(InventoryStatus.RESERVED, created.getStatus());
        assertEquals(0, created.getAvailableQuantity());
    }
}

