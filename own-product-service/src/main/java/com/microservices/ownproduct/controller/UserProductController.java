package com.microservices.ownproduct.controller;

import com.microservices.ownproduct.dto.UserProductRequest;
import com.microservices.ownproduct.dto.UserProductResponse;
import com.microservices.ownproduct.service.UserProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/user-products")
@RequiredArgsConstructor
@Slf4j
public class UserProductController {
    
    private final UserProductService service;
    
    /**
     * Kullanıcı için yeni ürün oluştur
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserProductResponse createProduct(
            @RequestHeader("X-User-Id") UUID userId,
            @Valid @RequestBody UserProductRequest request) {
        log.info("POST /api/user-products - User: {}", userId);
        return service.createProduct(userId, request);
    }
    
    /**
     * Ürünü güncelle
     */
    @PutMapping("/{productId}")
    public UserProductResponse updateProduct(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID productId,
            @Valid @RequestBody UserProductRequest request) {
        log.info("PUT /api/user-products/{} - User: {}", productId, userId);
        return service.updateProduct(userId, productId, request);
    }
    
    /**
     * Ürünü sil
     */
    @DeleteMapping("/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID productId) {
        log.info("DELETE /api/user-products/{} - User: {}", productId, userId);
        service.deleteProduct(userId, productId);
    }
    
    /**
     * Kullanıcının ürünlerini getir
     */
    @GetMapping("/my-products")
    public List<UserProductResponse> getMyProducts(@RequestHeader("X-User-Id") UUID userId) {
        log.info("GET /api/user-products/my-products - User: {}", userId);
        return service.getUserProducts(userId);
    }
    
    /**
     * Yayında olan ürünleri getir (sayfalı)
     */
    @GetMapping("/published")
    public Page<UserProductResponse> getPublishedProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "publishedAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {
        log.info("GET /api/user-products/published - Page: {}, Size: {}", page, size);
        
        Sort sort = sortDir.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        return service.getPublishedProducts(pageable);
    }
    
    /**
     * Kategoriye göre yayında olan ürünleri getir
     */
    @GetMapping("/published/category/{category}")
    public Page<UserProductResponse> getPublishedProductsByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("GET /api/user-products/published/category/{} - Page: {}, Size: {}", category, page, size);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("publishedAt").descending());
        return service.getPublishedProductsByCategory(category, pageable);
    }
    
    /**
     * Arama sorgusu ile yayında olan ürünleri getir
     */
    @GetMapping("/published/search")
    public Page<UserProductResponse> searchPublishedProducts(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("GET /api/user-products/published/search?q={} - Page: {}, Size: {}", q, page, size);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("publishedAt").descending());
        return service.searchPublishedProducts(q, pageable);
    }
    
    /**
     * Ürün detayını getir
     */
    @GetMapping("/{productId}")
    public UserProductResponse getProductById(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID productId) {
        log.info("GET /api/user-products/{} - User: {}", productId, userId);
        return service.getProductById(productId, userId);
    }
}

