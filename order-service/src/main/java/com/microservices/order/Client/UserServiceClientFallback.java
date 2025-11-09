package com.microservices.order.Client;

import java.util.UUID;

import org.springframework.stereotype.Component;

/**
 * User Service Client Fallback
 * 
 * Circuit Breaker açıldığında veya User Service hata verdiğinde
 * bu fallback method'ları çağrılır.
 */
@Component
public class UserServiceClientFallback implements UserServiceClient {

    @Override
    public UserResponse getUserById(UUID userId) {
        // Fallback: Default değerler döndür
        // Not: User bulunamadığında Order Service'de ResourceNotFoundException fırlatılır
        // Bu yüzden null döndürüyoruz ki Order Service hata fırlatsın
        System.err.println("User Service Fallback: User " + userId + " unavailable");
        
        return null;  // Order Service'de null kontrolü var, ResourceNotFoundException fırlatılır
    }
}

