package com.microservices.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Inventory Service Application
 * Stok yönetimi için microservice
 */
@SpringBootApplication
@EnableDiscoveryClient  // Eureka'ya kayıt olmak için
public class InventoryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InventoryServiceApplication.class, args);
    }
}

