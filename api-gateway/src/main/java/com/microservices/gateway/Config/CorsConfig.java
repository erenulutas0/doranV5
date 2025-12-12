package com.microservices.gateway.Config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Global CORS Configuration for API Gateway
 * 
 * This configuration allows cross-origin requests from Flutter app
 * and other frontend applications.
 */
@Configuration
@EnableConfigurationProperties(CorsProps.class)
public class CorsConfig {

    private final CorsProps corsProps;

    public CorsConfig(CorsProps corsProps) {
        this.corsProps = corsProps;
    }

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        
        // Set allowed origins - if credentials are allowed, we cannot use wildcard
        // Default to Flutter web app origin for development
        if (corsProps.getAllowedOrigins() != null && !corsProps.getAllowedOrigins().isEmpty()) {
            corsConfig.setAllowedOrigins(corsProps.getAllowedOrigins());
        } else {
            // Default origins for development (Flutter web app)
            corsConfig.setAllowedOrigins(Arrays.asList(
                "http://localhost:8088",
                "http://127.0.0.1:8088",
                "http://localhost:3000",
                "http://127.0.0.1:3000"
            ));
        }
        
        // Allowed methods
        if (corsProps.getAllowedMethods() != null && !corsProps.getAllowedMethods().isEmpty()) {
            corsConfig.setAllowedMethods(corsProps.getAllowedMethods());
        } else {
            corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "HEAD"));
        }
        
        // Allowed headers
        if (corsProps.getAllowedHeaders() != null && !corsProps.getAllowedHeaders().isEmpty()) {
            corsConfig.setAllowedHeaders(corsProps.getAllowedHeaders());
        } else {
            corsConfig.setAllowedHeaders(Arrays.asList(
                "Content-Type",
                "Accept",
                "Authorization",
                "X-Requested-With",
                "X-API-Version",
                "Origin",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers"
            ));
        }
        
        // Expose custom headers to client
        if (corsProps.getExposedHeaders() != null && !corsProps.getExposedHeaders().isEmpty()) {
            corsConfig.setExposedHeaders(corsProps.getExposedHeaders());
        } else {
            corsConfig.setExposedHeaders(Arrays.asList(
                "X-RateLimit-Remaining",
                "X-RateLimit-Limit", 
                "X-RateLimit-Reset",
                "X-Content-Type-Options",
                "X-Frame-Options"
            ));
        }
        
        // Allow credentials (cookies, authorization headers)
        // Note: When allowCredentials is true, cannot use wildcard origins
        corsConfig.setAllowCredentials(Objects.requireNonNullElse(corsProps.getAllowCredentials(), true));
        
        // Cache preflight response for 1 hour
        corsConfig.setMaxAge(Objects.requireNonNullElse(corsProps.getMaxAge(), 3600L));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);
        
        CorsWebFilter filter = new CorsWebFilter(source);
        return filter;
    }
}


