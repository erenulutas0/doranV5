package com.microservices.shop.service;

import com.microservices.shop.dto.ShopRequest;
import com.microservices.shop.dto.ShopResponse;
import com.microservices.shop.model.Shop;
import com.microservices.shop.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShopService {
    
    private final ShopRepository repository;
    
    /**
     * Yeni dükkan oluştur
     */
    @Transactional
    @CacheEvict(value = {"shops", "shopsByCategory", "shopsByCity"}, allEntries = true)
    public ShopResponse createShop(UUID ownerId, ShopRequest request) {
        log.info("Creating shop for owner: {}", ownerId);
        
        Shop shop = new Shop();
        shop.setOwnerId(ownerId);
        shop.setName(request.getName());
        shop.setDescription(request.getDescription());
        shop.setCategory(request.getCategory());
        shop.setAddress(request.getAddress());
        shop.setCity(request.getCity());
        shop.setDistrict(request.getDistrict());
        shop.setPostalCode(request.getPostalCode());
        shop.setPhone(request.getPhone());
        shop.setEmail(request.getEmail());
        shop.setWebsite(request.getWebsite());
        shop.setLatitude(request.getLatitude());
        shop.setLongitude(request.getLongitude());
        shop.setOpeningTime(request.getOpeningTime());
        shop.setClosingTime(request.getClosingTime());
        shop.setWorkingDays(request.getWorkingDays());
        shop.setLogoImageId(request.getLogoImageId());
        shop.setCoverImageId(request.getCoverImageId());
        shop.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        
        shop = repository.save(shop);
        log.info("Shop created successfully: {}", shop.getId());
        
        return ShopResponse.fromEntity(shop);
    }
    
    /**
     * Dükkanı güncelle (sadece sahibi güncelleyebilir)
     */
    @Transactional
    @org.springframework.cache.annotation.Caching(evict = {
            @CacheEvict(value = "shops", key = "#shopId.toString()"),
            @CacheEvict(value = {"shopsByCategory", "shopsByCity"}, allEntries = true)
    })
    public ShopResponse updateShop(UUID ownerId, UUID shopId, ShopRequest request) {
        log.info("Updating shop {} for owner: {}", shopId, ownerId);
        
        Shop shop = repository.findByIdAndOwnerIdAndDeletedAtIsNull(shopId, ownerId)
                .orElseThrow(() -> new RuntimeException("Shop not found or you don't have permission"));
        
        shop.setName(request.getName());
        shop.setDescription(request.getDescription());
        shop.setCategory(request.getCategory());
        shop.setAddress(request.getAddress());
        shop.setCity(request.getCity());
        shop.setDistrict(request.getDistrict());
        shop.setPostalCode(request.getPostalCode());
        shop.setPhone(request.getPhone());
        shop.setEmail(request.getEmail());
        shop.setWebsite(request.getWebsite());
        shop.setLatitude(request.getLatitude());
        shop.setLongitude(request.getLongitude());
        shop.setOpeningTime(request.getOpeningTime());
        shop.setClosingTime(request.getClosingTime());
        shop.setWorkingDays(request.getWorkingDays());
        shop.setLogoImageId(request.getLogoImageId());
        shop.setCoverImageId(request.getCoverImageId());
        
        if (request.getIsActive() != null) {
            shop.setIsActive(request.getIsActive());
        }
        
        shop = repository.save(shop);
        log.info("Shop updated successfully: {}", shopId);
        
        return ShopResponse.fromEntity(shop);
    }
    
    /**
     * Dükkanı sil (soft delete)
     */
    @Transactional
    @org.springframework.cache.annotation.Caching(evict = {
            @CacheEvict(value = "shops", key = "#shopId.toString()"),
            @CacheEvict(value = {"shopsByCategory", "shopsByCity"}, allEntries = true)
    })
    public void deleteShop(UUID ownerId, UUID shopId) {
        log.info("Deleting shop {} for owner: {}", shopId, ownerId);
        
        Shop shop = repository.findByIdAndOwnerIdAndDeletedAtIsNull(shopId, ownerId)
                .orElseThrow(() -> new RuntimeException("Shop not found or you don't have permission"));
        
        shop.setIsActive(false);
        shop.setDeletedAt(LocalDateTime.now());
        
        repository.save(shop);
        log.info("Shop deleted successfully: {}", shopId);
    }
    
    /**
     * Sahibinin dükkanlarını getir
     */
    @Transactional(readOnly = true)
    public List<ShopResponse> getOwnerShops(UUID ownerId) {
        log.debug("Fetching shops for owner: {}", ownerId);
        return repository.findByOwnerIdAndDeletedAtIsNullOrderByCreatedAtDesc(ownerId)
                .stream()
                .map(ShopResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * Aktif dükkanları sayfalı olarak getir
     */
    @Transactional(readOnly = true)
    public Page<ShopResponse> getActiveShops(Pageable pageable) {
        log.debug("Fetching active shops");
        return repository.findActiveShops(pageable)
                .map(ShopResponse::fromEntity);
    }
    
    /**
     * Kategoriye göre aktif dükkanları getir
     */
    @Transactional(readOnly = true)
    public Page<ShopResponse> getActiveShopsByCategory(String category, Pageable pageable) {
        log.debug("Fetching active shops by category: {}", category);
        return repository.findActiveShopsByCategory(category, pageable)
                .map(ShopResponse::fromEntity);
    }
    
    /**
     * Şehre göre aktif dükkanları getir
     */
    @Transactional(readOnly = true)
    public Page<ShopResponse> getActiveShopsByCity(String city, Pageable pageable) {
        log.debug("Fetching active shops by city: {}", city);
        return repository.findActiveShopsByCity(city, pageable)
                .map(ShopResponse::fromEntity);
    }
    
    /**
     * Arama sorgusu ile aktif dükkanları getir
     */
    @Transactional(readOnly = true)
    public Page<ShopResponse> searchActiveShops(String query, Pageable pageable) {
        log.debug("Searching active shops with query: {}", query);
        return repository.searchActiveShops(query, pageable)
                .map(ShopResponse::fromEntity);
    }
    
    /**
     * Yakındaki dükkanları getir (konum bazlı)
     */
    @Transactional(readOnly = true)
    public List<ShopResponse> getNearbyShops(BigDecimal latitude, BigDecimal longitude, double radiusKm) {
        log.debug("Fetching nearby shops - Lat: {}, Lng: {}, Radius: {} km", latitude, longitude, radiusKm);
        return repository.findNearbyShops(latitude, longitude, radiusKm)
                .stream()
                .map(ShopResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * Dükkan detayını getir
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "shops", key = "#shopId.toString()")
    public ShopResponse getShopById(UUID shopId) {
        log.debug("Fetching shop: {}", shopId);
        
        Shop shop = repository.findByIdAndDeletedAtIsNull(shopId)
                .orElseThrow(() -> new RuntimeException("Shop not found"));
        
        // Sadece aktif dükkanlar görüntülenebilir (sahibi hariç)
        // Bu kontrolü controller'da yapabiliriz
        
        return ShopResponse.fromEntity(shop);
    }
}

