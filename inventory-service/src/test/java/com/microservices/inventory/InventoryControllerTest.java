package com.microservices.inventory;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.inventory.Controller.InventoryController;
import com.microservices.inventory.Exception.ResourceNotFoundException;
import com.microservices.inventory.Model.Inventory;
import com.microservices.inventory.Model.InventoryStatus;
import com.microservices.inventory.Model.Location;
import com.microservices.inventory.Service.InventoryService;

/**
 * InventoryController için Integration Test
 * @WebMvcTest: Sadece web katmanını test eder, Controller'lar için
 * MockMvc: HTTP isteklerini simüle eder
 */
@WebMvcTest(InventoryController.class)
class InventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;  // HTTP isteklerini simüle eder

    @MockBean
    private InventoryService inventoryService;  // InventoryService mock'lanıyor

    @Autowired
    private ObjectMapper objectMapper;  // JSON dönüşümleri için

    private Inventory testInventory;
    private UUID testInventoryId;
    private UUID testProductId;

    @BeforeEach
    void setUp() {
        testInventoryId = UUID.randomUUID();
        testProductId = UUID.randomUUID();
        
        testInventory = new Inventory();
        testInventory.setId(testInventoryId);
        testInventory.setProductId(testProductId);
        testInventory.setQuantity(100);
        testInventory.setReservedQuantity(0);
        testInventory.setMinStockLevel(10);
        testInventory.setMaxStockLevel(500);
        testInventory.setLocation(Location.BESIKTAS);
        testInventory.setStatus(InventoryStatus.IN_STOCK);
    }

    @Test
    void testCreateInventory() throws Exception {
        // Given: Mock service davranışı
        when(inventoryService.createInventory(any(Inventory.class))).thenReturn(testInventory);

        // When & Then: POST isteği gönderiliyor
        mockMvc.perform(post("/inventory")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testInventory)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.productId").value(testProductId.toString()))
                .andExpect(jsonPath("$.quantity").value(100))
                .andExpect(jsonPath("$.location").value("BESIKTAS"));

        // Verify: Service metodu çağrıldı
        verify(inventoryService, times(1)).createInventory(any(Inventory.class));
    }

    @Test
    void testGetInventoryById() throws Exception {
        // Given: Mock service davranışı
        when(inventoryService.getInventoryById(testInventoryId)).thenReturn(testInventory);

        // When & Then: GET isteği gönderiliyor
        mockMvc.perform(get("/inventory/{id}", testInventoryId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testInventoryId.toString()))
                .andExpect(jsonPath("$.productId").value(testProductId.toString()))
                .andExpect(jsonPath("$.quantity").value(100));

        verify(inventoryService, times(1)).getInventoryById(testInventoryId);
    }

    @Test
    void testGetInventoryByIdNotFound() throws Exception {
        // Given: Stok kaydı bulunamıyor
        UUID nonExistentId = UUID.randomUUID();
        when(inventoryService.getInventoryById(nonExistentId))
                .thenThrow(new ResourceNotFoundException("Inventory", "id", nonExistentId));

        // When & Then: 404 dönmeli
        mockMvc.perform(get("/inventory/{id}", nonExistentId))
                .andExpect(status().isNotFound());

        verify(inventoryService, times(1)).getInventoryById(nonExistentId);
    }

    @Test
    void testGetInventoryByProductId() throws Exception {
        // Given: Mock service davranışı
        when(inventoryService.getInventoryByProductId(testProductId)).thenReturn(testInventory);

        // When & Then: GET isteği gönderiliyor
        mockMvc.perform(get("/inventory/product/{productId}", testProductId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(testProductId.toString()))
                .andExpect(jsonPath("$.quantity").value(100));

        verify(inventoryService, times(1)).getInventoryByProductId(testProductId);
    }

    @Test
    void testGetAvailableQuantity() throws Exception {
        // Given: Mock service davranışı
        when(inventoryService.getAvailableQuantity(testProductId)).thenReturn(70);

        // When & Then: GET isteği gönderiliyor
        mockMvc.perform(get("/inventory/product/{productId}/available", testProductId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(70));

        verify(inventoryService, times(1)).getAvailableQuantity(testProductId);
    }

    @Test
    void testGetInventoriesByStatus() throws Exception {
        // Given: Mock service davranışı
        when(inventoryService.getInventoriesByStatus(InventoryStatus.LOW_STOCK))
                .thenReturn(List.of(testInventory));

        // When & Then: GET isteği gönderiliyor
        mockMvc.perform(get("/inventory/status")
                .param("status", "LOW_STOCK"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(inventoryService, times(1)).getInventoriesByStatus(InventoryStatus.LOW_STOCK);
    }

    @Test
    void testGetInventoriesByLocation() throws Exception {
        // Given: Mock service davranışı
        when(inventoryService.getInventoriesByLocation(Location.BESIKTAS))
                .thenReturn(List.of(testInventory));

        // When & Then: GET isteği gönderiliyor
        mockMvc.perform(get("/inventory/location")
                .param("location", "BESIKTAS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(inventoryService, times(1)).getInventoriesByLocation(Location.BESIKTAS);
    }

    @Test
    void testCheckStockAvailability() throws Exception {
        // Given: Mock service davranışı
        UUID productId2 = UUID.randomUUID();
        Map<UUID, Integer> request = Map.of(
            testProductId, 50,
            productId2, 30
        );
        Map<UUID, Boolean> response = Map.of(
            testProductId, true,
            productId2, false
        );
        when(inventoryService.checkStockAvailability(anyMap())).thenReturn(response);

        // When & Then: POST isteği gönderiliyor
        mockMvc.perform(post("/inventory/check")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isMap());

        verify(inventoryService, times(1)).checkStockAvailability(anyMap());
    }

    @Test
    void testUpdateInventory() throws Exception {
        // Given: Mock service davranışı
        Inventory updatedInventory = new Inventory();
        updatedInventory.setId(testInventoryId);
        updatedInventory.setProductId(testProductId);
        updatedInventory.setQuantity(200);
        updatedInventory.setLocation(Location.KADIKOY);

        when(inventoryService.updateInventory(eq(testInventoryId), any(Inventory.class)))
                .thenReturn(updatedInventory);

        // When & Then: PUT isteği gönderiliyor
        mockMvc.perform(put("/inventory/{id}", testInventoryId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedInventory)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(200))
                .andExpect(jsonPath("$.location").value("KADIKOY"));

        verify(inventoryService, times(1)).updateInventory(eq(testInventoryId), any(Inventory.class));
    }

    @Test
    void testUpdateQuantity() throws Exception {
        // Given: Mock service davranışı
        Inventory updatedInventory = new Inventory();
        updatedInventory.setId(testInventoryId);
        updatedInventory.setQuantity(150);
        when(inventoryService.updateQuantity(testInventoryId, 150)).thenReturn(updatedInventory);

        // When & Then: PATCH isteği gönderiliyor
        mockMvc.perform(patch("/inventory/{id}/quantity", testInventoryId)
                .param("quantity", "150"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(150));

        verify(inventoryService, times(1)).updateQuantity(testInventoryId, 150);
    }

    @Test
    void testReserveStock() throws Exception {
        // Given: Mock service davranışı
        Inventory reservedInventory = new Inventory();
        reservedInventory.setId(testInventoryId);
        reservedInventory.setReservedQuantity(30);
        when(inventoryService.reserveStock(testInventoryId, 30)).thenReturn(reservedInventory);

        // When & Then: PATCH isteği gönderiliyor
        mockMvc.perform(patch("/inventory/{id}/reserve", testInventoryId)
                .param("quantity", "30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reservedQuantity").value(30));

        verify(inventoryService, times(1)).reserveStock(testInventoryId, 30);
    }

    @Test
    void testReleaseReservedStock() throws Exception {
        // Given: Mock service davranışı
        Inventory releasedInventory = new Inventory();
        releasedInventory.setId(testInventoryId);
        releasedInventory.setReservedQuantity(10);
        when(inventoryService.releaseReservedStock(testInventoryId, 20)).thenReturn(releasedInventory);

        // When & Then: PATCH isteği gönderiliyor
        mockMvc.perform(patch("/inventory/{id}/release", testInventoryId)
                .param("quantity", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reservedQuantity").value(10));

        verify(inventoryService, times(1)).releaseReservedStock(testInventoryId, 20);
    }

    @Test
    void testDeleteInventory() throws Exception {
        // Given: Mock service davranışı
        doNothing().when(inventoryService).deleteInventory(testInventoryId);

        // When & Then: DELETE isteği gönderiliyor
        mockMvc.perform(delete("/inventory/{id}", testInventoryId))
                .andExpect(status().isNoContent());

        verify(inventoryService, times(1)).deleteInventory(testInventoryId);
    }

    @Test
    void testDeleteInventoryNotFound() throws Exception {
        // Given: Stok kaydı bulunamıyor
        UUID nonExistentId = UUID.randomUUID();
        doThrow(new ResourceNotFoundException("Inventory", "id", nonExistentId))
                .when(inventoryService).deleteInventory(nonExistentId);

        // When & Then: 404 dönmeli
        mockMvc.perform(delete("/inventory/{id}", nonExistentId))
                .andExpect(status().isNotFound());

        verify(inventoryService, times(1)).deleteInventory(nonExistentId);
    }

    @Test
    void testGetAllInventories() throws Exception {
        // Given: Mock service davranışı
        when(inventoryService.getAllInventories()).thenReturn(List.of(testInventory));

        // When & Then: GET isteği gönderiliyor
        mockMvc.perform(get("/inventory"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(inventoryService, times(1)).getAllInventories();
    }
}

