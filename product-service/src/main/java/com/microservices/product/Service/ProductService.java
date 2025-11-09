package com.microservices.product.Service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.microservices.product.Exception.ResourceNotFoundException;
import com.microservices.product.Model.Product;
import com.microservices.product.Repository.ProductRepository;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Tüm ürünleri getir
     */
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * ID'ye göre ürün getir
     */
    public Product getProductById(UUID productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
    }

    /**
     * Kategoriye göre ürünleri getir
     */
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    /**
     * Aktif ürünleri getir
     */
    public List<Product> getActiveProducts() {
        return productRepository.findByIsActiveTrue();
    }

    /**
     * Yeni ürün oluştur
     */
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    /**
     * Ürün güncelle
     */
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
    public void deleteProductById(UUID productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product", "id", productId);
        }
        productRepository.deleteById(productId);
    }
}

