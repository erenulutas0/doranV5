package com.microservices.ownproduct.client;

import com.microservices.ownproduct.dto.RatingSummary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Review Service Client
 * Review Service ile iletişim için client
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ReviewServiceClient {
    
    private final RestTemplate restTemplate;
    
    @Value("${review.service.url:http://localhost:8085}")
    private String reviewServiceUrl;
    
    /**
     * Tek bir ürün için rating özeti getir
     */
    public RatingSummary getRatingSummary(UUID productId) {
        try {
            String url = reviewServiceUrl + "/reviews/product/" + productId + "/summary";
            ResponseEntity<RatingSummary> response = restTemplate.getForEntity(url, RatingSummary.class);
            return response.getBody();
        } catch (Exception e) {
            log.error("Error fetching rating summary for product {}: {}", productId, e.getMessage());
            return createEmptyRatingSummary(productId);
        }
    }
    
    /**
     * Birden fazla ürün için rating özetlerini toplu olarak getir
     * N+1 Query problemini çözer
     */
    public Map<UUID, RatingSummary> getBatchRatingSummaries(List<UUID> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return new HashMap<>();
        }
        
        try {
            // Batch API'yi çağır
            String url = UriComponentsBuilder
                .fromHttpUrl(reviewServiceUrl + "/reviews/batch/summary")
                .queryParam("productIds", productIds.stream()
                    .map(UUID::toString)
                    .collect(Collectors.joining(",")))
                .toUriString();
            
            log.debug("Fetching batch rating summaries for {} products", productIds.size());
            
            ResponseEntity<Map<UUID, RatingSummary>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<UUID, RatingSummary>>() {}
            );
            
            Map<UUID, RatingSummary> summaries = response.getBody();
            if (summaries == null) {
                summaries = new HashMap<>();
            }
            
            // Eksik productId'ler için empty summary ekle
            for (UUID productId : productIds) {
                summaries.putIfAbsent(productId, createEmptyRatingSummary(productId));
            }
            
            log.debug("Successfully fetched batch rating summaries for {} products", summaries.size());
            return summaries;
            
        } catch (Exception e) {
            log.error("Error fetching batch rating summaries: {}", e.getMessage());
            
            // Hata durumunda her ürün için empty summary döndür
            Map<UUID, RatingSummary> fallbackMap = new HashMap<>();
            for (UUID productId : productIds) {
                fallbackMap.put(productId, createEmptyRatingSummary(productId));
            }
            return fallbackMap;
        }
    }
    
    /**
     * Boş rating summary oluştur (fallback için)
     */
    private RatingSummary createEmptyRatingSummary(UUID productId) {
        RatingSummary summary = new RatingSummary();
        summary.setProductId(productId);
        summary.setAverageRating(0.0);
        summary.setTotalReviews(0);
        summary.setStar1Count(0);
        summary.setStar2Count(0);
        summary.setStar3Count(0);
        summary.setStar4Count(0);
        summary.setStar5Count(0);
        return summary;
    }
}

