package com.microservices.inventory.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.microservices.inventory.Exception.DuplicateResourceException;
import com.microservices.inventory.Exception.ResourceNotFoundException;
import com.microservices.inventory.Model.Inventory;
import com.microservices.inventory.Model.InventoryStatus;
import com.microservices.inventory.Model.Location;
import com.microservices.inventory.Repository.InventoryRepository;

/**
 * Inventory Service
 * Stok yönetimi için business logic
 * 
 * Önemli Notlar:
 * - Her product için tek bir inventory kaydı olmalı (productId unique)
 * - Rezerve işlemleri stok kontrolü yapmalı
 * - Status otomatik hesaplanır (@PreUpdate)
 */
@Service
public class InventoryService {
    private final InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    /**
     * Tüm stok kayıtlarını getir
     */
    public List<Inventory> getAllInventories() {
        return inventoryRepository.findAll();
    }

    /**
     * ID'ye göre stok getir
     */
    public Inventory getInventoryById(UUID id) {
        return inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", "id", id));
    }

    /**
     * Product ID'ye göre stok getir
     * EN ÖNEMLİ METHOD!
     * Order Service ve Product Service bu method'u kullanır
     */
    public Inventory getInventoryByProductId(UUID productId) {
        return inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", "productId", productId));
    }

    /**
     * Kullanılabilir stok miktarını getir
     * quantity - reservedQuantity
     */
    public Integer getAvailableQuantity(UUID productId) {
        Inventory inventory = getInventoryByProductId(productId);
        return inventory.getAvailableQuantity();
    }

    /**
     * Stok durumuna göre filtrele
     * Repository'de query ile yapılıyor (performans için)
     */
    public List<Inventory> getInventoriesByStatus(InventoryStatus status) {
        return inventoryRepository.findByStatus(status);
    }

    /**
     * Lokasyona göre filtrele
     * Repository'de query ile yapılıyor (performans için)
     */
    public List<Inventory> getInventoriesByLocation(Location location) {
        return inventoryRepository.findByLocation(location);
    }

    /**
     * Toplu stok kontrolü
     * Sepet için kullanılır
     * 
     * @param productQuantities Map<ProductId, RequiredQuantity>
     * @return Map<ProductId, IsAvailable> - true = stokta var, false = stokta yok
     */
    public Map<UUID, Boolean> checkStockAvailability(Map<UUID, Integer> productQuantities) {
        Map<UUID, Boolean> availabilityMap = new HashMap<>();
        
        for (Map.Entry<UUID, Integer> entry : productQuantities.entrySet()) {
            UUID productId = entry.getKey();
            Integer requiredQuantity = entry.getValue();
            
            try {
                Inventory inventory = getInventoryByProductId(productId);
                // Kullanılabilir stok yeterli mi?
                boolean isAvailable = inventory.hasEnoughStock(requiredQuantity);
                availabilityMap.put(productId, isAvailable);
            } catch (ResourceNotFoundException e) {
                // Stok kaydı yoksa stokta yok demektir
                availabilityMap.put(productId, false);
            }
        }
        
        return availabilityMap;
    }

    /**
     * Yeni stok kaydı oluştur
     * Product oluşturulduğunda çağrılır
     */
    public Inventory createInventory(Inventory inventory) {
        // Duplicate check: Aynı productId'ye sahip inventory var mı?
        if (inventory.getProductId() != null && 
            inventoryRepository.existsByProductId(inventory.getProductId())) {
            throw new DuplicateResourceException("Inventory", "productId", inventory.getProductId());
        }
        
        return inventoryRepository.save(inventory);
    }

    /**
     * Stok kaydını tamamen güncelle
     * Partial update yapıyor (null olmayan field'ları günceller)
     */
    public Inventory updateInventory(UUID id, Inventory inventoryDetails) {
        Inventory inventory = getInventoryById(id);
        
        // ProductId değiştirilemez (unique constraint)
        // Sadece diğer field'lar güncellenir
        
        if (inventoryDetails.getQuantity() != null) {
            inventory.setQuantity(inventoryDetails.getQuantity());
        }
        if (inventoryDetails.getReservedQuantity() != null) {
            inventory.setReservedQuantity(inventoryDetails.getReservedQuantity());
        }
        if (inventoryDetails.getMinStockLevel() != null) {
            inventory.setMinStockLevel(inventoryDetails.getMinStockLevel());
        }
        if (inventoryDetails.getMaxStockLevel() != null) {
            inventory.setMaxStockLevel(inventoryDetails.getMaxStockLevel());
        }
        if (inventoryDetails.getLocation() != null) {
            inventory.setLocation(inventoryDetails.getLocation());
        }
        
        // Status otomatik hesaplanır (@PreUpdate)
        return inventoryRepository.save(inventory);
    }

    /**
     * Sadece stok miktarını güncelle
     * Yeni ürün geldiğinde veya stok azaldığında kullanılır
     */
    public Inventory updateQuantity(UUID id, Integer quantity) {
        Inventory inventory = getInventoryById(id);
        
        // Negatif olamaz kontrolü
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        
        inventory.setQuantity(quantity);
        // Status otomatik hesaplanır (@PreUpdate)
        return inventoryRepository.save(inventory);
    }

    /**
     * Stok rezerve et (sipariş için)
     * Sipariş verildiğinde Order Service bu method'u çağırır
     * 
     * @param id Inventory ID
     * @param quantity Rezerve edilecek miktar
     * @return Güncellenmiş inventory
     */
    public Inventory reserveStock(UUID id, Integer quantity) {
        Inventory inventory = getInventoryById(id);
        
        // Stok yeterli mi kontrolü
        if (!inventory.hasEnoughStock(quantity)) {
            throw new IllegalArgumentException(
                String.format("Insufficient stock. Available: %d, Required: %d", 
                    inventory.getAvailableQuantity(), quantity));
        }
        
        // Rezerve edilmiş miktarı artır
        int newReservedQuantity = (inventory.getReservedQuantity() != null ? 
                                   inventory.getReservedQuantity() : 0) + quantity;
        inventory.setReservedQuantity(newReservedQuantity);
        
        // Status otomatik hesaplanır (@PreUpdate)
        return inventoryRepository.save(inventory);
    }

    /**
     * Rezerve edilmiş stoku serbest bırak
     * Sipariş iptal edildiğinde veya başarısız olduğunda kullanılır
     * 
     * @param id Inventory ID
     * @param quantity Serbest bırakılacak miktar
     * @return Güncellenmiş inventory
     */
    public Inventory releaseReservedStock(UUID id, Integer quantity) {
        Inventory inventory = getInventoryById(id);
        
        // Rezerve edilmiş miktar yeterli mi?
        int currentReserved = inventory.getReservedQuantity() != null ? 
                             inventory.getReservedQuantity() : 0;
        
        if (currentReserved < quantity) {
            throw new IllegalArgumentException(
                String.format("Cannot release more than reserved. Reserved: %d, Requested: %d", 
                    currentReserved, quantity));
        }
        
        // Rezerve edilmiş miktarı azalt
        inventory.setReservedQuantity(currentReserved - quantity);
        
        // Status otomatik hesaplanır (@PreUpdate)
        return inventoryRepository.save(inventory);
    }

    /**
     * Stok kaydını sil
     */
    public void deleteInventory(UUID id) {
        if (!inventoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Inventory", "id", id);
        }
        inventoryRepository.deleteById(id);
    }
}
