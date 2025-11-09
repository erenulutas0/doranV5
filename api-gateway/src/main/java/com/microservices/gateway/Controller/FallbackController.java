package com.microservices.gateway.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Fallback Controller
 * 
 * Circuit Breaker açıldığında veya servisler kullanılamadığında
 * bu endpoint'e yönlendirilir
 */
@RestController
@RequestMapping("/fallback")
public class FallbackController {

    /**
     * Genel fallback endpoint
     * Circuit breaker açıldığında bu endpoint'e yönlendirilir
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> fallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("error", "SERVICE_UNAVAILABLE");
        response.put("message", "Service is currently unavailable. Please try again later.");
        response.put("path", "/fallback");
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    /**
     * User Service fallback
     */
    @GetMapping("/user")
    public ResponseEntity<Map<String, Object>> userServiceFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("error", "USER_SERVICE_UNAVAILABLE");
        response.put("message", "User Service is currently unavailable. Please try again later.");
        response.put("path", "/fallback/user");
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    /**
     * Product Service fallback
     */
    @GetMapping("/product")
    public ResponseEntity<Map<String, Object>> productServiceFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("error", "PRODUCT_SERVICE_UNAVAILABLE");
        response.put("message", "Product Service is currently unavailable. Please try again later.");
        response.put("path", "/fallback/product");
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    /**
     * Order Service fallback
     */
    @GetMapping("/order")
    public ResponseEntity<Map<String, Object>> orderServiceFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("error", "ORDER_SERVICE_UNAVAILABLE");
        response.put("message", "Order Service is currently unavailable. Please try again later.");
        response.put("path", "/fallback/order");
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    /**
     * Inventory Service fallback
     */
    @GetMapping("/inventory")
    public ResponseEntity<Map<String, Object>> inventoryServiceFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("error", "INVENTORY_SERVICE_UNAVAILABLE");
        response.put("message", "Inventory Service is currently unavailable. Please try again later.");
        response.put("path", "/fallback/inventory");
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    /**
     * Notification Service fallback
     */
    @GetMapping("/notification")
    public ResponseEntity<Map<String, Object>> notificationServiceFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("error", "NOTIFICATION_SERVICE_UNAVAILABLE");
        response.put("message", "Notification Service is currently unavailable. Please try again later.");
        response.put("path", "/fallback/notification");
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }
}

