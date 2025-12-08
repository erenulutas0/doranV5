package com.microservices.product;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.microservices.product.Exception.ResourceNotFoundException;
import com.microservices.product.Model.Product;
import com.microservices.product.Repository.ProductRepository;
import com.microservices.product.service.ProductService;

/**
 * ProductService için Unit Test
 * @DataJpaTest: Sadece JPA katmanını test eder, veritabanı işlemleri için
 */
@DataJpaTest
@Import(ProductService.class)  // ProductService'i test context'ine ekle
class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        // Her test öncesi çalışır
        testProduct = new Product();
        testProduct.setName("MacBook Pro 16 inch");
        testProduct.setDescription("Apple'ın en güçlü laptop'u");
        testProduct.setPrice(new BigDecimal("45000.00"));
        testProduct.setCategory("Electronics");
        testProduct.setStockQuantity(10);
        testProduct.setSku("MBP-16-M3-2024");
        testProduct.setBrand("Apple");
        testProduct.setImageUrl("https://example.com/images/macbook-pro.jpg");
        testProduct.setIsActive(true);
    }

    @Test
    void testCreateProduct() {
        // Given: testProduct hazır
        // When: Ürün oluşturuluyor
        Product createdProduct = productService.createProduct(testProduct);

        // Then: Ürün başarıyla oluşturuldu
        assertNotNull(createdProduct.getId());
        assertEquals("MacBook Pro 16 inch", createdProduct.getName());
        assertEquals(new BigDecimal("45000.00"), createdProduct.getPrice());
        assertEquals("Electronics", createdProduct.getCategory());
        assertEquals(10, createdProduct.getStockQuantity());
    }

    @Test
    void testGetAllProducts() {
        // Given: Birkaç ürün oluşturuluyor
        productService.createProduct(testProduct);
        
        Product product2 = new Product();
        product2.setName("iPhone 15 Pro");
        product2.setDescription("Apple'ın en yeni telefonu");
        product2.setPrice(new BigDecimal("35000.00"));
        product2.setCategory("Electronics");
        product2.setStockQuantity(20);
        product2.setSku("IPHONE-15-PRO");
        product2.setBrand("Apple");
        product2.setIsActive(true);
        productService.createProduct(product2);

        // When: Tüm ürünler getiriliyor
        List<Product> products = productService.getAllProducts();

        // Then: 2 ürün olmalı
        assertEquals(2, products.size());
    }

    @Test
    void testGetProductById() {
        // Given: Bir ürün oluşturuluyor
        Product createdProduct = productService.createProduct(testProduct);
        UUID productId = createdProduct.getId();

        // When: ID ile ürün getiriliyor
        Product foundProduct = productService.getProductById(productId);

        // Then: Doğru ürün bulundu
        assertNotNull(foundProduct);
        assertEquals(productId, foundProduct.getId());
        assertEquals("MacBook Pro 16 inch", foundProduct.getName());
    }

    @Test
    void testGetProductByIdNotFound() {
        // Given: Var olmayan bir ID
        UUID nonExistentId = UUID.randomUUID();

        // When & Then: Exception fırlatılmalı
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            productService.getProductById(nonExistentId);
        });
        assertTrue(exception.getMessage().contains("Product not found"));
    }

    @Test
    void testGetProductsByCategory() {
        // Given: Farklı kategorilerde ürünler oluşturuluyor
        productService.createProduct(testProduct); // Electronics
        
        Product product2 = new Product();
        product2.setName("Nike Air Max 90");
        product2.setPrice(new BigDecimal("5000.00"));
        product2.setCategory("Clothing");
        product2.setStockQuantity(50);
        product2.setBrand("Nike");
        product2.setIsActive(true);
        productService.createProduct(product2); // Clothing

        Product product3 = new Product();
        product3.setName("Samsung Galaxy S24");
        product3.setPrice(new BigDecimal("30000.00"));
        product3.setCategory("Electronics");
        product3.setStockQuantity(15);
        product3.setBrand("Samsung");
        product3.setIsActive(true);
        productService.createProduct(product3); // Electronics

        // When: Electronics kategorisindeki ürünler getiriliyor
        List<Product> electronicsProducts = productService.getProductsByCategory("Electronics");

        // Then: 2 Electronics ürünü olmalı
        assertEquals(2, electronicsProducts.size());
        assertTrue(electronicsProducts.stream().allMatch(p -> p.getCategory().equals("Electronics")));
    }

    @Test
    void testGetActiveProducts() {
        // Given: Aktif ve pasif ürünler oluşturuluyor
        productService.createProduct(testProduct); // Active
        
        Product inactiveProduct = new Product();
        inactiveProduct.setName("Old Product");
        inactiveProduct.setPrice(new BigDecimal("1000.00"));
        inactiveProduct.setCategory("Electronics");
        inactiveProduct.setStockQuantity(0);
        inactiveProduct.setIsActive(false);
        productService.createProduct(inactiveProduct); // Inactive

        // When: Aktif ürünler getiriliyor
        List<Product> activeProducts = productService.getActiveProducts();

        // Then: Sadece aktif ürünler olmalı
        assertEquals(1, activeProducts.size());
        assertTrue(activeProducts.stream().allMatch(Product::getIsActive));
    }

    @Test
    void testUpdateProduct() {
        // Given: Bir ürün oluşturuluyor
        Product createdProduct = productService.createProduct(testProduct);
        UUID productId = createdProduct.getId();

        // When: Ürün güncelleniyor
        Product updateData = new Product();
        updateData.setName("MacBook Pro 16 inch M3 Max");
        updateData.setPrice(new BigDecimal("50000.00"));
        updateData.setDescription("Updated description");
        updateData.setStockQuantity(5);

        Product updatedProduct = productService.updateProduct(productId, updateData);

        // Then: Ürün güncellendi
        assertEquals(productId, updatedProduct.getId());
        assertEquals("MacBook Pro 16 inch M3 Max", updatedProduct.getName());
        assertEquals(new BigDecimal("50000.00"), updatedProduct.getPrice());
        assertEquals("Updated description", updatedProduct.getDescription());
        assertEquals(5, updatedProduct.getStockQuantity());
        // Güncellenmeyen alanlar korunmalı
        assertEquals("Electronics", updatedProduct.getCategory());
        assertEquals("Apple", updatedProduct.getBrand());
    }

    @Test
    void testUpdateProductPartial() {
        // Given: Bir ürün oluşturuluyor
        Product createdProduct = productService.createProduct(testProduct);
        UUID productId = createdProduct.getId();

        // When: Sadece fiyat güncelleniyor
        Product updateData = new Product();
        updateData.setPrice(new BigDecimal("40000.00"));

        Product updatedProduct = productService.updateProduct(productId, updateData);

        // Then: Sadece fiyat güncellendi, diğer alanlar korundu
        assertEquals(new BigDecimal("40000.00"), updatedProduct.getPrice());
        assertEquals("MacBook Pro 16 inch", updatedProduct.getName()); // Değişmedi
        assertEquals("Electronics", updatedProduct.getCategory()); // Değişmedi
    }

    @Test
    void testDeleteProduct() {
        // Given: Bir ürün oluşturuluyor
        Product createdProduct = productService.createProduct(testProduct);
        UUID productId = createdProduct.getId();

        // When: Ürün siliniyor
        productService.deleteProductById(productId);

        // Then: Ürün artık bulunamaz
        assertFalse(productRepository.existsById(productId));
    }

    @Test
    void testDeleteProductNotFound() {
        // Given: Var olmayan bir ID
        UUID nonExistentId = UUID.randomUUID();

        // When & Then: Exception fırlatılmalı
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            productService.deleteProductById(nonExistentId);
        });
        assertTrue(exception.getMessage().contains("Product not found"));
    }
}

