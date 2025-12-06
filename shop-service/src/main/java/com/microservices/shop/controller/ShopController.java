package com.microservices.shop.controller;

import com.microservices.shop.dto.ShopRequest;
import com.microservices.shop.dto.ShopResponse;
import com.microservices.shop.service.ShopService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/shops")
@RequiredArgsConstructor
@Slf4j
public class ShopController {
    
    private final ShopService service;
    
    /**
     * Yeni dükkan oluştur
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ShopResponse createShop(
            @RequestHeader("X-User-Id") UUID ownerId,
            @Valid @RequestBody ShopRequest request) {
        log.info("POST /api/shops - Owner: {}", ownerId);
        return service.createShop(ownerId, request);
    }
    
    /**
     * Dükkanı güncelle
     */
    @PutMapping("/{shopId}")
    public ShopResponse updateShop(
            @RequestHeader("X-User-Id") UUID ownerId,
            @PathVariable UUID shopId,
            @Valid @RequestBody ShopRequest request) {
        log.info("PUT /api/shops/{} - Owner: {}", shopId, ownerId);
        return service.updateShop(ownerId, shopId, request);
    }
    
    /**
     * Dükkanı sil
     */
    @DeleteMapping("/{shopId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteShop(
            @RequestHeader("X-User-Id") UUID ownerId,
            @PathVariable UUID shopId) {
        log.info("DELETE /api/shops/{} - Owner: {}", shopId, ownerId);
        service.deleteShop(ownerId, shopId);
    }
    
    /**
     * Sahibinin dükkanlarını getir
     */
    @GetMapping("/my-shops")
    public List<ShopResponse> getMyShops(@RequestHeader("X-User-Id") UUID ownerId) {
        log.info("GET /api/shops/my-shops - Owner: {}", ownerId);
        return service.getOwnerShops(ownerId);
    }
    
    /**
     * Aktif dükkanları getir (sayfalı)
     */
    @GetMapping("/active")
    public Page<ShopResponse> getActiveShops(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {
        log.info("GET /api/shops/active - Page: {}, Size: {}", page, size);
        
        Sort sort = sortDir.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        return service.getActiveShops(pageable);
    }
    
    /**
     * Kategoriye göre aktif dükkanları getir
     */
    @GetMapping("/active/category/{category}")
    public Page<ShopResponse> getActiveShopsByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("GET /api/shops/active/category/{} - Page: {}, Size: {}", category, page, size);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("averageRating").descending().and(Sort.by("createdAt").descending()));
        return service.getActiveShopsByCategory(category, pageable);
    }
    
    /**
     * Şehre göre aktif dükkanları getir
     */
    @GetMapping("/active/city/{city}")
    public Page<ShopResponse> getActiveShopsByCity(
            @PathVariable String city,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("GET /api/shops/active/city/{} - Page: {}, Size: {}", city, page, size);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("averageRating").descending().and(Sort.by("createdAt").descending()));
        return service.getActiveShopsByCity(city, pageable);
    }
    
    /**
     * Arama sorgusu ile aktif dükkanları getir
     */
    @GetMapping("/active/search")
    public Page<ShopResponse> searchActiveShops(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("GET /api/shops/active/search?q={} - Page: {}, Size: {}", q, page, size);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("averageRating").descending().and(Sort.by("createdAt").descending()));
        return service.searchActiveShops(q, pageable);
    }
    
    /**
     * Yakındaki dükkanları getir (konum bazlı)
     */
    @GetMapping("/nearby")
    public List<ShopResponse> getNearbyShops(
            @RequestParam BigDecimal latitude,
            @RequestParam BigDecimal longitude,
            @RequestParam(defaultValue = "5.0") double radiusKm) {
        log.info("GET /api/shops/nearby - Lat: {}, Lng: {}, Radius: {} km", latitude, longitude, radiusKm);
        return service.getNearbyShops(latitude, longitude, radiusKm);
    }
    
    /**
     * Dükkan detayını getir
     */
    @GetMapping("/{shopId}")
    public ShopResponse getShopById(@PathVariable UUID shopId) {
        log.info("GET /api/shops/{}", shopId);
        return service.getShopById(shopId);
    }
}

