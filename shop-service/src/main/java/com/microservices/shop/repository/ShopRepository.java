package com.microservices.shop.repository;

import com.microservices.shop.model.Shop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShopRepository extends JpaRepository<Shop, UUID> {
    
    /**
     * Sahibinin tüm dükkanlarını getir (silinmemiş)
     */
    List<Shop> findByOwnerIdAndDeletedAtIsNullOrderByCreatedAtDesc(UUID ownerId);
    
    /**
     * Sahibinin dükkanlarını sayfalı olarak getir
     */
    Page<Shop> findByOwnerIdAndDeletedAtIsNullOrderByCreatedAtDesc(UUID ownerId, Pageable pageable);
    
    /**
     * Aktif dükkanları getir
     */
    @Query("SELECT s FROM Shop s WHERE s.isActive = true AND s.deletedAt IS NULL ORDER BY s.createdAt DESC")
    Page<Shop> findActiveShops(Pageable pageable);
    
    /**
     * Kategoriye göre aktif dükkanları getir
     */
    @Query("SELECT s FROM Shop s WHERE s.isActive = true AND s.category = :category AND s.deletedAt IS NULL ORDER BY s.averageRating DESC, s.createdAt DESC")
    Page<Shop> findActiveShopsByCategory(@Param("category") String category, Pageable pageable);
    
    /**
     * Şehre göre aktif dükkanları getir
     */
    @Query("SELECT s FROM Shop s WHERE s.isActive = true AND s.city = :city AND s.deletedAt IS NULL ORDER BY s.averageRating DESC, s.createdAt DESC")
    Page<Shop> findActiveShopsByCity(@Param("city") String city, Pageable pageable);
    
    /**
     * Arama sorgusu ile aktif dükkanları getir
     */
    @Query("SELECT s FROM Shop s WHERE s.isActive = true AND s.deletedAt IS NULL AND (LOWER(s.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(s.description) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(s.category) LIKE LOWER(CONCAT('%', :query, '%'))) ORDER BY s.averageRating DESC, s.createdAt DESC")
    Page<Shop> searchActiveShops(@Param("query") String query, Pageable pageable);
    
    /**
     * Konum bazlı arama (yakındaki dükkanlar)
     * Haversine formülü kullanarak mesafe hesaplama
     */
    @Query(value = "SELECT s.*, " +
            "(6371 * acos(cos(radians(:lat)) * cos(radians(s.latitude)) * " +
            "cos(radians(s.longitude) - radians(:lng)) + sin(radians(:lat)) * " +
            "sin(radians(s.latitude)))) AS distance " +
            "FROM shops s " +
            "WHERE s.is_active = true AND s.deleted_at IS NULL " +
            "AND s.latitude IS NOT NULL AND s.longitude IS NOT NULL " +
            "HAVING distance <= :radius " +
            "ORDER BY distance ASC", 
            nativeQuery = true)
    List<Shop> findNearbyShops(@Param("lat") BigDecimal latitude, 
                               @Param("lng") BigDecimal longitude, 
                               @Param("radius") double radiusKm);
    
    /**
     * Sahibinin belirli bir dükkanını getir
     */
    Optional<Shop> findByIdAndOwnerIdAndDeletedAtIsNull(UUID id, UUID ownerId);
    
    /**
     * ID'ye göre dükkan getir (silinmemiş)
     */
    Optional<Shop> findByIdAndDeletedAtIsNull(UUID id);
}

