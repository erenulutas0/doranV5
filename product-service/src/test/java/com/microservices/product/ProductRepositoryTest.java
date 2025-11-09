package com.microservices.product;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.microservices.product.Model.Product;
import com.microservices.product.Repository.ProductRepository;

/**
 * ProductRepository için Integration Test
 * @DataJpaTest: JPA Repository'leri test eder, gerçek veritabanı işlemleri yapar
 */
@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        // Her test öncesi veritabanı temizlenir (@DataJpaTest sayesinde)
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
    void testSaveProduct() {
        // Given: testProduct hazır
        // When: Ürün kaydediliyor
        Product savedProduct = productRepository.save(testProduct);

        // Then: Ürün başarıyla kaydedildi
        assertNotNull(savedProduct.getId());
        assertEquals("MacBook Pro 16 inch", savedProduct.getName());
        assertEquals(new BigDecimal("45000.00"), savedProduct.getPrice());
        assertEquals("Electronics", savedProduct.getCategory());
    }

    @Test
    void testFindByCategory() {
        // Given: Farklı kategorilerde ürünler kaydediliyor
        productRepository.save(testProduct); // Electronics
        
        Product product2 = new Product();
        product2.setName("Nike Air Max 90");
        product2.setPrice(new BigDecimal("5000.00"));
        product2.setCategory("Clothing");
        product2.setStockQuantity(50);
        product2.setBrand("Nike");
        product2.setIsActive(true);
        productRepository.save(product2); // Clothing

        // When: Electronics kategorisindeki ürünler aranıyor
        List<Product> electronicsProducts = productRepository.findByCategory("Electronics");

        // Then: Sadece Electronics ürünleri bulundu
        assertEquals(1, electronicsProducts.size());
        assertEquals("Electronics", electronicsProducts.get(0).getCategory());
    }

    @Test
    void testFindByIsActiveTrue() {
        // Given: Aktif ve pasif ürünler kaydediliyor
        productRepository.save(testProduct); // Active
        
        Product inactiveProduct = new Product();
        inactiveProduct.setName("Old Product");
        inactiveProduct.setPrice(new BigDecimal("1000.00"));
        inactiveProduct.setCategory("Electronics");
        inactiveProduct.setStockQuantity(0);
        inactiveProduct.setIsActive(false);
        productRepository.save(inactiveProduct); // Inactive

        // When: Aktif ürünler aranıyor
        List<Product> activeProducts = productRepository.findByIsActiveTrue();

        // Then: Sadece aktif ürünler bulundu
        assertEquals(1, activeProducts.size());
        assertTrue(activeProducts.get(0).getIsActive());
    }

    @Test
    void testFindByBrand() {
        // Given: Farklı markalarda ürünler kaydediliyor
        productRepository.save(testProduct); // Apple
        
        Product product2 = new Product();
        product2.setName("Samsung Galaxy S24");
        product2.setPrice(new BigDecimal("30000.00"));
        product2.setCategory("Electronics");
        product2.setStockQuantity(15);
        product2.setBrand("Samsung");
        product2.setIsActive(true);
        productRepository.save(product2); // Samsung

        // When: Apple markalı ürünler aranıyor
        List<Product> appleProducts = productRepository.findByBrand("Apple");

        // Then: Sadece Apple ürünleri bulundu
        assertEquals(1, appleProducts.size());
        assertEquals("Apple", appleProducts.get(0).getBrand());
    }

    @Test
    void testFindById() {
        // Given: Bir ürün kaydediliyor
        Product savedProduct = productRepository.save(testProduct);
        UUID productId = savedProduct.getId();

        // When: ID ile ürün aranıyor
        var foundProduct = productRepository.findById(productId);

        // Then: Ürün bulundu
        assertTrue(foundProduct.isPresent());
        assertEquals(productId, foundProduct.get().getId());
    }

    @Test
    void testDeleteProduct() {
        // Given: Bir ürün kaydediliyor
        Product savedProduct = productRepository.save(testProduct);
        UUID productId = savedProduct.getId();

        // When: Ürün siliniyor
        productRepository.deleteById(productId);

        // Then: Ürün artık bulunamaz
        assertFalse(productRepository.existsById(productId));
    }

    @Test
    void testProductCreatedAtAndUpdatedAt() {
        // Given: testProduct hazır
        // When: Ürün kaydediliyor
        Product savedProduct = productRepository.save(testProduct);

        // Then: createdAt ve updatedAt otomatik oluşturuldu
        assertNotNull(savedProduct.getCreatedAt());
        assertNotNull(savedProduct.getUpdatedAt());
        assertEquals(savedProduct.getCreatedAt(), savedProduct.getUpdatedAt());
    }
}

