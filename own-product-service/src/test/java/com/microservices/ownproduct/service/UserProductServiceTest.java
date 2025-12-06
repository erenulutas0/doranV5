package com.microservices.ownproduct.service;

import com.microservices.ownproduct.dto.UserProductRequest;
import com.microservices.ownproduct.dto.UserProductResponse;
import com.microservices.ownproduct.model.UserProduct;
import com.microservices.ownproduct.repository.UserProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserProductServiceTest {
    
    @Mock
    private UserProductRepository repository;
    
    private UserProductService service;
    
    private UUID testUserId;
    private UUID testProductId;
    private UserProduct testProduct;
    
    @BeforeEach
    void setUp() {
        service = new UserProductService(repository);
        testUserId = UUID.randomUUID();
        testProductId = UUID.randomUUID();
        
        testProduct = new UserProduct();
        testProduct.setId(testProductId);
        testProduct.setUserId(testUserId);
        testProduct.setName("Test Product");
        testProduct.setDescription("Test Description");
        testProduct.setPrice(new BigDecimal("100.00"));
        testProduct.setCategory("Electronics");
        testProduct.setStatus(UserProduct.ProductStatus.DRAFT);
        testProduct.setVisibility(UserProduct.Visibility.PUBLIC);
        testProduct.setCreatedAt(LocalDateTime.now());
        testProduct.setUpdatedAt(LocalDateTime.now());
    }
    
    @Test
    void shouldCreateProduct() {
        // Given
        UserProductRequest request = new UserProductRequest();
        request.setName("New Product");
        request.setDescription("New Description");
        request.setPrice(new BigDecimal("200.00"));
        request.setCategory("Clothing");
        request.setStatus("DRAFT");
        request.setVisibility("PUBLIC");
        
        when(repository.save(any(UserProduct.class))).thenAnswer(invocation -> {
            UserProduct product = invocation.getArgument(0);
            product.setId(UUID.randomUUID());
            product.setCreatedAt(LocalDateTime.now());
            product.setUpdatedAt(LocalDateTime.now());
            return product;
        });
        
        // When
        UserProductResponse response = service.createProduct(testUserId, request);
        
        // Then
        ArgumentCaptor<UserProduct> captor = ArgumentCaptor.forClass(UserProduct.class);
        verify(repository).save(captor.capture());
        
        UserProduct savedProduct = captor.getValue();
        assertThat(savedProduct.getUserId()).isEqualTo(testUserId);
        assertThat(savedProduct.getName()).isEqualTo("New Product");
        assertThat(savedProduct.getPrice()).isEqualTo(new BigDecimal("200.00"));
        assertThat(savedProduct.getCategory()).isEqualTo("Clothing");
        assertThat(savedProduct.getStatus()).isEqualTo(UserProduct.ProductStatus.DRAFT);
        assertThat(savedProduct.getVisibility()).isEqualTo(UserProduct.Visibility.PUBLIC);
        
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("New Product");
    }
    
    @Test
    void shouldCreateProductAsPublished() {
        // Given
        UserProductRequest request = new UserProductRequest();
        request.setName("Published Product");
        request.setPrice(new BigDecimal("150.00"));
        request.setCategory("Books");
        request.setStatus("PUBLISHED");
        
        when(repository.save(any(UserProduct.class))).thenAnswer(invocation -> {
            UserProduct product = invocation.getArgument(0);
            product.setId(UUID.randomUUID());
            product.setCreatedAt(LocalDateTime.now());
            product.setUpdatedAt(LocalDateTime.now());
            product.setPublishedAt(LocalDateTime.now());
            return product;
        });
        
        // When
        UserProductResponse response = service.createProduct(testUserId, request);
        
        // Then
        ArgumentCaptor<UserProduct> captor = ArgumentCaptor.forClass(UserProduct.class);
        verify(repository).save(captor.capture());
        
        UserProduct savedProduct = captor.getValue();
        assertThat(savedProduct.getStatus()).isEqualTo(UserProduct.ProductStatus.PUBLISHED);
        assertThat(savedProduct.getPublishedAt()).isNotNull();
    }
    
    @Test
    void shouldUpdateProduct() {
        // Given
        UserProductRequest request = new UserProductRequest();
        request.setName("Updated Product");
        request.setPrice(new BigDecimal("300.00"));
        request.setCategory("Electronics");
        
        when(repository.findByIdAndUserIdAndDeletedAtIsNull(testProductId, testUserId))
                .thenReturn(Optional.of(testProduct));
        when(repository.save(any(UserProduct.class))).thenReturn(testProduct);
        
        // When
        UserProductResponse response = service.updateProduct(testUserId, testProductId, request);
        
        // Then
        ArgumentCaptor<UserProduct> captor = ArgumentCaptor.forClass(UserProduct.class);
        verify(repository).save(captor.capture());
        
        UserProduct updatedProduct = captor.getValue();
        assertThat(updatedProduct.getName()).isEqualTo("Updated Product");
        assertThat(updatedProduct.getPrice()).isEqualTo(new BigDecimal("300.00"));
    }
    
    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentProduct() {
        // Given
        UserProductRequest request = new UserProductRequest();
        request.setName("Updated Product");
        
        when(repository.findByIdAndUserIdAndDeletedAtIsNull(testProductId, testUserId))
                .thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> service.updateProduct(testUserId, testProductId, request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("not found");
    }
    
    @Test
    void shouldDeleteProduct() {
        // Given
        when(repository.findByIdAndUserIdAndDeletedAtIsNull(testProductId, testUserId))
                .thenReturn(Optional.of(testProduct));
        when(repository.save(any(UserProduct.class))).thenReturn(testProduct);
        
        // When
        service.deleteProduct(testUserId, testProductId);
        
        // Then
        ArgumentCaptor<UserProduct> captor = ArgumentCaptor.forClass(UserProduct.class);
        verify(repository).save(captor.capture());
        
        UserProduct deletedProduct = captor.getValue();
        assertThat(deletedProduct.getStatus()).isEqualTo(UserProduct.ProductStatus.DELETED);
        assertThat(deletedProduct.getDeletedAt()).isNotNull();
    }
    
    @Test
    void shouldGetUserProducts() {
        // Given
        List<UserProduct> products = Arrays.asList(testProduct);
        when(repository.findByUserIdAndDeletedAtIsNullOrderByCreatedAtDesc(testUserId))
                .thenReturn(products);
        
        // When
        List<UserProductResponse> responses = service.getUserProducts(testUserId);
        
        // Then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getName()).isEqualTo("Test Product");
    }
    
    @Test
    void shouldGetPublishedProducts() {
        // Given
        Pageable pageable = PageRequest.of(0, 20);
        Page<UserProduct> productPage = new PageImpl<>(Arrays.asList(testProduct), pageable, 1);
        
        when(repository.findPublishedPublicProducts(pageable)).thenReturn(productPage);
        
        // When
        Page<UserProductResponse> responses = service.getPublishedProducts(pageable);
        
        // Then
        assertThat(responses).isNotNull();
        assertThat(responses.getContent()).hasSize(1);
    }
}

