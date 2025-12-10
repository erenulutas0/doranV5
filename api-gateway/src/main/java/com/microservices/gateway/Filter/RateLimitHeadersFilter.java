package com.microservices.gateway.Filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * Rate Limit Headers Filter
 * 
 * Rate limiting response headers ekler:
 * - X-RateLimit-Remaining: Kalan istek sayısı
 * - X-RateLimit-Limit: Toplam limit
 * - X-RateLimit-Reset: Rate limit reset zamanı
 * - Retry-After: Rate limit aşıldığında bekleme süresi
 */
@Component
@Slf4j
public class RateLimitHeadersFilter implements GlobalFilter, Ordered {

    private static final String RATE_LIMIT_REMAINING_HEADER = "X-RateLimit-Remaining";
    private static final String RATE_LIMIT_LIMIT_HEADER = "X-RateLimit-Limit";
    private static final String RATE_LIMIT_RESET_HEADER = "X-RateLimit-Reset";
    private static final String RETRY_AFTER_HEADER = "Retry-After";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // Response committed olmadan önce header'ları ekle
        exchange.getResponse().beforeCommit(() -> {
            // Rate limit exceeded durumunda custom headers ekle
            if (exchange.getResponse().getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                HttpHeaders headers = exchange.getResponse().getHeaders();
                
                // Retry-After header'ı ekle (60 saniye bekle)
                headers.add(RETRY_AFTER_HEADER, "60");
                
                // Rate limit bilgileri ekle
                headers.add(RATE_LIMIT_REMAINING_HEADER, "0");
                headers.add(RATE_LIMIT_RESET_HEADER, String.valueOf(System.currentTimeMillis() / 1000 + 60));
                
                log.warn("Rate limit exceeded for request: {} from IP: {}", 
                    exchange.getRequest().getPath(), 
                    exchange.getRequest().getRemoteAddress());
            }
            return Mono.empty();
        });
        
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        // Rate limit filter'ından sonra çalışması için yüksek order
        return Ordered.LOWEST_PRECEDENCE;
    }
}

