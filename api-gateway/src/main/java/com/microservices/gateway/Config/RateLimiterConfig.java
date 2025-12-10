package com.microservices.gateway.Config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import reactor.core.publisher.Mono;

/**
 * Enhanced Rate Limiter Configuration
 * 
 * Multiple rate limiting strategies:
 * 1. IP-based rate limiting (default)
 * 2. User-based rate limiting (authenticated users)
 * 3. Path-based rate limiting (different limits for different endpoints)
 */
@Configuration
public class RateLimiterConfig {

    /**
     * IP Key Resolver (Default)
     * Rate limiting'i IP adresine göre yapar
     * 
     * Kullanım:
     * - X-Forwarded-For header'ını kontrol eder (proxy/load balancer desteği)
     * - Fallback olarak RemoteAddress kullanılır
     */
    @Bean
    @Primary
    public KeyResolver ipKeyResolver() {
        return exchange -> {
            // X-Forwarded-For header'ını kontrol et (proxy/load balancer arkasındaysa)
            String forwardedFor = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
            if (forwardedFor != null && !forwardedFor.isEmpty()) {
                // İlk IP adresini al (virgülle ayrılmış olabilir)
                return Mono.just(forwardedFor.split(",")[0].trim());
            }
            
            // Fallback: Client IP adresini al
            String clientIp = exchange.getRequest().getRemoteAddress() != null
                ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
                : "unknown";
            
            return Mono.just(clientIp);
        };
    }
    
    /**
     * User Key Resolver
     * Rate limiting'i authenticated user'a göre yapar
     * 
     * Kullanım:
     * - Authorization header'ından user ID çıkarılır
     * - Eğer user yoksa IP'ye fallback yapar
     * - key-resolver: "#{@userKeyResolver}" şeklinde kullanılır
     */
    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> {
            // Authorization header'ından user ID çıkar
            String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
            
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                // JWT token'dan user ID çıkarılabilir (şimdilik token'ı kullanıyoruz)
                // Gerçek implementasyonda JWT parse edilip user ID alınmalı
                String token = authHeader.substring(7);
                return Mono.just("user:" + token.hashCode()); // Simplified user identification
            }
            
            // Fallback: IP-based rate limiting
            return ipKeyResolver().resolve(exchange);
        };
    }
    
    /**
     * Path + IP Key Resolver
     * Rate limiting'i path ve IP kombinasyonuna göre yapar
     * 
     * Kullanım:
     * - Farklı endpoint'ler için farklı limitler uygulanır
     * - Örnek: /api/media/** için düşük, /api/shops/** için yüksek limit
     * - key-resolver: "#{@pathIpKeyResolver}" şeklinde kullanılır
     */
    @Bean
    public KeyResolver pathIpKeyResolver() {
        return exchange -> {
            String path = exchange.getRequest().getPath().value();
            String ip = exchange.getRequest().getRemoteAddress() != null
                ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
                : "unknown";
            
            // Path + IP kombinasyonu
            return Mono.just(path + ":" + ip);
        };
    }
    
    /**
     * Combined Key Resolver (Most Secure)
     * User (if authenticated) + IP kombinasyonu
     * 
     * Kullanım:
     * - Authenticated user'lar için user ID + IP
     * - Unauthenticated için sadece IP
     * - DDoS ve abuse koruması için en güvenli yöntem
     */
    @Bean
    public KeyResolver combinedKeyResolver() {
        return exchange -> {
            String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
            String ip = exchange.getRequest().getRemoteAddress() != null
                ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
                : "unknown";
            
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                return Mono.just("user:" + token.hashCode() + ":ip:" + ip);
            }
            
            return Mono.just("ip:" + ip);
        };
    }
}

