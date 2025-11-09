package com.microservices.notification.messaging;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;

import com.microservices.notification.Consumer.OrderEventConsumer;
import com.microservices.notification.Event.OrderCreatedEvent;
import com.microservices.notification.Event.OrderStatusChangedEvent;
import com.microservices.notification.Model.Notification;
import com.microservices.notification.Model.NotificationStatus;
import com.microservices.notification.Model.NotificationType;
import com.microservices.notification.Service.NotificationService;

/**
 * Order Event Consumer Test
 * 
 * RabbitMQ'dan gelen event'leri işleme işlemlerini test eder
 */
@ExtendWith(MockitoExtension.class)
class OrderEventConsumerTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private OrderEventConsumer orderEventConsumer;

    private OrderCreatedEvent orderCreatedEvent;
    private OrderStatusChangedEvent orderStatusChangedEvent;
    private UUID testOrderId;
    private UUID testUserId;

    @BeforeEach
    void setUp() {
        testOrderId = UUID.randomUUID();
        testUserId = UUID.randomUUID();

        // OrderCreatedEvent oluştur
        OrderCreatedEvent.OrderItemInfo itemInfo = new OrderCreatedEvent.OrderItemInfo();
        itemInfo.setProductId(UUID.randomUUID());
        itemInfo.setProductName("Test Product");
        itemInfo.setQuantity(2);
        itemInfo.setPrice(new BigDecimal("75.00"));
        itemInfo.setSubtotal(new BigDecimal("150.00"));

        List<OrderCreatedEvent.OrderItemInfo> items = new ArrayList<>();
        items.add(itemInfo);

        orderCreatedEvent = new OrderCreatedEvent();
        orderCreatedEvent.setOrderId(testOrderId);
        orderCreatedEvent.setUserId(testUserId);
        orderCreatedEvent.setUserEmail("test@example.com");
        orderCreatedEvent.setUserName("Test User");
        orderCreatedEvent.setTotalAmount(new BigDecimal("150.00"));
        orderCreatedEvent.setShippingAddress("Test Address, Test Street 123");
        orderCreatedEvent.setCity("Istanbul");
        orderCreatedEvent.setZipCode("34000");
        orderCreatedEvent.setPhoneNumber("5551234567");
        orderCreatedEvent.setOrderDate(LocalDateTime.now());
        orderCreatedEvent.setOrderItems(items);

        // OrderStatusChangedEvent oluştur
        orderStatusChangedEvent = new OrderStatusChangedEvent();
        orderStatusChangedEvent.setOrderId(testOrderId);
        orderStatusChangedEvent.setUserId(testUserId);
        orderStatusChangedEvent.setUserEmail("test@example.com");
        orderStatusChangedEvent.setUserName("Test User");
        orderStatusChangedEvent.setOldStatus("PENDING");
        orderStatusChangedEvent.setNewStatus("CONFIRMED");
        orderStatusChangedEvent.setChangedAt(LocalDateTime.now());
    }

    @Test
    void testHandleOrderCreated() {
        // Given: Mock notification service
        Notification savedNotification = new Notification();
        savedNotification.setId(UUID.randomUUID());
        savedNotification.setStatus(NotificationStatus.PENDING);
        
        when(notificationService.createNotification(any(Notification.class)))
            .thenReturn(savedNotification);
        when(notificationService.sendNotification(savedNotification.getId()))
            .thenReturn(savedNotification);

        // Mock Message
        Message message = new Message("{}".getBytes(), new MessageProperties());

        // When: OrderCreatedEvent işleniyor
        orderEventConsumer.handleOrderCreated(orderCreatedEvent, message);

        // Then: Notification oluşturuldu ve gönderildi
        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationService, times(1)).createNotification(notificationCaptor.capture());
        verify(notificationService, times(1)).sendNotification(savedNotification.getId());

        // Notification doğru bilgilerle oluşturuldu mu?
        Notification capturedNotification = notificationCaptor.getValue();
        assertEquals("test@example.com", capturedNotification.getRecipient());
        assertEquals(NotificationType.EMAIL, capturedNotification.getType());
        assertEquals(testUserId, capturedNotification.getUserId());
        assertEquals("ORDER", capturedNotification.getRelatedEntityType());
        assertEquals(testOrderId, capturedNotification.getRelatedEntityId());
        assertNotNull(capturedNotification.getSubject());
        assertNotNull(capturedNotification.getMessage());
        assertTrue(capturedNotification.getSubject().contains("Siparişiniz Oluşturuldu"));
    }

    @Test
    void testHandleOrderStatusChanged() {
        // Given: Mock notification service
        Notification savedNotification = new Notification();
        savedNotification.setId(UUID.randomUUID());
        savedNotification.setStatus(NotificationStatus.PENDING);
        
        when(notificationService.createNotification(any(Notification.class)))
            .thenReturn(savedNotification);
        when(notificationService.sendNotification(savedNotification.getId()))
            .thenReturn(savedNotification);

        // When: OrderStatusChangedEvent işleniyor
        orderEventConsumer.handleOrderStatusChanged(orderStatusChangedEvent);

        // Then: Notification oluşturuldu ve gönderildi
        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationService, times(1)).createNotification(notificationCaptor.capture());
        verify(notificationService, times(1)).sendNotification(savedNotification.getId());

        // Notification doğru bilgilerle oluşturuldu mu?
        Notification capturedNotification = notificationCaptor.getValue();
        assertEquals("test@example.com", capturedNotification.getRecipient());
        assertEquals(NotificationType.EMAIL, capturedNotification.getType());
        assertEquals(testUserId, capturedNotification.getUserId());
        assertEquals("ORDER", capturedNotification.getRelatedEntityType());
        assertEquals(testOrderId, capturedNotification.getRelatedEntityId());
        assertNotNull(capturedNotification.getSubject());
        assertNotNull(capturedNotification.getMessage());
        assertTrue(capturedNotification.getSubject().contains("Sipariş Durumu Güncellendi"));
    }

    @Test
    void testHandleOrderCreatedWithException() {
        // Given: Notification service exception fırlatıyor
        when(notificationService.createNotification(any(Notification.class)))
            .thenThrow(new RuntimeException("Database error"));

        // Mock Message
        Message message = new Message("{}".getBytes(), new MessageProperties());

        // When & Then: Exception yakalanmalı ve işlem devam etmeli
        assertDoesNotThrow(() -> {
            orderEventConsumer.handleOrderCreated(orderCreatedEvent, message);
        });
    }

    @Test
    void testHandleOrderStatusChangedWithException() {
        // Given: Notification service exception fırlatıyor
        when(notificationService.createNotification(any(Notification.class)))
            .thenThrow(new RuntimeException("Database error"));

        // When & Then: Exception yakalanmalı ve işlem devam etmeli
        assertDoesNotThrow(() -> {
            orderEventConsumer.handleOrderStatusChanged(orderStatusChangedEvent);
        });
    }

    @Test
    void testOrderCreatedMessageContent() {
        // Given: Mock notification service
        Notification savedNotification = new Notification();
        savedNotification.setId(UUID.randomUUID());
        
        when(notificationService.createNotification(any(Notification.class)))
            .thenReturn(savedNotification);
        when(notificationService.sendNotification(savedNotification.getId()))
            .thenReturn(savedNotification);

        // Mock Message
        Message rabbitMessage = new Message("{}".getBytes(), new MessageProperties());

        // When: OrderCreatedEvent işleniyor
        orderEventConsumer.handleOrderCreated(orderCreatedEvent, rabbitMessage);

        // Then: Mesaj içeriği doğru mu?
        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationService).createNotification(notificationCaptor.capture());
        
        Notification notification = notificationCaptor.getValue();
        String message = notification.getMessage();
        
        assertTrue(message.contains("Test User"));
        assertTrue(message.contains(testOrderId.toString().substring(0, 8)));
        assertTrue(message.contains("150.00"));
        assertTrue(message.contains("Test Product"));
        assertTrue(message.contains("Test Address"));
    }

    @Test
    void testOrderStatusChangedMessageContent() {
        // Given: Mock notification service
        Notification savedNotification = new Notification();
        savedNotification.setId(UUID.randomUUID());
        
        when(notificationService.createNotification(any(Notification.class)))
            .thenReturn(savedNotification);
        when(notificationService.sendNotification(savedNotification.getId()))
            .thenReturn(savedNotification);

        // When: OrderStatusChangedEvent işleniyor
        orderEventConsumer.handleOrderStatusChanged(orderStatusChangedEvent);

        // Then: Mesaj içeriği doğru mu?
        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationService).createNotification(notificationCaptor.capture());
        
        Notification notification = notificationCaptor.getValue();
        String message = notification.getMessage();
        
        assertTrue(message.contains("Test User"));
        assertTrue(message.contains(testOrderId.toString().substring(0, 8)));
        assertTrue(message.contains("Beklemede") || message.contains("PENDING"));
        assertTrue(message.contains("Onaylandı") || message.contains("CONFIRMED"));
    }
}

