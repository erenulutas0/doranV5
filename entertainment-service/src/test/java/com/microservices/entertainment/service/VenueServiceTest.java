package com.microservices.entertainment.service;

import com.microservices.entertainment.dto.VenueRequest;
import com.microservices.entertainment.dto.VenueResponse;
import com.microservices.entertainment.model.Venue;
import com.microservices.entertainment.repository.VenueRepository;
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
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VenueServiceTest {
    
    @Mock
    private VenueRepository repository;
    
    private VenueService service;
    
    private UUID testVenueId;
    private Venue testVenue;
    
    @BeforeEach
    void setUp() {
        service = new VenueService(repository);
        testVenueId = UUID.randomUUID();
        
        testVenue = new Venue();
        testVenue.setId(testVenueId);
        testVenue.setName("Test Venue");
        testVenue.setDescription("Test Description");
        testVenue.setVenueType(Venue.VenueType.CAFE);
        testVenue.setCity("Istanbul");
        testVenue.setIsActive(true);
        testVenue.setAverageRating(new BigDecimal("4.5"));
        testVenue.setReviewCount(10);
        testVenue.setCreatedAt(LocalDateTime.now());
        testVenue.setUpdatedAt(LocalDateTime.now());
    }
    
    @Test
    void shouldCreateVenue() {
        // Given
        VenueRequest request = new VenueRequest();
        request.setName("New Venue");
        request.setDescription("New Description");
        request.setVenueType("CAFE");
        request.setAddress("123 Test Street");
        request.setCity("Ankara");
        
        when(repository.save(any(Venue.class))).thenAnswer(invocation -> {
            Venue venue = invocation.getArgument(0);
            venue.setId(UUID.randomUUID());
            venue.setCreatedAt(LocalDateTime.now());
            venue.setUpdatedAt(LocalDateTime.now());
            venue.setIsActive(true);
            return venue;
        });
        
        // When
        VenueResponse response = service.createVenue(request);
        
        // Then
        ArgumentCaptor<Venue> captor = ArgumentCaptor.forClass(Venue.class);
        verify(repository).save(captor.capture());
        
        Venue savedVenue = captor.getValue();
        assertThat(savedVenue.getName()).isEqualTo("New Venue");
        assertThat(savedVenue.getVenueType()).isEqualTo(Venue.VenueType.CAFE);
        assertThat(savedVenue.getCity()).isEqualTo("Ankara");
        assertThat(savedVenue.getIsActive()).isTrue();
        
        assertThat(response).isNotNull();
    }
    
    @Test
    void shouldUpdateVenue() {
        // Given
        VenueRequest request = new VenueRequest();
        request.setName("Updated Venue");
        request.setVenueType("BAR");
        request.setCity("Izmir");
        
        when(repository.findByIdAndDeletedAtIsNull(testVenueId)).thenReturn(Optional.of(testVenue));
        when(repository.save(any(Venue.class))).thenReturn(testVenue);
        
        // When
        VenueResponse response = service.updateVenue(testVenueId, request);
        
        // Then
        ArgumentCaptor<Venue> captor = ArgumentCaptor.forClass(Venue.class);
        verify(repository).save(captor.capture());
        
        Venue updatedVenue = captor.getValue();
        assertThat(updatedVenue.getName()).isEqualTo("Updated Venue");
        assertThat(updatedVenue.getVenueType()).isEqualTo(Venue.VenueType.BAR);
    }
    
    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentVenue() {
        // Given
        VenueRequest request = new VenueRequest();
        request.setName("Updated Venue");
        
        when(repository.findByIdAndDeletedAtIsNull(testVenueId)).thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> service.updateVenue(testVenueId, request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("not found");
    }
    
    @Test
    void shouldDeleteVenue() {
        // Given
        when(repository.findByIdAndDeletedAtIsNull(testVenueId)).thenReturn(Optional.of(testVenue));
        when(repository.save(any(Venue.class))).thenReturn(testVenue);
        
        // When
        service.deleteVenue(testVenueId);
        
        // Then
        ArgumentCaptor<Venue> captor = ArgumentCaptor.forClass(Venue.class);
        verify(repository).save(captor.capture());
        
        Venue deletedVenue = captor.getValue();
        assertThat(deletedVenue.getIsActive()).isFalse();
        assertThat(deletedVenue.getDeletedAt()).isNotNull();
    }
    
    @Test
    void shouldGetActiveVenues() {
        // Given
        Pageable pageable = PageRequest.of(0, 20);
        Page<Venue> venuePage = new PageImpl<>(Arrays.asList(testVenue), pageable, 1);
        
        when(repository.findActiveVenues(pageable)).thenReturn(venuePage);
        
        // When
        Page<VenueResponse> responses = service.getActiveVenues(pageable);
        
        // Then
        assertThat(responses).isNotNull();
        assertThat(responses.getContent()).hasSize(1);
    }
}

