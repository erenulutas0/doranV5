package com.microservices.gateway.Filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

/**
 * Logging Global Filter
 * 
 * Tüm gateway isteklerini loglar
 * Request ve Response bilgilerini kaydeder
 */
@Component
public class LoggingGlobalFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(LoggingGlobalFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        
        // Request bilgilerini logla
        String path = request.getURI().getPath();
        String method = request.getMethod().name();
        String clientIp = getClientIp(request);
        
        logger.info("=== API Gateway Request ===");
        logger.info("Method: {}", method);
        logger.info("Path: {}", path);
        logger.info("Client IP: {}", clientIp);
        logger.info("Headers: {}", request.getHeaders());
        
        long startTime = System.currentTimeMillis();
        
        // Response'u loglamak için bir wrapper ekle
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            ServerHttpResponse response = exchange.getResponse();
            long duration = System.currentTimeMillis() - startTime;
            
            logger.info("=== API Gateway Response ===");
            logger.info("Status: {}", response.getStatusCode());
            logger.info("Duration: {} ms", duration);
            logger.info("============================");
        }));
    }

    /**
     * Client IP adresini al
     * X-Forwarded-For header'ını kontrol eder (proxy/load balancer arkasındaysa)
     */
    private String getClientIp(ServerHttpRequest request) {
        String forwardedFor = request.getHeaders().getFirst("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isEmpty()) {
            return forwardedFor.split(",")[0].trim();
        }
        
        if (request.getRemoteAddress() != null) {
            return request.getRemoteAddress().getAddress().getHostAddress();
        }
        
        return "unknown";
    }

    @Override
    public int getOrder() {
        // Filter sırası (düşük sayı = önce çalışır)
        return -1;  // Diğer filter'lardan önce çalışsın
    }
}

