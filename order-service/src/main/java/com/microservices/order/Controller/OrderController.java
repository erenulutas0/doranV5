package com.microservices.order.Controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.microservices.order.Model.Order;
import com.microservices.order.Model.OrderStatus;
import com.microservices.order.Service.OrderService;

/**
 * Order Controller
 * Sipariş yönetimi için REST API endpoints
 * 
 * Önemli Endpoint'ler:
 * - POST /orders → Yeni sipariş oluştur
 * - GET /orders/{id} → Sipariş detayı
 * - GET /orders/user/{userId} → Kullanıcının siparişleri
 * - PATCH /orders/{id}/status → Sipariş durumu güncelle
 * - PATCH /orders/{id}/cancel → Sipariş iptal et
 */
@RestController
@RequestMapping("/orders")  // Gateway zaten /api/orders/** alıyor
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Tüm siparişleri getir
     * GET /orders
     * Admin paneli için kullanılır
     */
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    /**
     * ID'ye göre sipariş getir
     * GET /orders/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable("id") UUID id) {
        Order order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    /**
     * User ID'ye göre siparişleri getir
     * GET /orders/user/{userId}
     * Kullanıcının tüm siparişlerini listeler
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getOrdersByUserId(@PathVariable("userId") UUID userId) {
        List<Order> orders = orderService.getOrdersByUserId(userId);
        return ResponseEntity.ok(orders);
    }

    /**
     * Sipariş durumuna göre filtrele
     * GET /orders/status?status=PENDING
     */
    @GetMapping("/status")
    public ResponseEntity<List<Order>> getOrdersByStatus(@RequestParam("status") OrderStatus status) {
        List<Order> orders = orderService.getOrdersByStatus(status);
        return ResponseEntity.ok(orders);
    }

    /**
     * Kullanıcının belirli durumdaki siparişlerini getir
     * GET /orders/user/{userId}/status?status=DELIVERED
     */
    @GetMapping("/user/{userId}/status")
    public ResponseEntity<List<Order>> getOrdersByUserIdAndStatus(
            @PathVariable("userId") UUID userId,
            @RequestParam("status") OrderStatus status) {
        List<Order> orders = orderService.getOrdersByUserIdAndStatus(userId, status);
        return ResponseEntity.ok(orders);
    }

    /**
     * Yeni sipariş oluştur
     * POST /orders
     * 
     * Request Body örneği:
     * {
     *   "userId": "user-id",
     *   "addressId": "address-id" (opsiyonel),
     *   "shippingAddress": "Adres",
     *   "city": "İstanbul",
     *   "zipCode": "34000",
     *   "phoneNumber": "5551234567",
     *   "orderItems": [
     *     {
     *       "productId": "product-id",
     *       "productName": "Ürün Adı",
     *       "quantity": 2,
     *       "price": 100.00
     *     }
     *   ]
     * }
     */
    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        Order createdOrder = orderService.createOrder(order);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }

    /**
     * Sipariş güncelle
     * PUT /orders/{id}
     * Sadece PENDING durumundaki siparişler güncellenebilir
     */
    @PutMapping("/{id}")
    public ResponseEntity<Order> updateOrder(
            @PathVariable("id") UUID id,
            @RequestBody Order order) {
        Order updatedOrder = orderService.updateOrder(id, order);
        return ResponseEntity.ok(updatedOrder);
    }

    /**
     * Sipariş durumunu güncelle
     * PATCH /orders/{id}/status?status=CONFIRMED
     * 
     * Örnek durum geçişleri:
     * - PENDING → CONFIRMED
     * - CONFIRMED → PROCESSING
     * - PROCESSING → SHIPPED
     * - SHIPPED → DELIVERED
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<Order> updateOrderStatus(
            @PathVariable("id") UUID id,
            @RequestParam("status") OrderStatus status) {
        Order updatedOrder = orderService.updateOrderStatus(id, status);
        return ResponseEntity.ok(updatedOrder);
    }

    /**
     * Sipariş iptal et
     * PATCH /orders/{id}/cancel
     * Sadece PENDING veya CONFIRMED durumundaki siparişler iptal edilebilir
     */
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Order> cancelOrder(@PathVariable("id") UUID id) {
        Order cancelledOrder = orderService.cancelOrder(id);
        return ResponseEntity.ok(cancelledOrder);
    }

    /**
     * Sipariş sil
     * DELETE /orders/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable("id") UUID id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}

