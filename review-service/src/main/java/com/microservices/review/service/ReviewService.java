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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.microservices.review.event.ReviewCreatedEvent;
import com.microservices.review.model.RatingSummary;
import com.microservices.review.model.Review;
import com.microservices.review.model.ReviewHelpful;
import com.microservices.review.repository.ReviewRepository;
import com.microservices.review.repository.ReviewHelpfulRepository;

/**
 * Review Service
 * Review iş mantığı için service
 */
@Service
public class ReviewService {
    
    private final ReviewRepository reviewRepository;
    private final ReviewHelpfulRepository reviewHelpfulRepository;
    private final ApplicationEventPublisher eventPublisher;
    
    public ReviewService(ReviewRepository reviewRepository, 
                        ReviewHelpfulRepository reviewHelpfulRepository,
                        ApplicationEventPublisher eventPublisher) {
        this.reviewRepository = reviewRepository;
        this.reviewHelpfulRepository = reviewHelpfulRepository;
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
     * visitorId verilirse, kullanıcının hangi review'ları beğendiği bilgisi de döner
     * visitorId: String formatında user ID (örn: "user_123456")
     * 
     * NOT: Cache geçici olarak devre dışı - helpfulCount ve likedByUser güncellemeleri
     * cache'den eski veri döndüğü için sorun yaratıyor
     */
    public List<Review> getReviewsByProductId(UUID productId, String visitorId) {
        System.out.println("🔍 getReviewsByProductId Service çağrısı:");
        System.out.println("   Product ID: " + productId);
        System.out.println("   Visitor ID: " + visitorId);
        
        List<Review> reviews = reviewRepository.findByProductIdAndIsApprovedTrueOrderByCreatedAtDesc(productId);
        System.out.println("✅ Database'den " + reviews.size() + " review alındı");
        
        if (!reviews.isEmpty()) {
            List<UUID> reviewIds = reviews.stream()
                .map(Review::getId)
                .collect(java.util.stream.Collectors.toList());
            
            // Her review için helpfulCount'u review_helpful tablosundan güncel olarak hesapla
            System.out.println("📊 helpfulCount değerleri güncelleniyor...");
            for (Review review : reviews) {
                Long actualHelpfulCount = reviewHelpfulRepository.countByReviewId(review.getId());
                review.setHelpfulCount(actualHelpfulCount.intValue());
                System.out.println("   Review " + review.getId() + ": helpfulCount = " + review.getHelpfulCount());
            }
            
            // Eğer visitorId verilmişse, kullanıcının hangi review'ları beğendiğini kontrol et
            if (visitorId != null && !visitorId.isEmpty()) {
                System.out.println("👍 Kullanıcının beğendiği review'lar kontrol ediliyor...");
                System.out.println("   Visitor ID: " + visitorId);
                
                try {
                    // Kullanıcının beğendiği review ID'lerini toplu olarak getir (optimize edilmiş sorgu)
                    List<UUID> likedReviewIds = reviewHelpfulRepository.findLikedReviewIdsByVisitorIdAndReviewIds(visitorId, reviewIds);
                    System.out.println("   Beğenilen review sayısı: " + likedReviewIds.size());
                    System.out.println("   Beğenilen review ID'leri: " + likedReviewIds);
                    
                    // Her review için likedByUser bilgisini set et
                    for (Review review : reviews) {
                        boolean liked = likedReviewIds.contains(review.getId());
                        review.setLikedByUser(liked);
                        if (liked) {
                            System.out.println("   ✅ Review " + review.getId() + ": likedByUser = true");
                        } else {
                            // Debug için: likedByUser false olan review'ları da logla (ilk 3 tanesini)
                            if (reviews.indexOf(review) < 3) {
                                System.out.println("   ❌ Review " + review.getId() + ": likedByUser = false");
                            }
                        }
                    }
                } catch (Exception e) {
                    System.out.println("❌ likedByUser kontrolünde hata: " + e.getMessage());
                    e.printStackTrace();
                    // Hata durumunda tüm review'lar için likedByUser = false yap
                    for (Review review : reviews) {
                        review.setLikedByUser(false);
                    }
                }
            } else {
                System.out.println("⚠️ Visitor ID null veya boş - likedByUser kontrolü yapılmıyor");
                // Visitor ID yoksa tüm review'lar için likedByUser = false
                for (Review review : reviews) {
                    review.setLikedByUser(false);
                }
            }
        }
        
        System.out.println("✅ getReviewsByProductId tamamlandı");
        return reviews;
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
     * Yorum için "Yardımcı Oldu" işaretle
     * Kullanıcı bazında takip edilir - aynı kullanıcı aynı yorumu birden fazla beğenemez
     * visitorId: String formatında user ID (örn: "user_123456")
     */
    @Transactional
    @CacheEvict(value = {"reviews", "ratingSummary"}, allEntries = true) // Cache'i tamamen temizle
    public Review markAsHelpful(UUID reviewId, String visitorId) {
        System.out.println("🔍 ReviewService.markAsHelpful çağrısı:");
        System.out.println("   Review ID: " + reviewId);
        System.out.println("   Visitor ID: " + visitorId);
        
        // Yorumun varlığını kontrol et
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> {
                System.out.println("❌ Review bulunamadı: " + reviewId);
                return new IllegalArgumentException("Review not found");
            });
        
        System.out.println("✅ Review bulundu: " + review.getId());
        System.out.println("   Mevcut helpful_count: " + review.getHelpfulCount());
        UUID productId = review.getProductId();
        System.out.println("   Product ID: " + productId);
        
        // Kullanıcı daha önce bu yorumu beğenmiş mi kontrol et
        if (reviewHelpfulRepository.findByReviewIdAndVisitorId(reviewId, visitorId).isPresent()) {
            System.out.println("⚠️ Kullanıcı zaten beğenmiş: " + visitorId);
            throw new IllegalStateException("User has already marked this review as helpful");
        }
        
        // Yeni beğeni kaydı oluştur
        ReviewHelpful reviewHelpful = new ReviewHelpful();
        reviewHelpful.setReviewId(reviewId);
        reviewHelpful.setVisitorId(visitorId);
        System.out.println("💾 ReviewHelpful kaydı oluşturuluyor...");
        reviewHelpfulRepository.save(reviewHelpful);
        reviewHelpfulRepository.flush(); // Database'e yazmayı garanti et
        System.out.println("✅ ReviewHelpful kaydedildi");
        
        // Trigger otomatik olarak helpful_count'u güncelleyecek
        // Ama güvenlik için manuel olarak da güncelleyelim
        Long actualCount = reviewHelpfulRepository.countByReviewId(reviewId);
        System.out.println("📊 Actual count (review_helpful tablosundan): " + actualCount);
        review.setHelpfulCount(actualCount.intValue());
        
        // Review'ı kaydet ve refresh et (database'den güncel veriyi al)
        System.out.println("💾 Review güncelleniyor...");
        Review savedReview = reviewRepository.save(review);
        reviewRepository.flush(); // Database'e yazmayı garanti et
        System.out.println("✅ Review kaydedildi, helpful_count: " + savedReview.getHelpfulCount());
        
        // Database'den tekrar oku (trigger'ın güncellediği değeri almak için)
        // EntityManager refresh ile güncel veriyi al
        Review refreshedReview = reviewRepository.findById(reviewId)
            .orElse(savedReview);
        
        // helpful_count'u tekrar kontrol et (trigger çalıştıysa güncellenmiş olmalı)
        Long finalCount = reviewHelpfulRepository.countByReviewId(reviewId);
        System.out.println("📊 Final count (trigger sonrası): " + finalCount);
        refreshedReview.setHelpfulCount(finalCount.intValue());
        
        System.out.println("✅ markAsHelpful tamamlandı, dönen helpful_count: " + refreshedReview.getHelpfulCount());
        
        return refreshedReview;
    }
    
    
    /**
     * Ürün için rating özeti getir
     * Cache geçici olarak devre dışı - Redis serialization sorunu nedeniyle
     */
    // @Cacheable(value = "ratingSummary", key = "#a0")
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

