package com.microservices.review.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.microservices.review.model.ReviewHelpful;

/**
 * ReviewHelpful Repository
 * Yorum beğenileri için repository
 */
@Repository
public interface ReviewHelpfulRepository extends JpaRepository<ReviewHelpful, UUID> {
    
    /**
     * Belirli bir kullanıcının belirli bir yorumu beğenip beğenmediğini kontrol et
     * visitorId: String formatında user ID (örn: "user_123456")
     */
    Optional<ReviewHelpful> findByReviewIdAndVisitorId(UUID reviewId, String visitorId);
    
    /**
     * Bir yorumun toplam beğeni sayısını getir
     */
    @Query("SELECT COUNT(rh) FROM ReviewHelpful rh WHERE rh.reviewId = :reviewId")
    Long countByReviewId(@Param("reviewId") UUID reviewId);
    
    /**
     * Bir kullanıcının beğendiği yorum sayısını getir
     */
    @Query("SELECT COUNT(rh) FROM ReviewHelpful rh WHERE rh.visitorId = :visitorId")
    Long countByVisitorId(@Param("visitorId") String visitorId);
    
    /**
     * Bir yorumu beğenen kullanıcıları sil (yorum silindiğinde)
     */
    void deleteByReviewId(UUID reviewId);
    
    /**
     * Belirli bir kullanıcının belirli review ID'lerinden hangilerini beğendiğini getir
     * Performans için: toplu sorgu
     * visitorId: String formatında user ID (örn: "user_123456")
     */
    @Query("SELECT rh.reviewId FROM ReviewHelpful rh WHERE rh.visitorId = :visitorId AND rh.reviewId IN :reviewIds")
    List<UUID> findLikedReviewIdsByVisitorIdAndReviewIds(@Param("visitorId") String visitorId, @Param("reviewIds") List<UUID> reviewIds);
}


