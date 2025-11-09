package com.microservices.order.Repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.microservices.order.Model.Order;
import com.microservices.order.Model.OrderStatus;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    
    /**
     * User ID'ye göre siparişleri getir
     * Kullanıcının tüm siparişlerini listeler
     */
    List<Order> findByUserId(UUID userId);
    
    /**
     * Sipariş durumuna göre filtrele
     * Örnek: PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED
     */
    List<Order> findByStatus(OrderStatus status);
    
    /**
     * Kullanıcının belirli durumdaki siparişlerini getir
     * Örnek: Kullanıcının teslim edilmiş siparişleri
     */
    List<Order> findByUserIdAndStatus(UUID userId, OrderStatus status);
}

