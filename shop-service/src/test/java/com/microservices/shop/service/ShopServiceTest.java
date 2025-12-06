package com.microservices.shop.service;

import com.microservices.shop.dto.ShopRequest;
import com.microservices.shop.dto.ShopResponse;
import com.microservices.shop.model.Shop;
import com.microservices.shop.repository.ShopRepository;
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
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShopServiceTest {
    
    @Mock
    private ShopRepository repository;
    
    private ShopService service;
    
    private UUID testOwnerId;
    private UUID testShopId;
    private Shop testShop;
    
    @BeforeEach
    void setUp() {
        service = new ShopService(repository);
        testOwnerId = UUID.randomUUID();
        testShopId = UUID.randomUUID();
        
        testShop = new Shop();
        testShop.setId(testShopId);
        testShop.setOwnerId(testOwnerId);
        testShop.setName("Test Shop");
        testShop.setDescription("Test Description");
        testShop.setCategory("Electronics");
        testShop.setAddress("123 Test Street");
        testShop.setCity("Istanbul");
        testShop.setIsActive(true);
        testShop.setAverageRating(new BigDecimal("4.5"));
        testShop.setReviewCount(10);
        testShop.setCreatedAt(LocalDateTime.now());
        testShop.setUpdatedAt(LocalDateTime.now());
    }
    
    @Test
    void shouldCreateShop() {
        // Given
        ShopRequest request = new ShopRequest();
        request.setName("New Shop");
        request.setDescription("New Description");
        request.setCategory("Clothing");
        request.setAddress("456 New Street");
        request.setCity("Ankara");
        request.setPhone("5551234567");
        request.setEmail("shop@example.com");
        
        when(repository.save(any(Shop.class))).thenAnswer(invocation -> {
            Shop shop = invocation.getArgument(0);
            shop.setId(UUID.randomUUID());
            shop.setCreatedAt(LocalDateTime.now());
            shop.setUpdatedAt(LocalDateTime.now());
            shop.setIsActive(true);
            return shop;
        });
        
        // When
        ShopResponse response = service.createShop(testOwnerId, request);
        
        // Then
        ArgumentCaptor<Shop> captor = ArgumentCaptor.forClass(Shop.class);
        verify(repository).save(captor.capture());
        
        Shop savedShop = captor.getValue();
        assertThat(savedShop.getOwnerId()).isEqualTo(testOwnerId);
        assertThat(savedShop.getName()).isEqualTo("New Shop");
        assertThat(savedShop.getCategory()).isEqualTo("Clothing");
        assertThat(savedShop.getCity()).isEqualTo("Ankara");
        assertThat(savedShop.getIsActive()).isTrue();
        
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("New Shop");
    }
    
    @Test
    void shouldUpdateShop() {
        // Given
        ShopRequest request = new ShopRequest();
        request.setName("Updated Shop");
        request.setCategory("Books");
        request.setAddress("789 Updated Street");
        request.setCity("Izmir");
        
        when(repository.findByIdAndOwnerIdAndDeletedAtIsNull(testShopId, testOwnerId))
                .thenReturn(Optional.of(testShop));
        when(repository.save(any(Shop.class))).thenReturn(testShop);
        
        // When
        ShopResponse response = service.updateShop(testOwnerId, testShopId, request);
        
        // Then
        ArgumentCaptor<Shop> captor = ArgumentCaptor.forClass(Shop.class);
        verify(repository).save(captor.capture());
        
        Shop updatedShop = captor.getValue();
        assertThat(updatedShop.getName()).isEqualTo("Updated Shop");
        assertThat(updatedShop.getCategory()).isEqualTo("Books");
        assertThat(updatedShop.getCity()).isEqualTo("Izmir");
    }
    
    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentShop() {
        // Given
        ShopRequest request = new ShopRequest();
        request.setName("Updated Shop");
        
        when(repository.findByIdAndOwnerIdAndDeletedAtIsNull(testShopId, testOwnerId))
                .thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> service.updateShop(testOwnerId, testShopId, request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("not found");
    }
    
    @Test
    void shouldDeleteShop() {
        // Given
        when(repository.findByIdAndOwnerIdAndDeletedAtIsNull(testShopId, testOwnerId))
                .thenReturn(Optional.of(testShop));
        when(repository.save(any(Shop.class))).thenReturn(testShop);
        
        // When
        service.deleteShop(testOwnerId, testShopId);
        
        // Then
        ArgumentCaptor<Shop> captor = ArgumentCaptor.forClass(Shop.class);
        verify(repository).save(captor.capture());
        
        Shop deletedShop = captor.getValue();
        assertThat(deletedShop.getIsActive()).isFalse();
        assertThat(deletedShop.getDeletedAt()).isNotNull();
    }
    
    @Test
    void shouldGetOwnerShops() {
        // Given
        List<Shop> shops = Arrays.asList(testShop);
        when(repository.findByOwnerIdAndDeletedAtIsNullOrderByCreatedAtDesc(testOwnerId))
                .thenReturn(shops);
        
        // When
        List<ShopResponse> responses = service.getOwnerShops(testOwnerId);
        
        // Then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getName()).isEqualTo("Test Shop");
    }
    
    @Test
    void shouldGetActiveShops() {
        // Given
        Pageable pageable = PageRequest.of(0, 20);
        Page<Shop> shopPage = new PageImpl<>(Arrays.asList(testShop), pageable, 1);
        
        when(repository.findActiveShops(pageable)).thenReturn(shopPage);
        
        // When
        Page<ShopResponse> responses = service.getActiveShops(pageable);
        
        // Then
        assertThat(responses).isNotNull();
        assertThat(responses.getContent()).hasSize(1);
    }
}

