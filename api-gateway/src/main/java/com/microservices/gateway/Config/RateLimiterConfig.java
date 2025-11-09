package com.microservices.gateway.Config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import reactor.core.publisher.Mono;

/**
 * Rate Limiter Configuration
 * 
 * IP adresine göre rate limiting yapar
 * Her IP adresi için ayrı rate limit uygulanır
 */
@Configuration
public class RateLimiterConfig {

    /**
     * IP Key Resolver
     * Rate limiting'i IP adresine göre yapar
     * 
     * Örnek:
     * - Aynı IP'den saniyede 10 istek yapılabilir
     * - Farklı IP'ler birbirini etkilemez
     */
    @Bean
    public KeyResolver ipKeyResolver() {
        return exchange -> {
            // Client IP adresini al
            String clientIp = exchange.getRequest().getRemoteAddress() != null
                ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
                : "unknown";
            
            // X-Forwarded-For header'ını kontrol et (proxy/load balancer arkasındaysa)
            String forwardedFor = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
            if (forwardedFor != null && !forwardedFor.isEmpty()) {
                // İlk IP adresini al (virgülle ayrılmış olabilir)
                clientIp = forwardedFor.split(",")[0].trim();
            }
            
            return Mono.just(clientIp);
        };
    }
}

