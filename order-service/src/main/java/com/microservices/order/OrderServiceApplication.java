package com.microservices.order;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Order Service Application
 * Sipariş yönetimi için microservice
 * 
 * Özellikler:
 * - Sipariş oluşturma
 * - Sipariş durumu takibi
 * - Sipariş geçmişi
 * - Inventory Service ile stok kontrolü (Feign Client ile)
 * - Product Service ile ürün bilgisi çekme (Feign Client ile)
 * - User Service ile kullanıcı doğrulama (Feign Client ile)
 * - RabbitMQ ile asenkron bildirim gönderme
 */
@SpringBootApplication
@EnableDiscoveryClient  // Eureka'ya kayıt olmak için
@EnableFeignClients     // Feign Client'ları aktif et
@EnableRabbit           // RabbitMQ için aktif et
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}

