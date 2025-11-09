package com.microservices.order;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.microservices.order.Model.Order;
import com.microservices.order.Model.OrderItem;
import com.microservices.order.Model.OrderStatus;
import com.microservices.order.Repository.OrderRepository;

/**
 * OrderRepository için Integration Test
 * @DataJpaTest: JPA Repository'leri test eder, gerçek veritabanı işlemleri yapar
 */
@DataJpaTest
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    private Order testOrder;
    private UUID testUserId;
    private UUID testProductId;

    @BeforeEach
    void setUp() {
        // Her test öncesi veritabanı temizlenir (@DataJpaTest sayesinde)
        testUserId = UUID.randomUUID();
        testProductId = UUID.randomUUID();
        
        testOrder = new Order();
        testOrder.setUserId(testUserId);
        testOrder.setShippingAddress("Test Adresi, Levent");
        testOrder.setCity("İstanbul");
        testOrder.setZipCode("34394");
        testOrder.setPhoneNumber("5551234567");
        
        // OrderItem oluştur
        List<OrderItem> orderItems = new ArrayList<>();
        OrderItem item = new OrderItem();
        item.setProductId(testProductId);
        item.setProductName("MacBook Pro");
        item.setQuantity(1);
        item.setPrice(new BigDecimal("45000.00"));
        // OrderItem'ı Order'a ekle (ilişkiyi kur)
        testOrder.addOrderItem(item);
    }

    @Test
    void testSaveOrder() {
        // Given: testOrder hazır
        // When: Sipariş kaydediliyor
        Order savedOrder = orderRepository.save(testOrder);

        // Then: Sipariş başarıyla kaydedildi
        assertNotNull(savedOrder.getId());
        assertEquals(testUserId, savedOrder.getUserId());
        assertEquals(OrderStatus.PENDING, savedOrder.getStatus());
        assertNotNull(savedOrder.getOrderDate());
        assertEquals(1, savedOrder.getOrderItems().size());
    }

    @Test
    void testFindByUserId() {
        // Given: Farklı kullanıcılar için siparişler kaydediliyor
        orderRepository.save(testOrder); // testUserId
        
        Order order2 = new Order();
        order2.setUserId(testUserId);
        order2.setShippingAddress("Başka Adres");
        order2.setCity("İstanbul");
        order2.setZipCode("34000");
        order2.setPhoneNumber("5551111111");
        order2.setOrderItems(new ArrayList<>());
        orderRepository.save(order2); // testUserId
        
        Order order3 = new Order();
        order3.setUserId(UUID.randomUUID());
        order3.setShippingAddress("Farklı Kullanıcı");
        order3.setCity("Ankara");
        order3.setZipCode("06000");
        order3.setPhoneNumber("5552222222");
        order3.setOrderItems(new ArrayList<>());
        orderRepository.save(order3); // Farklı kullanıcı

        // When: testUserId'nin siparişleri aranıyor
        List<Order> userOrders = orderRepository.findByUserId(testUserId);

        // Then: Sadece testUserId'nin siparişleri bulundu
        assertEquals(2, userOrders.size());
        assertTrue(userOrders.stream().allMatch(o -> o.getUserId().equals(testUserId)));
    }

    @Test
    void testFindByStatus() {
        // Given: Farklı durumlarda siparişler kaydediliyor
        Order order1 = orderRepository.save(testOrder); // PENDING
        order1.setStatus(OrderStatus.CONFIRMED);
        orderRepository.save(order1);
        
        Order order2 = new Order();
        order2.setUserId(testUserId);
        order2.setShippingAddress("Test Adresi, Kadıköy");
        order2.setCity("İstanbul");
        order2.setZipCode("34000");
        order2.setPhoneNumber("5551234567");
        order2.setOrderItems(new ArrayList<>());
        orderRepository.save(order2); // PENDING

        // When: CONFIRMED durumundaki siparişler aranıyor
        List<Order> confirmedOrders = orderRepository.findByStatus(OrderStatus.CONFIRMED);

        // Then: Sadece CONFIRMED siparişler bulundu
        assertEquals(1, confirmedOrders.size());
        assertEquals(OrderStatus.CONFIRMED, confirmedOrders.get(0).getStatus());
    }

    @Test
    void testFindByUserIdAndStatus() {
        // Given: Farklı durumlarda siparişler kaydediliyor
        Order order1 = orderRepository.save(testOrder); // PENDING
        order1.setStatus(OrderStatus.DELIVERED);
        orderRepository.save(order1);
        
        Order order2 = new Order();
        order2.setUserId(testUserId);
        order2.setShippingAddress("Test Adresi, Kadıköy");
        order2.setCity("İstanbul");
        order2.setZipCode("34000");
        order2.setPhoneNumber("5551234567");
        order2.setOrderItems(new ArrayList<>());
        orderRepository.save(order2); // PENDING

        // When: testUserId'nin DELIVERED durumundaki siparişleri aranıyor
        List<Order> deliveredOrders = orderRepository.findByUserIdAndStatus(testUserId, OrderStatus.DELIVERED);

        // Then: Sadece testUserId'nin DELIVERED siparişleri bulundu
        assertEquals(1, deliveredOrders.size());
        assertEquals(OrderStatus.DELIVERED, deliveredOrders.get(0).getStatus());
        assertEquals(testUserId, deliveredOrders.get(0).getUserId());
    }

    @Test
    void testFindById() {
        // Given: Bir sipariş kaydediliyor
        Order savedOrder = orderRepository.save(testOrder);
        UUID orderId = savedOrder.getId();

        // When: ID ile sipariş aranıyor
        var foundOrder = orderRepository.findById(orderId);

        // Then: Sipariş bulundu
        assertTrue(foundOrder.isPresent());
        assertEquals(orderId, foundOrder.get().getId());
        assertEquals(testUserId, foundOrder.get().getUserId());
    }

    @Test
    void testDeleteOrder() {
        // Given: Bir sipariş kaydediliyor
        Order savedOrder = orderRepository.save(testOrder);
        UUID orderId = savedOrder.getId();

        // When: Sipariş siliniyor
        orderRepository.deleteById(orderId);

        // Then: Sipariş artık bulunamaz
        assertFalse(orderRepository.existsById(orderId));
    }

    @Test
    void testOrderCreatedAtAndUpdatedAt() {
        // Given: testOrder hazır
        // When: Sipariş kaydediliyor
        Order savedOrder = orderRepository.save(testOrder);

        // Then: createdAt ve updatedAt otomatik oluşturuldu
        assertNotNull(savedOrder.getCreatedAt());
        assertNotNull(savedOrder.getUpdatedAt());
        assertEquals(savedOrder.getCreatedAt(), savedOrder.getUpdatedAt());
    }

    @Test
    void testOrderStatusInitialValue() {
        // Given: testOrder hazır (status null)
        // When: Sipariş kaydediliyor
        Order savedOrder = orderRepository.save(testOrder);

        // Then: Status otomatik olarak PENDING olmalı
        assertEquals(OrderStatus.PENDING, savedOrder.getStatus());
    }

    @Test
    void testOrderItemCascadeDelete() {
        // Given: Bir sipariş ve OrderItem'ları kaydediliyor
        Order savedOrder = orderRepository.save(testOrder);
        UUID orderId = savedOrder.getId();
        assertEquals(1, savedOrder.getOrderItems().size());

        // When: Sipariş siliniyor
        orderRepository.deleteById(orderId);

        // Then: OrderItem'lar da silinmeli (CascadeType.ALL)
        // OrderItem'lar Order'a bağlı olduğu için Order silinince onlar da silinir
        assertFalse(orderRepository.existsById(orderId));
    }

    @Test
    void testOrderTotalAmountAutoCalculation() {
        // Given: Farklı fiyatlarda OrderItem'lar
        Order order = new Order();
        order.setUserId(testUserId);
        order.setShippingAddress("Test Adresi, Kadıköy");
        order.setCity("İstanbul");
        order.setZipCode("34000");
        order.setPhoneNumber("5551234567");
        
        OrderItem item1 = new OrderItem();
        item1.setProductId(testProductId);
        item1.setQuantity(2);
        item1.setPrice(new BigDecimal("100.00"));
        order.addOrderItem(item1);
        
        OrderItem item2 = new OrderItem();
        item2.setProductId(UUID.randomUUID());
        item2.setQuantity(3);
        item2.setPrice(new BigDecimal("50.00"));
        order.addOrderItem(item2);

        // When: Sipariş kaydediliyor
        Order savedOrder = orderRepository.save(order);

        // Then: Toplam tutar otomatik hesaplanmalı (@PrePersist)
        // (2 * 100) + (3 * 50) = 200 + 150 = 350
        assertNotNull(savedOrder.getTotalAmount());
        assertEquals(new BigDecimal("350.00"), savedOrder.getTotalAmount());
    }

    @Test
    void testFindAll() {
        // Given: Birkaç sipariş kaydediliyor
        orderRepository.save(testOrder);
        
        Order order2 = new Order();
        order2.setUserId(UUID.randomUUID());
        order2.setShippingAddress("Başka Adres");
        order2.setCity("Ankara");
        order2.setZipCode("06000");
        order2.setPhoneNumber("5559876543");
        order2.setOrderItems(new ArrayList<>());
        orderRepository.save(order2);

        // When: Tüm siparişler getiriliyor
        List<Order> allOrders = orderRepository.findAll();

        // Then: 2 sipariş olmalı
        assertEquals(2, allOrders.size());
    }
}

