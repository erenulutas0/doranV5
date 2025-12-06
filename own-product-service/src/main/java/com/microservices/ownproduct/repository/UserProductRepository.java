package com.microservices.ownproduct.repository;

import com.microservices.ownproduct.model.UserProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserProductRepository extends JpaRepository<UserProduct, UUID> {
    
    /**
     * Kullanıcının tüm ürünlerini getir (silinmemiş)
     */
    List<UserProduct> findByUserIdAndDeletedAtIsNullOrderByCreatedAtDesc(UUID userId);
    
    /**
     * Kullanıcının ürünlerini sayfalı olarak getir
     */
    Page<UserProduct> findByUserIdAndDeletedAtIsNullOrderByCreatedAtDesc(UUID userId, Pageable pageable);
    
    /**
     * Yayında olan ürünleri getir (herkese açık)
     */
    @Query("SELECT up FROM UserProduct up WHERE up.status = 'PUBLISHED' AND up.visibility = 'PUBLIC' AND up.deletedAt IS NULL ORDER BY up.publishedAt DESC")
    Page<UserProduct> findPublishedPublicProducts(Pageable pageable);
    
    /**
     * Kategoriye göre yayında olan ürünleri getir
     */
    @Query("SELECT up FROM UserProduct up WHERE up.status = 'PUBLISHED' AND up.visibility = 'PUBLIC' AND up.category = :category AND up.deletedAt IS NULL ORDER BY up.publishedAt DESC")
    Page<UserProduct> findPublishedPublicProductsByCategory(@Param("category") String category, Pageable pageable);
    
    /**
     * Arama sorgusu ile yayında olan ürünleri getir
     */
    @Query("SELECT up FROM UserProduct up WHERE up.status = 'PUBLISHED' AND up.visibility = 'PUBLIC' AND up.deletedAt IS NULL AND (LOWER(up.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(up.description) LIKE LOWER(CONCAT('%', :query, '%'))) ORDER BY up.publishedAt DESC")
    Page<UserProduct> searchPublishedPublicProducts(@Param("query") String query, Pageable pageable);
    
    /**
     * Kullanıcının belirli bir ürününü getir (sahipliği kontrolü için)
     */
    Optional<UserProduct> findByIdAndUserIdAndDeletedAtIsNull(UUID id, UUID userId);
    
    /**
     * ID'ye göre ürün getir (silinmemiş)
     */
    Optional<UserProduct> findByIdAndDeletedAtIsNull(UUID id);
    
    /**
     * Kullanıcının taslak ürünlerini getir
     */
    List<UserProduct> findByUserIdAndStatusAndDeletedAtIsNullOrderByCreatedAtDesc(UUID userId, UserProduct.ProductStatus status);
}

