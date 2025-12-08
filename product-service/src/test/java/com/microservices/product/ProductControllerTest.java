package com.microservices.product;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.product.Controller.ProductController;
import com.microservices.product.Exception.ResourceNotFoundException;
import com.microservices.product.Model.Product;
import com.microservices.product.service.ProductService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * ProductController için Integration Test
 * @WebMvcTest: Sadece web katmanını test eder, Controller'lar için
 * MockMvc: HTTP isteklerini simüle eder
 */
@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;  // HTTP isteklerini simüle eder

    @MockBean
    private ProductService productService;  // ProductService mock'lanıyor

    @Autowired
    private ObjectMapper objectMapper;  // JSON dönüşümleri için

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setId(UUID.randomUUID());
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
    void testCreateProduct() throws Exception {
        // Given: Mock service davranışı
        when(productService.createProduct(any(Product.class))).thenReturn(testProduct);

        // When & Then: POST isteği gönderiliyor
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testProduct)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("MacBook Pro 16 inch"))
                .andExpect(jsonPath("$.price").value(45000.00))
                .andExpect(jsonPath("$.category").value("Electronics"));

        // Verify: Service metodu çağrıldı
        verify(productService, times(1)).createProduct(any(Product.class));
    }

    @Test
    void testGetProductById() throws Exception {
        // Given: Mock service davranışı
        UUID productId = testProduct.getId();
        when(productService.getProductById(productId)).thenReturn(testProduct);

        // When & Then: GET isteği gönderiliyor
        mockMvc.perform(get("/products/{id}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productId.toString()))
                .andExpect(jsonPath("$.name").value("MacBook Pro 16 inch"))
                .andExpect(jsonPath("$.price").value(45000.00));

        verify(productService, times(1)).getProductById(productId);
    }

    @Test
    void testGetProductByIdNotFound() throws Exception {
        // Given: Ürün bulunamıyor
        UUID productId = UUID.randomUUID();
        when(productService.getProductById(productId))
                .thenThrow(new ResourceNotFoundException("Product", "id", productId));

        // When & Then: 404 dönmeli
        mockMvc.perform(get("/products/{id}", productId))
                .andExpect(status().isNotFound());

        verify(productService, times(1)).getProductById(productId);
    }

    @Test
    void testGetProductsByCategory() throws Exception {
        // Given: Mock service davranışı
        when(productService.getProductsByCategory("Electronics")).thenReturn(java.util.List.of(testProduct));

        // When & Then: GET isteği gönderiliyor
        mockMvc.perform(get("/products/category")
                .param("category", "Electronics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(productService, times(1)).getProductsByCategory("Electronics");
    }

    @Test
    void testGetActiveProducts() throws Exception {
        // Given: Mock service davranışı
        when(productService.getActiveProducts()).thenReturn(java.util.List.of(testProduct));

        // When & Then: GET isteği gönderiliyor
        mockMvc.perform(get("/products/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(productService, times(1)).getActiveProducts();
    }

    @Test
    void testUpdateProduct() throws Exception {
        // Given: Mock service davranışı
        UUID productId = testProduct.getId();
        Product updatedProduct = new Product();
        updatedProduct.setId(productId);
        updatedProduct.setName("MacBook Pro 16 inch M3 Max");
        updatedProduct.setPrice(new BigDecimal("50000.00"));
        updatedProduct.setCategory("Electronics");
        updatedProduct.setStockQuantity(5);

        when(productService.updateProduct(eq(productId), any(Product.class))).thenReturn(updatedProduct);

        // When & Then: PUT isteği gönderiliyor
        mockMvc.perform(put("/products/{id}", productId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("MacBook Pro 16 inch M3 Max"))
                .andExpect(jsonPath("$.price").value(50000.00));

        verify(productService, times(1)).updateProduct(eq(productId), any(Product.class));
    }

    @Test
    void testDeleteProduct() throws Exception {
        // Given: Mock service davranışı
        UUID productId = testProduct.getId();
        doNothing().when(productService).deleteProductById(productId);

        // When & Then: DELETE isteği gönderiliyor
        mockMvc.perform(delete("/products/{id}", productId))
                .andExpect(status().isNoContent());

        verify(productService, times(1)).deleteProductById(productId);
    }

    @Test
    void testDeleteProductNotFound() throws Exception {
        // Given: Ürün bulunamıyor
        UUID productId = UUID.randomUUID();
        doThrow(new ResourceNotFoundException("Product", "id", productId))
                .when(productService).deleteProductById(productId);

        // When & Then: 404 dönmeli
        mockMvc.perform(delete("/products/{id}", productId))
                .andExpect(status().isNotFound());

        verify(productService, times(1)).deleteProductById(productId);
    }
}

