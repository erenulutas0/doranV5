package com.microservices.inventory.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.microservices.inventory.Model.Inventory;
import com.microservices.inventory.Model.InventoryStatus;
import com.microservices.inventory.Model.Location;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, UUID>{
    /**
     * Product ID'ye göre stok kaydı bul
     * Her product için tek bir inventory kaydı olmalı (unique constraint)
     */
    Optional<Inventory> findByProductId(UUID productId);
    
    /**
     * Stok durumuna göre filtrele
     * Birden fazla kayıt olabilir, bu yüzden List döndürür
     */
    List<Inventory> findByStatus(InventoryStatus status);
    
    /**
     * Lokasyona göre filtrele
     * Birden fazla kayıt olabilir, bu yüzden List döndürür
     */
    List<Inventory> findByLocation(Location location);
    
    /**
     * Product ID'nin zaten var olup olmadığını kontrol et
     * Duplicate check için kullanılır
     */
    boolean existsByProductId(UUID productId);
}
