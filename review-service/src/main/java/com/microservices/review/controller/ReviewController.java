package com.microservices.review.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.microservices.review.model.RatingSummary;
import com.microservices.review.model.Review;
import com.microservices.review.service.ReviewService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Review Controller
 * Review yönetimi için REST API endpoints
 */
@RestController
@RequestMapping("/reviews")
@Tag(name = "Review Controller", description = "Ürün yorumları ve değerlendirmeleri API")
public class ReviewController {
    
    private final ReviewService reviewService;
    
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }
    
    /**
     * Ürüne ait yorumları getir
     * GET /reviews/product/{productId}
     */
    @GetMapping("/product/{productId}")
    @Operation(summary = "Get reviews by product ID", description = "Belirli bir ürüne ait tüm yorumları getirir")
    public ResponseEntity<List<Review>> getReviewsByProductId(@PathVariable("productId") UUID productId) {
        List<Review> reviews = reviewService.getReviewsByProductId(productId);
        return ResponseEntity.ok(reviews);
    }
    
    /**
     * Kullanıcının yorumlarını getir
     * GET /reviews/user/{userId}
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get reviews by user ID", description = "Belirli bir kullanıcının tüm yorumlarını getirir")
    public ResponseEntity<List<Review>> getReviewsByUserId(@PathVariable("userId") UUID userId) {
        List<Review> reviews = reviewService.getReviewsByUserId(userId);
        return ResponseEntity.ok(reviews);
    }
    
    /**
     * Ürün için rating özeti getir
     * GET /reviews/product/{productId}/summary
     */
    @GetMapping("/product/{productId}/summary")
    @Operation(summary = "Get rating summary", description = "Ürün için rating özet bilgilerini getirir")
    public ResponseEntity<RatingSummary> getRatingSummary(@PathVariable("productId") UUID productId) {
        RatingSummary summary = reviewService.getRatingSummary(productId);
        return ResponseEntity.ok(summary);
    }
    
    /**
     * Birden fazla ürün için rating özetlerini getir (Batch API)
     * GET /reviews/batch/summary?productIds=uuid1,uuid2,uuid3
     * 
     * N+1 Query problemini çözer
     */
    @GetMapping("/batch/summary")
    @Operation(summary = "Get batch rating summaries", description = "Birden fazla ürün için rating özetlerini tek sorguda getirir")
    public ResponseEntity<Map<UUID, RatingSummary>> getBatchRatingSummaries(
            @RequestParam("productIds") List<UUID> productIds) {
        Map<UUID, RatingSummary> summaries = reviewService.getBatchRatingSummaries(productIds);
        return ResponseEntity.ok(summaries);
    }
    
    /**
     * Yeni yorum oluştur
     * POST /reviews
     */
    @PostMapping
    @Operation(summary = "Create review", description = "Yeni bir ürün yorumu oluşturur")
    public ResponseEntity<Review> createReview(@RequestBody Review review) {
        try {
            Review createdReview = reviewService.createReview(review);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdReview);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    /**
     * Yorum güncelle
     * PUT /reviews/{reviewId}
     */
    @PutMapping("/{reviewId}")
    @Operation(summary = "Update review", description = "Mevcut bir yorumu günceller")
    public ResponseEntity<Review> updateReview(@PathVariable("reviewId") UUID reviewId, @RequestBody Review review) {
        review.setId(reviewId);
        Review updatedReview = reviewService.updateReview(review);
        return ResponseEntity.ok(updatedReview);
    }
    
    /**
     * Yorum sil
     * DELETE /reviews/{reviewId}
     */
    @DeleteMapping("/{reviewId}")
    @Operation(summary = "Delete review", description = "Bir yorumu siler")
    public ResponseEntity<Void> deleteReview(
            @PathVariable("reviewId") UUID reviewId,
            @RequestParam("productId") UUID productId) {
        reviewService.deleteReview(reviewId, productId);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Yorum için "Yardımcı Oldu" işaretle
     * POST /reviews/{reviewId}/helpful
     */
    @PostMapping("/{reviewId}/helpful")
    @Operation(summary = "Mark review as helpful", description = "Bir yorumu 'Yardımcı Oldu' olarak işaretler")
    public ResponseEntity<Review> markAsHelpful(@PathVariable("reviewId") UUID reviewId) {
        Review review = reviewService.markAsHelpful(reviewId);
        return ResponseEntity.ok(review);
    }
}

