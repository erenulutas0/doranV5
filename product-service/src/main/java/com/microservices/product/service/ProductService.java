package com.microservices.product.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.microservices.product.Exception.ResourceNotFoundException;
import com.microservices.product.Model.Product;
import com.microservices.product.Repository.ProductRepository;
import com.microservices.product.service.ReviewServiceClient;
import com.microservices.product.service.ReviewServiceClient.RatingSummary;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ReviewServiceClient reviewServiceClient;

    public ProductService(ProductRepository productRepository, ReviewServiceClient reviewServiceClient) {
        this.productRepository = productRepository;
        this.reviewServiceClient = reviewServiceClient;
    }

    /**
     * Tüm ürünleri getir
     * Review-service'den rating ve reviewCount bilgilerini çekip ekler
     */
    @Cacheable(value = "products", key = "'all'")
    public List<Product> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return enrichProductsWithRatings(products);
    }

    /**
     * ID'ye göre ürün getir
     * Review-service'den rating ve reviewCount bilgilerini çekip ekler
     */
    @Cacheable(value = "products", key = "#productId.toString()")
    public Product getProductById(UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        return enrichProductWithRating(product);
    }

    /**
     * Kategoriye göre ürünleri getir
     * Review-service'den rating ve reviewCount bilgilerini çekip ekler
     */
    @Cacheable(value = "products", key = "'category:' + #category")
    public List<Product> getProductsByCategory(String category) {
        List<Product> products = productRepository.findByCategory(category);
        return enrichProductsWithRatings(products);
    }

    /**
     * Aktif ürünleri getir
     * Review-service'den rating ve reviewCount bilgilerini çekip ekler
     */
    @Cacheable(value = "products", key = "'active'")
    public List<Product> getActiveProducts() {
        List<Product> products = productRepository.findByIsActiveTrue();
        return enrichProductsWithRatings(products);
    }

    /**
     * Featured ürünleri getir (en yüksek rating'e sahip aktif ürünler, maksimum 6)
     * Review-service'den rating ve reviewCount bilgilerini çekip ekler
     */
    @Cacheable(value = "products", key = "'featured'")
    public List<Product> getFeaturedProducts() {
        List<Product> products = productRepository.findByIsActiveTrue();
        List<Product> enrichedProducts = enrichProductsWithRatings(products);
        
        // Rating'e göre sırala (yüksekten düşüğe) ve ilk 6'sını al
        return enrichedProducts.stream()
                .sorted((a, b) -> {
                    BigDecimal ratingA = a.getAverageRating() != null ? a.getAverageRating() : BigDecimal.ZERO;
                    BigDecimal ratingB = b.getAverageRating() != null ? b.getAverageRating() : BigDecimal.ZERO;
                    return ratingB.compareTo(ratingA); // Yüksekten düşüğe
                })
                .limit(6)
                .collect(Collectors.toList());
    }
    
    /**
     * Product listesini rating ve reviewCount ile zenginleştir
     */
    private List<Product> enrichProductsWithRatings(List<Product> products) {
        return products.stream()
                .map(this::enrichProductWithRating)
                .collect(Collectors.toList());
    }
    
    /**
     * Product'ı rating ve reviewCount ile zenginleştir
     */
    private Product enrichProductWithRating(Product product) {
        try {
            RatingSummary ratingSummary = reviewServiceClient.getRatingSummary(product.getId());
            product.setAverageRating(ratingSummary.getAverageRating());
            product.setReviewCount(ratingSummary.getTotalReviews());
        } catch (Exception e) {
            // Review-service'den veri çekilemezse, mevcut değerleri koru veya null bırak
            if (product.getAverageRating() == null) {
                product.setAverageRating(BigDecimal.ZERO);
            }
            if (product.getReviewCount() == null) {
                product.setReviewCount(0);
            }
        }
        return product;
    }

    /**
     * Yeni ürün oluştur
     */
    @CacheEvict(value = "products", allEntries = true)  // Tüm product cache'lerini temizle
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    /**
     * Ürün güncelle
     */
    @CacheEvict(value = "products", key = "#productId.toString()")
    public Product updateProduct(UUID productId, Product productDetails) {
        Product product = getProductById(productId);
        
        // Güncelleme
        if (productDetails.getName() != null) {
            product.setName(productDetails.getName());
        }
        if (productDetails.getDescription() != null) {
            product.setDescription(productDetails.getDescription());
        }
        if (productDetails.getPrice() != null) {
            product.setPrice(productDetails.getPrice());
        }
        if (productDetails.getCategory() != null) {
            product.setCategory(productDetails.getCategory());
        }
        if (productDetails.getStockQuantity() != null) {
            product.setStockQuantity(productDetails.getStockQuantity());
        }
        if (productDetails.getSku() != null) {
            product.setSku(productDetails.getSku());
        }
        if (productDetails.getBrand() != null) {
            product.setBrand(productDetails.getBrand());
        }
        if (productDetails.getImageUrl() != null) {
            product.setImageUrl(productDetails.getImageUrl());
        }
        if (productDetails.getIsActive() != null) {
            product.setIsActive(productDetails.getIsActive());
        }
        
        return productRepository.save(product);
    }

    /**
     * Ürün sil
     */
    @CacheEvict(value = "products", key = "#productId.toString()")
    public void deleteProductById(UUID productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product", "id", productId);
        }
        productRepository.deleteById(productId);
    }
}

