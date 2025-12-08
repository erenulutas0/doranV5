package com.microservices.product.Controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.microservices.product.Model.Product;
import com.microservices.product.service.ProductService;

/**
 * Product Controller
 * Exception handling @ControllerAdvice tarafından yapılıyor
 * Controller'da try-catch yok - Clean Code!
 */
@RestController
@RequestMapping("/products")  // Gateway zaten /api/products/** alıyor, burada sadece /products
public class ProductController {
    private final ProductService productService;
    
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Tüm ürünleri getir
     * GET /products
     */
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    /**
     * Kategoriye göre ürünleri getir
     * GET /products/category?category=Electronics
     */
    @GetMapping("/category")
    public ResponseEntity<List<Product>> getProductsByCategory(@RequestParam("category") String category) {
        List<Product> products = productService.getProductsByCategory(category);
        return ResponseEntity.ok(products);
    }

    /**
     * Aktif ürünleri getir
     * GET /products/active
     */
    @GetMapping("/active")
    public ResponseEntity<List<Product>> getActiveProducts() {
        List<Product> products = productService.getActiveProducts();
        return ResponseEntity.ok(products);
    }

    /**
     * Featured ürünleri getir (en yüksek rating'e sahip aktif ürünler, maksimum 6)
     * GET /products/featured
     * NOT: Bu endpoint /products/{id} endpoint'inden ÖNCE tanımlanmalı
     * çünkü Spring path variable'ları sırayla eşleştirir
     */
    @GetMapping("/featured")
    public ResponseEntity<List<Product>> getFeaturedProducts() {
        List<Product> products = productService.getFeaturedProducts();
        return ResponseEntity.ok(products);
    }

    /**
     * ID'ye göre ürün getir
     * GET /products/{id}
     * NOT: Bu endpoint en sonda olmalı çünkü path variable tüm string'leri yakalar
     */
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable("id") UUID id) {
        Product product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }
    
    /**
     * Yeni ürün oluştur
     * POST /products
     */
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product createdProduct = productService.createProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    /**
     * Ürün güncelle
     * PUT /products/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable("id") UUID id, 
            @RequestBody Product product) {
        Product updatedProduct = productService.updateProduct(id, product);
        return ResponseEntity.ok(updatedProduct);
    }
    
    /**
     * Ürün sil
     * DELETE /products/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductById(@PathVariable("id") UUID id) {
        productService.deleteProductById(id);
        return ResponseEntity.noContent().build();
    }
}
