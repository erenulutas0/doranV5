package com.microservices.hobbygroup.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Tüm endpoint'ler için CORS etkinleştir
                .allowedOrigins("http://localhost:8086", "http://127.0.0.1:8086") // Flutter web uygulamasının çalıştığı adres
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH") // İzin verilen HTTP metodları
                .allowedHeaders("*") // Tüm header'lara izin ver
                .allowCredentials(true) // Kimlik bilgilerinin (cookie, authorization header) gönderilmesine izin ver
                .maxAge(3600); // Pre-flight isteğinin cache'lenme süresi (saniye)
    }
}

