package com.microservices.order.Client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Product Service Client
 * Product Service ile iletişim kurmak için Feign Client
 * 
 * Feign Client Nedir?
 * - Spring Cloud'un bir parçası
 * - REST API çağrılarını basitleştirir
 * - Interface tanımlayıp Spring otomatik implement eder
 * - Eureka ile entegre çalışır (servis adıyla çağırır)
 * 
 * @FeignClient(name = "product-service"):
 * - Eureka'da kayıtlı "product-service" adlı servisi çağırır
 * - Eureka otomatik olarak IP ve port'u bulur
 * - Load balancing otomatik yapılır (birden fazla instance varsa)
 */
@FeignClient(
    name = "product-service", 
    url = "${product.service.url:}",
    fallback = ProductServiceClientFallback.class  // Circuit Breaker açıldığında çağrılacak fallback
)
public interface ProductServiceClient {
    
    /**
     * Product ID'ye göre ürün bilgisi getir
     * 
     * @param productId Ürün ID'si
     * @return Product bilgisi (id, name, price, description, vb.)
     * 
     * Nasıl Çalışır?
     * 1. Order Service bu metodu çağırır
     * 2. Feign Client, Eureka'dan "product-service" adresini bulur
     * 3. GET /products/{productId} endpoint'ine istek gönderir
     * 4. Product Service'den dönen response'u Product objesine map eder
     * 
     * Örnek Kullanım:
     * Product product = productServiceClient.getProductById(productId);
     * orderItem.setProductName(product.getName());
     * orderItem.setPrice(product.getPrice());
     */
    @GetMapping("/products/{productId}")
    ProductResponse getProductById(@PathVariable("productId") UUID productId);
    
    /**
     * Product Response DTO
     * Product Service'den dönen response'u map etmek için
     * 
     * ÖNEMLİ: Product Service'deki Product entity'si ile aynı field'lara sahip olmalı
     * Ama burada sadece ihtiyacımız olan field'ları tutuyoruz
     */
    class ProductResponse {
        private UUID id;
        private String name;
        private String description;
        private java.math.BigDecimal price;
        
        // Getters and Setters
        public UUID getId() {
            return id;
        }
        
        public void setId(UUID id) {
            this.id = id;
        }
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public String getDescription() {
            return description;
        }
        
        public void setDescription(String description) {
            this.description = description;
        }
        
        public java.math.BigDecimal getPrice() {
            return price;
        }
        
        public void setPrice(java.math.BigDecimal price) {
            this.price = price;
        }
    }
}

