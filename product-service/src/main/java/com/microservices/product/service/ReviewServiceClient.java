package com.microservices.product.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Review Service Client
 * Review-service'den rating ve reviewCount bilgilerini çeker
 */
@Service
@Slf4j
public class ReviewServiceClient {
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    @Value("${review.service.url:http://review-service:8087/reviews}")
    private String reviewServiceBaseUrl;
    
    public ReviewServiceClient(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }
    
    /**
     * Product için rating summary'yi çek
     */
    public RatingSummary getRatingSummary(UUID productId) {
        try {
            String url = reviewServiceBaseUrl + "/product/" + productId + "/summary";
            String response = restTemplate.getForObject(url, String.class);
            
            if (response != null) {
                JsonNode jsonNode = objectMapper.readTree(response);
                BigDecimal averageRating = jsonNode.has("averageRating") 
                    ? jsonNode.get("averageRating").decimalValue() 
                    : BigDecimal.ZERO;
                Integer totalReviews = jsonNode.has("totalReviews") 
                    ? jsonNode.get("totalReviews").asInt() 
                    : 0;
                
                return new RatingSummary(averageRating, totalReviews);
            }
        } catch (Exception e) {
            log.warn("Failed to fetch rating summary for product {}: {}", productId, e.getMessage());
        }
        return new RatingSummary(BigDecimal.ZERO, 0);
    }
    
    /**
     * Rating Summary DTO
     */
    public static class RatingSummary {
        private final BigDecimal averageRating;
        private final Integer totalReviews;
        
        public RatingSummary(BigDecimal averageRating, Integer totalReviews) {
            this.averageRating = averageRating;
            this.totalReviews = totalReviews;
        }
        
        public BigDecimal getAverageRating() {
            return averageRating;
        }
        
        public Integer getTotalReviews() {
            return totalReviews;
        }
    }
}

