package com.microservices.order.messaging;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.microservices.order.Event.OrderCreatedEvent;
import com.microservices.order.Event.OrderStatusChangedEvent;

/**
 * Order Event Producer Test
 * 
 * Event DTO'larının yapısını test eder
 * Gerçek event gönderme testleri OrderServiceTest'te integration test olarak yapılıyor
 */
class OrderEventProducerTest {

    private UUID testOrderId;
    private UUID testUserId;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        testOrderId = UUID.randomUUID();
        testUserId = UUID.randomUUID();
    }

    // Not: Event gönderme testleri OrderServiceTest'te integration test olarak yapılıyor
    // Burada sadece event structure testleri yapılıyor

    @Test
    void testOrderCreatedEventStructure() {
        // Given: Event oluşturuluyor
        OrderCreatedEvent.OrderItemInfo itemInfo = new OrderCreatedEvent.OrderItemInfo();
        itemInfo.setProductId(UUID.randomUUID());
        itemInfo.setProductName("Test Product");
        itemInfo.setQuantity(2);
        itemInfo.setPrice(new BigDecimal("75.00"));
        itemInfo.setSubtotal(new BigDecimal("150.00"));

        List<OrderCreatedEvent.OrderItemInfo> items = new ArrayList<>();
        items.add(itemInfo);

        OrderCreatedEvent event = new OrderCreatedEvent(
            testOrderId,
            testUserId,
            "test@example.com",
            "Test User",
            new BigDecimal("150.00"),
            "Test Address",
            "Istanbul",
            "34000",
            "5551234567",
            LocalDateTime.now(),
            items
        );

        // Then: Event doğru yapıda
        assertNotNull(event);
        assertEquals(testOrderId, event.getOrderId());
        assertEquals(testUserId, event.getUserId());
        assertEquals("test@example.com", event.getUserEmail());
        assertEquals("Test User", event.getUserName());
        assertEquals(1, event.getOrderItems().size());
    }

    @Test
    void testOrderStatusChangedEventStructure() {
        // Given: Event oluşturuluyor
        OrderStatusChangedEvent event = new OrderStatusChangedEvent(
            testOrderId,
            testUserId,
            "test@example.com",
            "Test User",
            "PENDING",
            "CONFIRMED",
            LocalDateTime.now()
        );

        // Then: Event doğru yapıda
        assertNotNull(event);
        assertEquals(testOrderId, event.getOrderId());
        assertEquals(testUserId, event.getUserId());
        assertEquals("test@example.com", event.getUserEmail());
        assertEquals("PENDING", event.getOldStatus());
        assertEquals("CONFIRMED", event.getNewStatus());
    }
}

