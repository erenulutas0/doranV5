package com.microservices.order.messaging;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import com.microservices.order.Client.InventoryServiceClient;
import com.microservices.order.Client.ProductServiceClient;
import com.microservices.order.Client.UserServiceClient;
import com.microservices.order.Config.RabbitMQConfig;
import com.microservices.order.Event.OrderCreatedEvent;
import com.microservices.order.Event.OrderStatusChangedEvent;
import com.microservices.order.Model.Order;
import com.microservices.order.Model.OrderItem;
import com.microservices.order.Model.OrderStatus;
import com.microservices.order.Repository.OrderRepository;
import com.microservices.order.Service.OrderService;

/**
 * Order Service RabbitMQ Integration Test
 * 
 * OrderService'in RabbitMQ'ya event gönderme işlemlerini test eder
 * MockRabbitTemplate kullanarak gerçek RabbitMQ'ya bağlanmadan test yapar
 */
@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class OrderServiceRabbitMQIntegrationTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @MockBean
    private ProductServiceClient productServiceClient;

    @MockBean
    private InventoryServiceClient inventoryServiceClient;

    @MockBean
    private UserServiceClient userServiceClient;

    @MockBean
    private RabbitTemplate rabbitTemplate;

    private Order testOrder;
    private UserServiceClient.UserResponse testUser;
    private ProductServiceClient.ProductResponse testProduct;
    private InventoryServiceClient.InventoryResponse testInventory;
    private UUID testOrderId;
    private UUID testUserId;
    private UUID testProductId;

    @BeforeEach
    void setUp() {
        testOrderId = UUID.randomUUID();
        testUserId = UUID.randomUUID();
        testProductId = UUID.randomUUID();

        // Test User
        testUser = new UserServiceClient.UserResponse();
        testUser.setId(testUserId);
        testUser.setEmail("test@example.com");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setAddress("Test Address, Test Street 123");
        testUser.setCity("Istanbul");
        testUser.setZip("34000");
        testUser.setPhone("5551234567");

        // Test Product
        testProduct = new ProductServiceClient.ProductResponse();
        testProduct.setId(testProductId);
        testProduct.setName("Test Product");
        testProduct.setPrice(new BigDecimal("75.00"));

        // Test Inventory
        testInventory = new InventoryServiceClient.InventoryResponse();
        testInventory.setId(UUID.randomUUID());
        testInventory.setProductId(testProductId);
        testInventory.setQuantity(10);
        testInventory.setReservedQuantity(0);
        testInventory.setStatus("IN_STOCK");

        // Test Order
        testOrder = new Order();
        testOrder.setUserId(testUserId);
        testOrder.setShippingAddress("Test Address, Test Street 123");
        testOrder.setCity("Istanbul");
        testOrder.setZipCode("34000");
        testOrder.setPhoneNumber("5551234567");

        OrderItem orderItem = new OrderItem();
        orderItem.setProductId(testProductId);
        orderItem.setQuantity(2);
        testOrder.addOrderItem(orderItem);
    }

    @Test
    void testCreateOrderSendsRabbitMQEvent() {
        // Given: Mock service responses
        when(userServiceClient.getUserById(testUserId)).thenReturn(testUser);
        when(productServiceClient.getProductById(testProductId)).thenReturn(testProduct);
        when(inventoryServiceClient.getInventoryByProductId(testProductId)).thenReturn(testInventory);
        
        Map<UUID, Boolean> stockAvailability = new HashMap<>();
        stockAvailability.put(testProductId, true);
        when(inventoryServiceClient.checkStockAvailability(anyMap())).thenReturn(stockAvailability);

        // When: Order oluşturuluyor
        Order createdOrder = orderService.createOrder(testOrder);

        // Then: RabbitMQ'ya OrderCreatedEvent gönderilmiş olmalı
        verify(rabbitTemplate, times(1)).convertAndSend(
            eq(RabbitMQConfig.ORDER_CREATED_QUEUE),
            org.mockito.ArgumentMatchers.any(OrderCreatedEvent.class)
        );

        // Order başarıyla oluşturuldu
        assertNotNull(createdOrder.getId());
        assertEquals(testUserId, createdOrder.getUserId());
    }

    @Test
    void testUpdateOrderStatusSendsRabbitMQEvent() {
        // Given: Bir order oluşturuluyor
        when(userServiceClient.getUserById(testUserId)).thenReturn(testUser);
        when(productServiceClient.getProductById(testProductId)).thenReturn(testProduct);
        when(inventoryServiceClient.getInventoryByProductId(testProductId)).thenReturn(testInventory);
        
        Map<UUID, Boolean> stockAvailability = new HashMap<>();
        stockAvailability.put(testProductId, true);
        when(inventoryServiceClient.checkStockAvailability(anyMap())).thenReturn(stockAvailability);

        Order createdOrder = orderService.createOrder(testOrder);
        UUID orderId = createdOrder.getId();

        // Mock'ları temizle
        reset(rabbitTemplate);

        // When: Order durumu güncelleniyor
        when(userServiceClient.getUserById(testUserId)).thenReturn(testUser);
        Order updatedOrder = orderService.updateOrderStatus(orderId, OrderStatus.CONFIRMED);

        // Then: RabbitMQ'ya OrderStatusChangedEvent gönderilmiş olmalı
        verify(rabbitTemplate, times(1)).convertAndSend(
            eq(RabbitMQConfig.ORDER_STATUS_CHANGED_QUEUE),
            org.mockito.ArgumentMatchers.any(OrderStatusChangedEvent.class)
        );

        // Order durumu güncellendi
        assertEquals(OrderStatus.CONFIRMED, updatedOrder.getStatus());
    }

    @Test
    void testRabbitMQFailureDoesNotBlockOrderCreation() {
        // Given: RabbitMQ hatası simüle ediliyor
        when(userServiceClient.getUserById(testUserId)).thenReturn(testUser);
        when(productServiceClient.getProductById(testProductId)).thenReturn(testProduct);
        when(inventoryServiceClient.getInventoryByProductId(testProductId)).thenReturn(testInventory);
        
        Map<UUID, Boolean> stockAvailability = new HashMap<>();
        stockAvailability.put(testProductId, true);
        when(inventoryServiceClient.checkStockAvailability(anyMap())).thenReturn(stockAvailability);
        
        // RabbitMQ exception fırlatıyor
        doThrow(new RuntimeException("RabbitMQ connection error"))
            .when(rabbitTemplate).convertAndSend(
                org.mockito.ArgumentMatchers.anyString(), 
                org.mockito.ArgumentMatchers.any(OrderCreatedEvent.class)
            );

        // When: Order oluşturuluyor
        // Then: Order yine de oluşturulmalı (RabbitMQ hatası engellememeli)
        assertDoesNotThrow(() -> {
            Order createdOrder = orderService.createOrder(testOrder);
            assertNotNull(createdOrder.getId());
        });
    }
}

