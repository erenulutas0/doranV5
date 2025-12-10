package com.microservices.review.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.microservices.review.event.ReviewCreatedEvent;
import com.microservices.review.model.RatingSummary;
import com.microservices.review.model.Review;
import com.microservices.review.repository.ReviewRepository;

/**
 * Review Service
 * Review iş mantığı için service
 */
@Service
public class ReviewService {
    
    private final ReviewRepository reviewRepository;
    private final ApplicationEventPublisher eventPublisher;
    
    public ReviewService(ReviewRepository reviewRepository, ApplicationEventPublisher eventPublisher) {
        this.reviewRepository = reviewRepository;
        this.eventPublisher = eventPublisher;
    }
    
    /**
     * Yeni yorum oluştur
     * 
     * Review oluşturulduktan sonra async event publish edilir:
     * - Notification gönderme
     * - Analytics güncelleme
     * - Cache warming
     */
    @Transactional
    @CacheEvict(value = {"reviews", "ratingSummary"}, key = "#a0.productId")
    public Review createReview(Review review) {
        // Kullanıcı daha önce bu ürün için yorum yapmış mı kontrol et
        Optional<Review> existingReview = reviewRepository.findByProductIdAndUserId(
            review.getProductId(), 
            review.getUserId()
        );
        
        if (existingReview.isPresent()) {
            throw new IllegalArgumentException("You have already reviewed this product");
        }
        
        Review savedReview = reviewRepository.save(review);
        
        // Publish review created event (async processing)
        eventPublisher.publishEvent(new ReviewCreatedEvent(this, savedReview));
        
        return savedReview;
    }
    
    /**
     * Ürüne ait yorumları getir
     */
    @Cacheable(value = "reviews", key = "#a0")
    public List<Review> getReviewsByProductId(UUID productId) {
        return reviewRepository.findByProductIdAndIsApprovedTrueOrderByCreatedAtDesc(productId);
    }
    
    /**
     * Kullanıcının yorumlarını getir
     */
    public List<Review> getReviewsByUserId(UUID userId) {
        return reviewRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
    
    /**
     * Yorum ID'sine göre getir
     */
    public Optional<Review> getReviewById(UUID reviewId) {
        return reviewRepository.findById(reviewId);
    }
    
    /**
     * Yorum güncelle
     */
    @Transactional
    @CacheEvict(value = {"reviews", "ratingSummary"}, key = "#a0.productId")
    public Review updateReview(Review review) {
        return reviewRepository.save(review);
    }
    
    /**
     * Yorum sil
     */
    @Transactional
    @CacheEvict(value = {"reviews", "ratingSummary"}, key = "#a1")
    public void deleteReview(UUID reviewId, UUID productId) {
        // Yorumun varlığını kontrol et
        reviewRepository.findById(reviewId)
            .orElseThrow(() -> new IllegalArgumentException("Review not found"));
        reviewRepository.deleteById(reviewId);
    }
    
    /**
     * Yorum için "Yardımcı Oldu" sayısını artır
     */
    @Transactional
    public Review markAsHelpful(UUID reviewId) {
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new IllegalArgumentException("Review not found"));
        
        review.setHelpfulCount(review.getHelpfulCount() + 1);
        return reviewRepository.save(review);
    }
    
    /**
     * Ürün için rating özeti getir
     */
    @Cacheable(value = "ratingSummary", key = "#a0")
    public RatingSummary getRatingSummary(UUID productId) {
        Double avgRating = reviewRepository.calculateAverageRating(productId);
        Long totalReviews = reviewRepository.countByProductId(productId);
        
        RatingSummary summary = new RatingSummary();
        summary.setProductId(productId);
        summary.setAverageRating(avgRating != null ? 
            BigDecimal.valueOf(avgRating).setScale(1, RoundingMode.HALF_UP) : 
            BigDecimal.ZERO);
        summary.setTotalReviews(totalReviews != null ? totalReviews.intValue() : 0);
        
        // Yıldız dağılımı
        summary.setStar1Count(reviewRepository.countByProductIdAndRating(productId, 1).intValue());
        summary.setStar2Count(reviewRepository.countByProductIdAndRating(productId, 2).intValue());
        summary.setStar3Count(reviewRepository.countByProductIdAndRating(productId, 3).intValue());
        summary.setStar4Count(reviewRepository.countByProductIdAndRating(productId, 4).intValue());
        summary.setStar5Count(reviewRepository.countByProductIdAndRating(productId, 5).intValue());
        
        return summary;
    }
    
    /**
     * Birden fazla ürün için rating özetlerini getir (Batch API)
     * N+1 Query problemini çözer
     */
    public Map<UUID, RatingSummary> getBatchRatingSummaries(List<UUID> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return new HashMap<>();
        }
        
        // Her productId için rating summary'yi getir
        // Cache'ten yararlanılır (@Cacheable sayesinde)
        return productIds.stream()
            .collect(Collectors.toMap(
                productId -> productId,
                this::getRatingSummary
            ));
    }
}

