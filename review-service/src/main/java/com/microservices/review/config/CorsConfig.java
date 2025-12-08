package com.microservices.review.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * CORS Configuration
 * Flutter web uygulamasından gelen isteklere izin vermek için
 */
@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // Flutter web uygulamasının origin'i
        config.addAllowedOrigin("http://localhost:8081");
        config.addAllowedOrigin("http://localhost:8082");
        config.addAllowedOrigin("http://localhost:8086");
        config.addAllowedOrigin("http://127.0.0.1:8081");
        config.addAllowedOrigin("http://127.0.0.1:8082");
        config.addAllowedOrigin("http://127.0.0.1:8086");

        // Tüm HTTP metodlarına izin ver
        config.addAllowedMethod("*");

        // Tüm header'lara izin ver
        config.addAllowedHeader("*");

        // Exposed headers
        config.addExposedHeader("*");

        // Credentials (cookies, authorization headers) göndermeye izin ver
        config.setAllowCredentials(true);

        // Preflight request'lerin cache'lenme süresi (1 saat)
        config.setMaxAge(3600L);

        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}

