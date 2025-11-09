package com.microservices.order.Client;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Component;

/**
 * Product Service Client Fallback
 * 
 * Circuit Breaker açıldığında veya Product Service hata verdiğinde
 * bu fallback method'ları çağrılır.
 * 
 * Fallback Nedir?
 * - Bir servis çalışmadığında alternatif davranış sağlar
 * - Kullanıcıya daha iyi bir deneyim sunar (hata mesajı yerine)
 * - Sistemin daha dayanıklı olmasını sağlar
 * 
 * Örnek Senaryo:
 * 1. Product Service down (çalışmıyor)
 * 2. Order Service, Product Service'e istek gönderir
 * 3. Circuit Breaker açılır (çok fazla hata)
 * 4. Fallback method çağrılır
 * 5. Fallback, default değerler döner veya cache'den okur
 */
@Component
public class ProductServiceClientFallback implements ProductServiceClient {

    /**
     * Product Service hata verdiğinde çağrılır
     * 
     * @param productId Ürün ID'si
     * @return Default ProductResponse veya null
     * 
     * Not: Gerçek uygulamada cache'den okuyabilir veya
     * default değerler dönebilir
     */
    @Override
    public ProductResponse getProductById(UUID productId) {
        // Fallback: Default değerler döndür
        // Gerçek uygulamada cache'den okuyabilir veya
        // önceki başarılı response'ları saklayabilir
        ProductResponse fallbackResponse = new ProductResponse();
        fallbackResponse.setId(productId);
        fallbackResponse.setName("Product Unavailable");
        fallbackResponse.setDescription("Product service is currently unavailable");
        fallbackResponse.setPrice(BigDecimal.ZERO);
        
        // Log: Production'da logger kullanılmalı
        System.err.println("Product Service Fallback: Product " + productId + " unavailable");
        
        return fallbackResponse;
    }
}

