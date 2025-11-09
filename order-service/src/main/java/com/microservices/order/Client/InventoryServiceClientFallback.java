package com.microservices.order.Client;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.microservices.order.Client.InventoryServiceClient.InventoryResponse;

/**
 * Inventory Service Client Fallback
 * 
 * Circuit Breaker açıldığında veya Inventory Service hata verdiğinde
 * bu fallback method'ları çağrılır.
 */
@Component
public class InventoryServiceClientFallback implements InventoryServiceClient {

    @Override
    public InventoryResponse getInventoryByProductId(UUID productId) {
        // Fallback: Default değerler döndür
        InventoryResponse fallbackResponse = new InventoryResponse();
        fallbackResponse.setId(UUID.randomUUID());
        fallbackResponse.setProductId(productId);
        fallbackResponse.setQuantity(0);
        fallbackResponse.setReservedQuantity(0);
        fallbackResponse.setStatus("UNAVAILABLE");
        
        System.err.println("Inventory Service Fallback: Inventory for product " + productId + " unavailable");
        
        return fallbackResponse;
    }

    @Override
    public Map<UUID, Boolean> checkStockAvailability(Map<UUID, Integer> request) {
        // Fallback: Tüm ürünler için stok yok döndür
        // Bu, sipariş oluşturmayı engeller (güvenli taraf)
        Map<UUID, Boolean> fallbackResponse = new HashMap<>();
        for (UUID productId : request.keySet()) {
            fallbackResponse.put(productId, false);  // Stok yok
        }
        
        System.err.println("Inventory Service Fallback: Stock check unavailable, returning false for all products");
        
        return fallbackResponse;
    }

    @Override
    public InventoryResponse reserveStock(UUID inventoryId, Integer quantity) {
        // Fallback: Stok rezerve edilemedi
        InventoryResponse fallbackResponse = new InventoryResponse();
        fallbackResponse.setId(inventoryId);
        fallbackResponse.setQuantity(0);
        fallbackResponse.setReservedQuantity(0);
        fallbackResponse.setStatus("UNAVAILABLE");
        
        System.err.println("Inventory Service Fallback: Cannot reserve stock for inventory " + inventoryId);
        
        return fallbackResponse;
    }

    @Override
    public InventoryResponse releaseReservedStock(UUID inventoryId, Integer quantity) {
        // Fallback: Rezerve stok geri verilemedi
        InventoryResponse fallbackResponse = new InventoryResponse();
        fallbackResponse.setId(inventoryId);
        fallbackResponse.setQuantity(0);
        fallbackResponse.setReservedQuantity(0);
        fallbackResponse.setStatus("UNAVAILABLE");
        
        System.err.println("Inventory Service Fallback: Cannot release stock for inventory " + inventoryId);
        
        return fallbackResponse;
    }

    @Override
    public Integer getAvailableQuantity(UUID productId) {
        // Fallback: Kullanılabilir stok yok
        System.err.println("Inventory Service Fallback: Available quantity unavailable for product " + productId);
        return 0;
    }
}

