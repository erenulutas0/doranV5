package com.microservices.ownproduct.service;

import com.microservices.ownproduct.dto.UserProductRequest;
import com.microservices.ownproduct.dto.UserProductResponse;
import com.microservices.ownproduct.model.UserProduct;
import com.microservices.ownproduct.repository.UserProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserProductService {
    
    private final UserProductRepository repository;
    
    /**
     * Kullanıcı için yeni ürün oluştur
     */
    @Transactional
    public UserProductResponse createProduct(UUID userId, UserProductRequest request) {
        log.info("Creating product for user: {}", userId);
        
        UserProduct product = new UserProduct();
        product.setUserId(userId);
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setCategory(request.getCategory());
        product.setLocation(request.getLocation());
        product.setContactInfo(request.getContactInfo());
        product.setPrimaryImageId(request.getPrimaryImageId());
        
        // Status ve Visibility ayarla
        if (request.getStatus() != null) {
            try {
                product.setStatus(UserProduct.ProductStatus.valueOf(request.getStatus().toUpperCase()));
            } catch (IllegalArgumentException e) {
                product.setStatus(UserProduct.ProductStatus.DRAFT);
            }
        } else {
            product.setStatus(UserProduct.ProductStatus.DRAFT);
        }
        
        if (request.getVisibility() != null) {
            try {
                product.setVisibility(UserProduct.Visibility.valueOf(request.getVisibility().toUpperCase()));
            } catch (IllegalArgumentException e) {
                product.setVisibility(UserProduct.Visibility.PUBLIC);
            }
        } else {
            product.setVisibility(UserProduct.Visibility.PUBLIC);
        }
        
        // Eğer PUBLISHED ise publishedAt'i ayarla
        if (product.getStatus() == UserProduct.ProductStatus.PUBLISHED) {
            product.setPublishedAt(LocalDateTime.now());
        }
        
        product = repository.save(product);
        log.info("Product created successfully: {}", product.getId());
        
        return UserProductResponse.fromEntity(product);
    }
    
    /**
     * Ürünü güncelle (sadece sahibi güncelleyebilir)
     */
    @Transactional
    public UserProductResponse updateProduct(UUID userId, UUID productId, UserProductRequest request) {
        log.info("Updating product {} for user: {}", productId, userId);
        
        UserProduct product = repository.findByIdAndUserIdAndDeletedAtIsNull(productId, userId)
                .orElseThrow(() -> new RuntimeException("Product not found or you don't have permission"));
        
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setCategory(request.getCategory());
        product.setLocation(request.getLocation());
        product.setContactInfo(request.getContactInfo());
        product.setPrimaryImageId(request.getPrimaryImageId());
        
        // Status güncellemesi
        if (request.getStatus() != null) {
            try {
                UserProduct.ProductStatus newStatus = UserProduct.ProductStatus.valueOf(request.getStatus().toUpperCase());
                product.setStatus(newStatus);
                
                // Eğer PUBLISHED'e geçiliyorsa ve daha önce publishedAt yoksa
                if (newStatus == UserProduct.ProductStatus.PUBLISHED && product.getPublishedAt() == null) {
                    product.setPublishedAt(LocalDateTime.now());
                }
            } catch (IllegalArgumentException e) {
                log.warn("Invalid status: {}", request.getStatus());
            }
        }
        
        // Visibility güncellemesi
        if (request.getVisibility() != null) {
            try {
                product.setVisibility(UserProduct.Visibility.valueOf(request.getVisibility().toUpperCase()));
            } catch (IllegalArgumentException e) {
                log.warn("Invalid visibility: {}", request.getVisibility());
            }
        }
        
        product = repository.save(product);
        log.info("Product updated successfully: {}", productId);
        
        return UserProductResponse.fromEntity(product);
    }
    
    /**
     * Ürünü sil (soft delete)
     */
    @Transactional
    public void deleteProduct(UUID userId, UUID productId) {
        log.info("Deleting product {} for user: {}", productId, userId);
        
        UserProduct product = repository.findByIdAndUserIdAndDeletedAtIsNull(productId, userId)
                .orElseThrow(() -> new RuntimeException("Product not found or you don't have permission"));
        
        product.setStatus(UserProduct.ProductStatus.DELETED);
        product.setDeletedAt(LocalDateTime.now());
        
        repository.save(product);
        log.info("Product deleted successfully: {}", productId);
    }
    
    /**
     * Kullanıcının ürünlerini getir
     */
    @Transactional(readOnly = true)
    public List<UserProductResponse> getUserProducts(UUID userId) {
        log.debug("Fetching products for user: {}", userId);
        return repository.findByUserIdAndDeletedAtIsNullOrderByCreatedAtDesc(userId)
                .stream()
                .map(UserProductResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * Yayında olan ürünleri sayfalı olarak getir
     */
    @Transactional(readOnly = true)
    public Page<UserProductResponse> getPublishedProducts(Pageable pageable) {
        log.debug("Fetching published products");
        return repository.findPublishedPublicProducts(pageable)
                .map(UserProductResponse::fromEntity);
    }
    
    /**
     * Kategoriye göre yayında olan ürünleri getir
     */
    @Transactional(readOnly = true)
    public Page<UserProductResponse> getPublishedProductsByCategory(String category, Pageable pageable) {
        log.debug("Fetching published products by category: {}", category);
        return repository.findPublishedPublicProductsByCategory(category, pageable)
                .map(UserProductResponse::fromEntity);
    }
    
    /**
     * Arama sorgusu ile yayında olan ürünleri getir
     */
    @Transactional(readOnly = true)
    public Page<UserProductResponse> searchPublishedProducts(String query, Pageable pageable) {
        log.debug("Searching published products with query: {}", query);
        return repository.searchPublishedPublicProducts(query, pageable)
                .map(UserProductResponse::fromEntity);
    }
    
    /**
     * Ürün detayını getir (yayında olan veya sahibi)
     */
    @Transactional(readOnly = true)
    public UserProductResponse getProductById(UUID productId, UUID userId) {
        log.debug("Fetching product: {}", productId);
        
        UserProduct product = repository.findByIdAndDeletedAtIsNull(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        // Eğer ürün sahibi değilse ve ürün PUBLIC değilse veya PUBLISHED değilse
        if (!product.getUserId().equals(userId)) {
            if (product.getVisibility() != UserProduct.Visibility.PUBLIC ||
                product.getStatus() != UserProduct.ProductStatus.PUBLISHED) {
                throw new RuntimeException("Product not accessible");
            }
        }
        
        return UserProductResponse.fromEntity(product);
    }
}

