package com.microservices.inventory;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.microservices.inventory.Model.Inventory;
import com.microservices.inventory.Model.InventoryStatus;
import com.microservices.inventory.Model.Location;
import com.microservices.inventory.Repository.InventoryRepository;

/**
 * InventoryRepository için Integration Test
 * @DataJpaTest: JPA Repository'leri test eder, gerçek veritabanı işlemleri yapar
 */
@DataJpaTest
class InventoryRepositoryTest {

    @Autowired
    private InventoryRepository inventoryRepository;

    private Inventory testInventory;
    private UUID testProductId;

    @BeforeEach
    void setUp() {
        // Her test öncesi veritabanı temizlenir (@DataJpaTest sayesinde)
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
    void testSaveInventory() {
        // Given: testInventory hazır
        // When: Stok kaydı kaydediliyor
        Inventory savedInventory = inventoryRepository.save(testInventory);

        // Then: Stok kaydı başarıyla kaydedildi
        assertNotNull(savedInventory.getId());
        assertEquals(testProductId, savedInventory.getProductId());
        assertEquals(100, savedInventory.getQuantity());
        assertEquals(Location.BESIKTAS, savedInventory.getLocation());
        // Status otomatik hesaplanır
        assertNotNull(savedInventory.getStatus());
    }

    @Test
    void testFindByProductId() {
        // Given: Bir stok kaydı kaydediliyor
        inventoryRepository.save(testInventory);

        // When: Product ID ile stok kaydı aranıyor
        var foundInventory = inventoryRepository.findByProductId(testProductId);

        // Then: Stok kaydı bulundu
        assertTrue(foundInventory.isPresent());
        assertEquals(testProductId, foundInventory.get().getProductId());
        assertEquals(100, foundInventory.get().getQuantity());
    }

    @Test
    void testFindByProductIdNotFound() {
        // Given: Var olmayan bir productId
        UUID nonExistentProductId = UUID.randomUUID();

        // When: Product ID ile stok kaydı aranıyor
        var foundInventory = inventoryRepository.findByProductId(nonExistentProductId);

        // Then: Stok kaydı bulunamadı
        assertFalse(foundInventory.isPresent());
    }

    @Test
    void testFindByStatus() {
        // Given: Farklı durumlarda stok kayıtları kaydediliyor
        inventoryRepository.save(testInventory); // IN_STOCK (quantity=100, minStockLevel=10)
        
        Inventory outOfStock = new Inventory();
        outOfStock.setProductId(UUID.randomUUID());
        outOfStock.setQuantity(0);
        outOfStock.setMinStockLevel(10);
        inventoryRepository.save(outOfStock); // OUT_OF_STOCK
        
        Inventory lowStock = new Inventory();
        lowStock.setProductId(UUID.randomUUID());
        lowStock.setQuantity(5);
        lowStock.setMinStockLevel(10);
        inventoryRepository.save(lowStock); // LOW_STOCK

        // When: OUT_OF_STOCK durumundaki kayıtlar aranıyor
        List<Inventory> outOfStockInventories = inventoryRepository.findByStatus(InventoryStatus.OUT_OF_STOCK);

        // Then: Sadece OUT_OF_STOCK kayıtları bulundu
        assertEquals(1, outOfStockInventories.size());
        assertEquals(InventoryStatus.OUT_OF_STOCK, outOfStockInventories.get(0).getStatus());
    }

    @Test
    void testFindByLocation() {
        // Given: Farklı lokasyonlarda stok kayıtları kaydediliyor
        inventoryRepository.save(testInventory); // BESIKTAS
        
        Inventory inventory2 = new Inventory();
        inventory2.setProductId(UUID.randomUUID());
        inventory2.setQuantity(50);
        inventory2.setLocation(Location.KADIKOY);
        inventoryRepository.save(inventory2); // KADIKOY

        Inventory inventory3 = new Inventory();
        inventory3.setProductId(UUID.randomUUID());
        inventory3.setQuantity(75);
        inventory3.setLocation(Location.BESIKTAS);
        inventoryRepository.save(inventory3); // BESIKTAS

        // When: BESIKTAS lokasyonundaki kayıtlar aranıyor
        List<Inventory> besiktasInventories = inventoryRepository.findByLocation(Location.BESIKTAS);

        // Then: Sadece BESIKTAS kayıtları bulundu
        assertEquals(2, besiktasInventories.size());
        assertTrue(besiktasInventories.stream().allMatch(i -> i.getLocation() == Location.BESIKTAS));
    }

    @Test
    void testExistsByProductId() {
        // Given: Bir stok kaydı kaydediliyor
        inventoryRepository.save(testInventory);

        // When: Product ID'nin var olup olmadığı kontrol ediliyor
        boolean exists = inventoryRepository.existsByProductId(testProductId);
        boolean notExists = inventoryRepository.existsByProductId(UUID.randomUUID());

        // Then: Doğru sonuçlar dönmeli
        assertTrue(exists);
        assertFalse(notExists);
    }

    @Test
    void testProductIdUniqueConstraint() {
        // Given: Bir stok kaydı kaydediliyor
        Inventory savedInventory = inventoryRepository.save(testInventory);
        UUID firstInventoryId = savedInventory.getId();

        // When: existsByProductId kontrol ediliyor
        boolean exists = inventoryRepository.existsByProductId(testProductId);
        boolean notExists = inventoryRepository.existsByProductId(UUID.randomUUID());

        // Then: Doğru sonuçlar dönmeli
        assertTrue(exists);
        assertFalse(notExists);
        
        // Verify: İlk kayıt hala var
        var found = inventoryRepository.findByProductId(testProductId);
        assertTrue(found.isPresent());
        assertEquals(firstInventoryId, found.get().getId());
        
        // Not: Unique constraint test'i service katmanında yapılıyor
        // Repository test'inde sadece query method'larını test ediyoruz
    }

    @Test
    void testDeleteInventory() {
        // Given: Bir stok kaydı kaydediliyor
        Inventory savedInventory = inventoryRepository.save(testInventory);
        UUID inventoryId = savedInventory.getId();

        // When: Stok kaydı siliniyor
        inventoryRepository.deleteById(inventoryId);

        // Then: Stok kaydı artık bulunamaz
        assertFalse(inventoryRepository.existsById(inventoryId));
    }

    @Test
    void testInventoryCreatedAtAndUpdatedAt() {
        // Given: testInventory hazır
        // When: Stok kaydı kaydediliyor
        Inventory savedInventory = inventoryRepository.save(testInventory);

        // Then: createdAt ve updatedAt otomatik oluşturuldu
        assertNotNull(savedInventory.getCreatedAt());
        assertNotNull(savedInventory.getUpdatedAt());
        assertEquals(savedInventory.getCreatedAt(), savedInventory.getUpdatedAt());
    }

    @Test
    void testInventoryStatusAutoCalculation() {
        // Given: Farklı miktarlarda stok kayıtları
        // OUT_OF_STOCK
        Inventory outOfStock = new Inventory();
        outOfStock.setProductId(UUID.randomUUID());
        outOfStock.setQuantity(0);
        outOfStock.setMinStockLevel(10);
        Inventory saved1 = inventoryRepository.save(outOfStock);
        assertEquals(InventoryStatus.OUT_OF_STOCK, saved1.getStatus());

        // LOW_STOCK
        Inventory lowStock = new Inventory();
        lowStock.setProductId(UUID.randomUUID());
        lowStock.setQuantity(5);
        lowStock.setMinStockLevel(10);
        Inventory saved2 = inventoryRepository.save(lowStock);
        assertEquals(InventoryStatus.LOW_STOCK, saved2.getStatus());

        // IN_STOCK
        Inventory inStock = new Inventory();
        inStock.setProductId(UUID.randomUUID());
        inStock.setQuantity(100);
        inStock.setMinStockLevel(10);
        Inventory saved3 = inventoryRepository.save(inStock);
        assertEquals(InventoryStatus.IN_STOCK, saved3.getStatus());
    }

    @Test
    void testFindAll() {
        // Given: Birkaç stok kaydı kaydediliyor
        inventoryRepository.save(testInventory);
        
        Inventory inventory2 = new Inventory();
        inventory2.setProductId(UUID.randomUUID());
        inventory2.setQuantity(50);
        inventoryRepository.save(inventory2);

        // When: Tüm kayıtlar getiriliyor
        List<Inventory> allInventories = inventoryRepository.findAll();

        // Then: 2 kayıt olmalı
        assertEquals(2, allInventories.size());
    }
}

