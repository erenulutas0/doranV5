package com.microservices.entertainment.service;

import com.microservices.entertainment.dto.VenueRequest;
import com.microservices.entertainment.dto.VenueResponse;
import com.microservices.entertainment.model.Venue;
import com.microservices.entertainment.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VenueService {
    
    private final VenueRepository repository;
    
    @Transactional
    public VenueResponse createVenue(VenueRequest request) {
        log.info("Creating venue: {}", request.getName());
        
        Venue venue = new Venue();
        venue.setName(request.getName());
        venue.setDescription(request.getDescription());
        venue.setCategory(request.getCategory());
        venue.setAddress(request.getAddress());
        venue.setCity(request.getCity());
        venue.setDistrict(request.getDistrict());
        venue.setPhone(request.getPhone());
        venue.setEmail(request.getEmail());
        venue.setWebsite(request.getWebsite());
        venue.setLatitude(request.getLatitude());
        venue.setLongitude(request.getLongitude());
        venue.setOpeningTime(request.getOpeningTime());
        venue.setClosingTime(request.getClosingTime());
        venue.setWorkingDays(request.getWorkingDays());
        venue.setCoverImageId(request.getCoverImageId());
        
        if (request.getVenueType() != null) {
            try {
                venue.setVenueType(Venue.VenueType.valueOf(request.getVenueType().toUpperCase()));
            } catch (IllegalArgumentException e) {
                venue.setVenueType(Venue.VenueType.OTHER);
            }
        } else {
            venue.setVenueType(Venue.VenueType.OTHER);
        }
        
        venue.setIsActive(true);
        
        venue = repository.save(venue);
        log.info("Venue created successfully: {}", venue.getId());
        
        return VenueResponse.fromEntity(venue);
    }
    
    @Transactional
    public VenueResponse updateVenue(UUID venueId, VenueRequest request) {
        log.info("Updating venue: {}", venueId);
        
        Venue venue = repository.findByIdAndDeletedAtIsNull(venueId)
                .orElseThrow(() -> new RuntimeException("Venue not found"));
        
        venue.setName(request.getName());
        venue.setDescription(request.getDescription());
        venue.setCategory(request.getCategory());
        venue.setAddress(request.getAddress());
        venue.setCity(request.getCity());
        venue.setDistrict(request.getDistrict());
        venue.setPhone(request.getPhone());
        venue.setEmail(request.getEmail());
        venue.setWebsite(request.getWebsite());
        venue.setLatitude(request.getLatitude());
        venue.setLongitude(request.getLongitude());
        venue.setOpeningTime(request.getOpeningTime());
        venue.setClosingTime(request.getClosingTime());
        venue.setWorkingDays(request.getWorkingDays());
        venue.setCoverImageId(request.getCoverImageId());
        
        if (request.getVenueType() != null) {
            try {
                venue.setVenueType(Venue.VenueType.valueOf(request.getVenueType().toUpperCase()));
            } catch (IllegalArgumentException e) {
                log.warn("Invalid venue type: {}", request.getVenueType());
            }
        }
        
        venue = repository.save(venue);
        log.info("Venue updated successfully: {}", venueId);
        
        return VenueResponse.fromEntity(venue);
    }
    
    @Transactional
    public void deleteVenue(UUID venueId) {
        log.info("Deleting venue: {}", venueId);
        
        Venue venue = repository.findByIdAndDeletedAtIsNull(venueId)
                .orElseThrow(() -> new RuntimeException("Venue not found"));
        
        venue.setIsActive(false);
        venue.setDeletedAt(LocalDateTime.now());
        
        repository.save(venue);
        log.info("Venue deleted successfully: {}", venueId);
    }
    
    @Transactional(readOnly = true)
    public Page<VenueResponse> getActiveVenues(Pageable pageable) {
        log.debug("Fetching active venues");
        return repository.findActiveVenues(pageable)
                .map(VenueResponse::fromEntity);
    }
    
    @Transactional(readOnly = true)
    public Page<VenueResponse> getActiveVenuesByType(Venue.VenueType venueType, Pageable pageable) {
        log.debug("Fetching active venues by type: {}", venueType);
        return repository.findActiveVenuesByType(venueType, pageable)
                .map(VenueResponse::fromEntity);
    }
    
    @Transactional(readOnly = true)
    public Page<VenueResponse> getActiveVenuesByCity(String city, Pageable pageable) {
        log.debug("Fetching active venues by city: {}", city);
        return repository.findActiveVenuesByCity(city, pageable)
                .map(VenueResponse::fromEntity);
    }
    
    @Transactional(readOnly = true)
    public Page<VenueResponse> searchActiveVenues(String query, Pageable pageable) {
        log.debug("Searching active venues with query: {}", query);
        return repository.searchActiveVenues(query, pageable)
                .map(VenueResponse::fromEntity);
    }
    
    @Transactional(readOnly = true)
    public List<VenueResponse> getNearbyVenues(BigDecimal latitude, BigDecimal longitude, double radiusKm) {
        log.debug("Fetching nearby venues - Lat: {}, Lng: {}, Radius: {} km", latitude, longitude, radiusKm);
        return repository.findNearbyVenues(latitude, longitude, radiusKm)
                .stream()
                .map(VenueResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public VenueResponse getVenueById(UUID venueId) {
        log.debug("Fetching venue: {}", venueId);
        
        Venue venue = repository.findByIdAndDeletedAtIsNull(venueId)
                .orElseThrow(() -> new RuntimeException("Venue not found"));
        
        return VenueResponse.fromEntity(venue);
    }
}

