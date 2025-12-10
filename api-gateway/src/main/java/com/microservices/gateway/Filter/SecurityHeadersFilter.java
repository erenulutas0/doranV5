package com.microservices.gateway.Filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * Security Headers Filter
 * 
 * Tüm HTTP response'lara güvenlik header'ları ekler:
 * - X-Content-Type-Options: MIME type sniffing önleme
 * - X-Frame-Options: Clickjacking saldırılarını önleme
 * - X-XSS-Protection: XSS saldırılarını önleme
 * - Strict-Transport-Security: HTTPS kullanımını zorunlu kılma
 * - Content-Security-Policy: XSS ve injection saldırılarını önleme
 * - Referrer-Policy: Referrer bilgisi kontrolü
 * - Permissions-Policy: Browser feature policy
 */
@Component
@Slf4j
public class SecurityHeadersFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // Response committed olmadan önce header'ları ekle
        exchange.getResponse().beforeCommit(() -> {
            HttpHeaders headers = exchange.getResponse().getHeaders();
            
            // X-Content-Type-Options
            // MIME type sniffing'i engeller, declared content-type kullanılır
            headers.add("X-Content-Type-Options", "nosniff");
            
            // X-Frame-Options
            // Clickjacking saldırılarını önler, iframe içinde kullanılamaz
            headers.add("X-Frame-Options", "DENY");
            
            // X-XSS-Protection
            // Tarayıcının built-in XSS korumasını aktif eder
            headers.add("X-XSS-Protection", "1; mode=block");
            
            // Strict-Transport-Security (HSTS)
            // HTTPS kullanımını zorunlu kılar (1 yıl boyunca)
            // Production'da kullanılmalı, development'ta optional
            headers.add("Strict-Transport-Security", "max-age=31536000; includeSubDomains; preload");
            
            // Content-Security-Policy (CSP)
            // XSS ve data injection saldırılarını önler
            // Not: Frontend ile uyumlu olması için ayarlanmalı
            headers.add("Content-Security-Policy", 
                "default-src 'self'; " +
                "script-src 'self' 'unsafe-inline' 'unsafe-eval' https://cdn.jsdelivr.net https://unpkg.com; " +
                "style-src 'self' 'unsafe-inline' https://fonts.googleapis.com; " +
                "img-src 'self' data: https:; " +
                "font-src 'self' https://fonts.gstatic.com; " +
                "connect-src 'self' https: wss:; " +
                "frame-ancestors 'none';");
            
            // Referrer-Policy
            // Referrer bilgisinin nasıl paylaşılacağını kontrol eder
            headers.add("Referrer-Policy", "strict-origin-when-cross-origin");
            
            // Permissions-Policy (formerly Feature-Policy)
            // Browser feature'larını kontrol eder (camera, microphone, geolocation)
            headers.add("Permissions-Policy", 
                "camera=(), " +
                "microphone=(), " +
                "geolocation=(self), " +
                "payment=(), " +
                "usb=()");
            
            // X-Permitted-Cross-Domain-Policies
            // Cross-domain policy dosyalarını engeller (Adobe Flash, PDF, etc.)
            headers.add("X-Permitted-Cross-Domain-Policies", "none");
            
            // Cache-Control (Sensitive data için)
            // Health check ve actuator endpoint'leri için cache'i engelle
            String path = exchange.getRequest().getPath().value();
            if (path.contains("/actuator") || path.contains("/health")) {
                headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
                headers.add(HttpHeaders.PRAGMA, "no-cache");
                headers.add(HttpHeaders.EXPIRES, "0");
            }
            
            log.debug("Security headers added to response for path: {}", path);
            return Mono.empty();
        });
        
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        // Security headers en önce eklenmeli
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }
}

