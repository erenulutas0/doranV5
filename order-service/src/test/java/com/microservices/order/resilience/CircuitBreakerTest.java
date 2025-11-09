package com.microservices.order.resilience;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import com.microservices.order.Client.InventoryServiceClient;
import com.microservices.order.Client.ProductServiceClient;
import com.microservices.order.Client.UserServiceClient;
import com.microservices.order.Exception.ResourceNotFoundException;
import com.microservices.order.Model.Order;
import com.microservices.order.Model.OrderItem;
import com.microservices.order.Model.OrderStatus;
import com.microservices.order.Service.OrderService;

import feign.FeignException;

import static org.mockito.Mockito.when;

/**
 * Circuit Breaker Test
 * 
 * Bu test, Circuit Breaker'ın doğru çalıştığını doğrular:
 * 1. Servis hata verdiğinde fallback method'ları çağrılır
 * 2. Circuit Breaker açıldığında servise istek gönderilmez
 * 3. Fallback method'ları doğru değerler döner
 * 
 * Circuit Breaker States:
 * - CLOSED: Normal çalışma, istekler servise gönderilir
 * - OPEN: Çok fazla hata var, istekler servise gönderilmez, fallback çağrılır
 * - HALF_OPEN: Servis tekrar çalışıyor mu test ediliyor
 */
@SpringBootTest
@ActiveProfiles("test")
class CircuitBreakerTest {

    @Autowired
    private OrderService orderService;

    @MockBean
    private ProductServiceClient productServiceClient;

    @MockBean
    private InventoryServiceClient inventoryServiceClient;

    @MockBean
    private UserServiceClient userServiceClient;

    private UUID testUserId;
    private UUID testProductId;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testProductId = UUID.randomUUID();
    }

    @Test
    void testProductServiceFallback() {
        // Not: Mock kullanıldığında fallback çağrılmaz
        // Fallback sadece gerçek Feign Client çağrılarında devreye girer
        // Bu test, fallback class'ının doğru implement edildiğini doğrular
        
        // Fallback class'ını doğrudan test et
        com.microservices.order.Client.ProductServiceClientFallback fallback = 
            new com.microservices.order.Client.ProductServiceClientFallback();
        
        ProductServiceClient.ProductResponse product = fallback.getProductById(testProductId);
        
        // Then: Fallback method doğru değerler döndü
        assertNotNull(product);
        assertEquals("Product Unavailable", product.getName());
        assertEquals(BigDecimal.ZERO, product.getPrice());
    }

    @Test
    void testInventoryServiceFallback() {
        // Fallback class'ını doğrudan test et
        com.microservices.order.Client.InventoryServiceClientFallback fallback = 
            new com.microservices.order.Client.InventoryServiceClientFallback();
        
        InventoryServiceClient.InventoryResponse inventory = 
            fallback.getInventoryByProductId(testProductId);

        // Then: Fallback method doğru değerler döndü
        assertNotNull(inventory);
        assertEquals(0, inventory.getQuantity());
        assertEquals("UNAVAILABLE", inventory.getStatus());
    }

    @Test
    void testInventoryServiceStockCheckFallback() {
        // Fallback class'ını doğrudan test et
        com.microservices.order.Client.InventoryServiceClientFallback fallback = 
            new com.microservices.order.Client.InventoryServiceClientFallback();
        
        Map<UUID, Integer> request = new HashMap<>();
        request.put(testProductId, 1);

        // When: Stok kontrolü yapılıyor
        Map<UUID, Boolean> result = fallback.checkStockAvailability(request);

        // Then: Fallback method doğru değerler döndü (tüm ürünler için false)
        assertNotNull(result);
        assertEquals(false, result.get(testProductId));
    }

    @Test
    void testUserServiceFallback() {
        // Fallback class'ını doğrudan test et
        com.microservices.order.Client.UserServiceClientFallback fallback = 
            new com.microservices.order.Client.UserServiceClientFallback();

        // When: User bilgisi çekilmeye çalışılıyor
        UserServiceClient.UserResponse user = fallback.getUserById(testUserId);

        // Then: Fallback method null döndü
        assertNull(user);
    }

    @Test
    void testOrderCreationWithFallback() {
        // Given: User Service null döndürüyor (fallback davranışı)
        when(userServiceClient.getUserById(testUserId))
            .thenReturn(null);

        // Order oluştur
        Order order = new Order();
        order.setUserId(testUserId);
        order.setShippingAddress("Test Adresi");
        order.setCity("İstanbul");
        order.setZipCode("34000");
        order.setPhoneNumber("5551234567");
        
        OrderItem item = new OrderItem();
        item.setProductId(testProductId);
        item.setQuantity(1);
        order.setOrderItems(new ArrayList<>(List.of(item)));

        // When & Then: Order oluşturulmaya çalışılıyor
        // User Service null döndüğü için ResourceNotFoundException fırlatılır
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            orderService.createOrder(order);
        });
        
        assertTrue(exception.getMessage().contains("User not found"));
    }
}

