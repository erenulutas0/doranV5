package com.microservices.product.Repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.microservices.product.Model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    
    /**
     * Kategoriye göre ürünleri bul
     */
    List<Product> findByCategory(String category);
    
    /**
     * Aktif ürünleri bul
     */
    List<Product> findByIsActiveTrue();
    
    /**
     * Markaya göre ürünleri bul
     */
    List<Product> findByBrand(String brand);
}

