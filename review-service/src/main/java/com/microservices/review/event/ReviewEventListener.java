package com.microservices.review.event;

import com.microservices.review.model.Review;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Review Event Listener
 * 
 * Review event'lerini asenkron olarak işler:
 * - Email notification gönderme
 * - Analytics güncelleme
 * - Product rating cache'ini güncelleme
 * - Admin notification (low rating için)
 */
@Component
@Slf4j
public class ReviewEventListener {
    
    /**
     * Review Created Event Handler
     * 
     * Async olarak çalışır, main thread'i bloklamaz
     * Background'da notification ve analytics işlemleri yapar
     */
    @Async("taskExecutor")
    @EventListener
    public void handleReviewCreatedEvent(ReviewCreatedEvent event) {
        Review review = event.getReview();
        
        log.info("Processing review created event async for review ID: {}", review.getId());
        
        try {
            // 1. Send notification to product owner
            sendNotificationToProductOwner(review);
            
            // 2. Update analytics
            updateProductAnalytics(review);
            
            // 3. Send admin notification if rating is low
            if (review.getRating() <= 2) {
                sendLowRatingNotificationToAdmin(review);
            }
            
            // 4. Update recommendation engine (if applicable)
            updateRecommendationEngine(review);
            
            log.info("Successfully processed review created event for review ID: {}", review.getId());
            
        } catch (Exception e) {
            log.error("Error processing review created event for review ID: {}", review.getId(), e);
            // Error handling - retry logic, dead letter queue, etc.
        }
    }
    
    /**
     * Send notification to product owner
     * 
     * TODO: Integrate with notification service
     */
    private void sendNotificationToProductOwner(Review review) {
        log.info("Sending notification to product owner for review: {}", review.getId());
        
        // Simulate notification sending (replace with actual notification service call)
        try {
            Thread.sleep(500); // Simulate network call
            log.info("Notification sent successfully for review: {}", review.getId());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Failed to send notification", e);
        }
    }
    
    /**
     * Update product analytics
     * 
     * TODO: Integrate with analytics service
     */
    private void updateProductAnalytics(Review review) {
        log.info("Updating analytics for product: {}", review.getProductId());
        
        // Simulate analytics update
        try {
            Thread.sleep(300);
            log.info("Analytics updated for product: {}", review.getProductId());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Failed to update analytics", e);
        }
    }
    
    /**
     * Send low rating notification to admin
     * 
     * TODO: Integrate with notification service
     */
    private void sendLowRatingNotificationToAdmin(Review review) {
        log.warn("Low rating ({}) detected for product: {}. Sending admin notification.",
                review.getRating(), review.getProductId());
        
        // Simulate admin notification
        try {
            Thread.sleep(200);
            log.info("Admin notification sent for low rating review: {}", review.getId());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Failed to send admin notification", e);
        }
    }
    
    /**
     * Update recommendation engine
     * 
     * TODO: Integrate with recommendation service
     */
    private void updateRecommendationEngine(Review review) {
        log.info("Updating recommendation engine for user: {}", review.getUserId());
        
        // Simulate recommendation update
        try {
            Thread.sleep(400);
            log.info("Recommendation engine updated for user: {}", review.getUserId());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Failed to update recommendation engine", e);
        }
    }
}

