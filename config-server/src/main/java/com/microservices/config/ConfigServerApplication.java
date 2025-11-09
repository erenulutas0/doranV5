package com.microservices.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * Config Server Application
 * 
 * Spring Cloud Config Server - Merkezi yapılandırma yönetimi
 * 
 * Tüm microservice'lerin yapılandırma dosyalarını tek bir yerden yönetir.
 * Git repository veya local file system'den yapılandırmaları okur.
 * 
 * Not: Spring Boot 3'te @EnableEurekaClient annotation'ı kaldırılmıştır.
 * Eureka Client otomatik olarak aktif olur (spring-cloud-starter-netflix-eureka-client dependency'si ile).
 */
@SpringBootApplication
@EnableConfigServer  // Config Server'ı aktif et
public class ConfigServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }
}

