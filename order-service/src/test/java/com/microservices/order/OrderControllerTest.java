package com.microservices.order;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.order.Controller.OrderController;
import com.microservices.order.Exception.ResourceNotFoundException;
import com.microservices.order.Model.Order;
import com.microservices.order.Model.OrderItem;
import com.microservices.order.Model.OrderStatus;
import com.microservices.order.Service.OrderService;

/**
 * OrderController için Integration Test
 * @WebMvcTest: Sadece web katmanını test eder, Controller'lar için
 * MockMvc: HTTP isteklerini simüle eder
 */
@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;  // HTTP isteklerini simüle eder

    @MockBean
    private OrderService orderService;  // OrderService mock'lanıyor

    @Autowired
    private ObjectMapper objectMapper;  // JSON dönüşümleri için

    private Order testOrder;
    private UUID testOrderId;
    private UUID testUserId;

    @BeforeEach
    void setUp() {
        testOrderId = UUID.randomUUID();
        testUserId = UUID.randomUUID();
        
        testOrder = new Order();
        testOrder.setId(testOrderId);
        testOrder.setUserId(testUserId);
        testOrder.setShippingAddress("Test Adresi, Levent");
        testOrder.setCity("İstanbul");
        testOrder.setZipCode("34394");
        testOrder.setPhoneNumber("5551234567");
        testOrder.setStatus(OrderStatus.PENDING);
        testOrder.setTotalAmount(new BigDecimal("115000.00"));
        
        OrderItem item = new OrderItem();
        item.setProductId(UUID.randomUUID());
        item.setProductName("MacBook Pro");
        item.setQuantity(1);
        item.setPrice(new BigDecimal("45000.00"));
        testOrder.setOrderItems(List.of(item));
    }

    @Test
    void testCreateOrder() throws Exception {
        // Given: Mock service davranışı
        when(orderService.createOrder(any(Order.class))).thenReturn(testOrder);

        // When & Then: POST isteği gönderiliyor
        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testOrder)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.userId").value(testUserId.toString()))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.totalAmount").value(115000.00));

        // Verify: Service metodu çağrıldı
        verify(orderService, times(1)).createOrder(any(Order.class));
    }

    @Test
    void testGetOrderById() throws Exception {
        // Given: Mock service davranışı
        when(orderService.getOrderById(testOrderId)).thenReturn(testOrder);

        // When & Then: GET isteği gönderiliyor
        mockMvc.perform(get("/orders/{id}", testOrderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testOrderId.toString()))
                .andExpect(jsonPath("$.userId").value(testUserId.toString()))
                .andExpect(jsonPath("$.status").value("PENDING"));

        verify(orderService, times(1)).getOrderById(testOrderId);
    }

    @Test
    void testGetOrderByIdNotFound() throws Exception {
        // Given: Sipariş bulunamıyor
        UUID nonExistentId = UUID.randomUUID();
        when(orderService.getOrderById(nonExistentId))
                .thenThrow(new ResourceNotFoundException("Order", "id", nonExistentId));

        // When & Then: 404 dönmeli
        mockMvc.perform(get("/orders/{id}", nonExistentId))
                .andExpect(status().isNotFound());

        verify(orderService, times(1)).getOrderById(nonExistentId);
    }

    @Test
    void testGetOrdersByUserId() throws Exception {
        // Given: Mock service davranışı
        when(orderService.getOrdersByUserId(testUserId)).thenReturn(List.of(testOrder));

        // When & Then: GET isteği gönderiliyor
        mockMvc.perform(get("/orders/user/{userId}", testUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].userId").value(testUserId.toString()));

        verify(orderService, times(1)).getOrdersByUserId(testUserId);
    }

    @Test
    void testGetOrdersByStatus() throws Exception {
        // Given: Mock service davranışı
        when(orderService.getOrdersByStatus(OrderStatus.PENDING)).thenReturn(List.of(testOrder));

        // When & Then: GET isteği gönderiliyor
        mockMvc.perform(get("/orders/status")
                .param("status", "PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(orderService, times(1)).getOrdersByStatus(OrderStatus.PENDING);
    }

    @Test
    void testGetOrdersByUserIdAndStatus() throws Exception {
        // Given: Mock service davranışı
        when(orderService.getOrdersByUserIdAndStatus(testUserId, OrderStatus.DELIVERED))
                .thenReturn(List.of(testOrder));

        // When & Then: GET isteği gönderiliyor
        mockMvc.perform(get("/orders/user/{userId}/status", testUserId)
                .param("status", "DELIVERED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(orderService, times(1)).getOrdersByUserIdAndStatus(testUserId, OrderStatus.DELIVERED);
    }

    @Test
    void testUpdateOrder() throws Exception {
        // Given: Mock service davranışı
        Order updatedOrder = new Order();
        updatedOrder.setId(testOrderId);
        updatedOrder.setShippingAddress("Yeni Adres, Beşiktaş");
        updatedOrder.setCity("Ankara");
        updatedOrder.setStatus(OrderStatus.PENDING);
        
        when(orderService.updateOrder(eq(testOrderId), any(Order.class))).thenReturn(updatedOrder);

        // When & Then: PUT isteği gönderiliyor
        mockMvc.perform(put("/orders/{id}", testOrderId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedOrder)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shippingAddress").value("Yeni Adres, Beşiktaş"))
                .andExpect(jsonPath("$.city").value("Ankara"));

        verify(orderService, times(1)).updateOrder(eq(testOrderId), any(Order.class));
    }

    @Test
    void testUpdateOrderStatus() throws Exception {
        // Given: Mock service davranışı
        Order updatedOrder = new Order();
        updatedOrder.setId(testOrderId);
        updatedOrder.setStatus(OrderStatus.CONFIRMED);
        
        when(orderService.updateOrderStatus(testOrderId, OrderStatus.CONFIRMED))
                .thenReturn(updatedOrder);

        // When & Then: PATCH isteği gönderiliyor
        mockMvc.perform(patch("/orders/{id}/status", testOrderId)
                .param("status", "CONFIRMED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));

        verify(orderService, times(1)).updateOrderStatus(testOrderId, OrderStatus.CONFIRMED);
    }

    @Test
    void testCancelOrder() throws Exception {
        // Given: Mock service davranışı
        Order cancelledOrder = new Order();
        cancelledOrder.setId(testOrderId);
        cancelledOrder.setStatus(OrderStatus.CANCELLED);
        
        when(orderService.cancelOrder(testOrderId)).thenReturn(cancelledOrder);

        // When & Then: PATCH isteği gönderiliyor
        mockMvc.perform(patch("/orders/{id}/cancel", testOrderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));

        verify(orderService, times(1)).cancelOrder(testOrderId);
    }

    @Test
    void testDeleteOrder() throws Exception {
        // Given: Mock service davranışı
        doNothing().when(orderService).deleteOrder(testOrderId);

        // When & Then: DELETE isteği gönderiliyor
        mockMvc.perform(delete("/orders/{id}", testOrderId))
                .andExpect(status().isNoContent());

        verify(orderService, times(1)).deleteOrder(testOrderId);
    }

    @Test
    void testDeleteOrderNotFound() throws Exception {
        // Given: Sipariş bulunamıyor
        UUID nonExistentId = UUID.randomUUID();
        doThrow(new ResourceNotFoundException("Order", "id", nonExistentId))
                .when(orderService).deleteOrder(nonExistentId);

        // When & Then: 404 dönmeli
        mockMvc.perform(delete("/orders/{id}", nonExistentId))
                .andExpect(status().isNotFound());

        verify(orderService, times(1)).deleteOrder(nonExistentId);
    }

    @Test
    void testGetAllOrders() throws Exception {
        // Given: Mock service davranışı
        when(orderService.getAllOrders()).thenReturn(List.of(testOrder));

        // When & Then: GET isteği gönderiliyor
        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(orderService, times(1)).getAllOrders();
    }
}

