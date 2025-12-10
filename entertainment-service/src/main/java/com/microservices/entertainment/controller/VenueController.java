package com.microservices.entertainment.controller;

import com.microservices.entertainment.dto.VenueRequest;
import com.microservices.entertainment.dto.VenueResponse;
import com.microservices.entertainment.model.Venue;
import com.microservices.entertainment.service.VenueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/venues")
@RequiredArgsConstructor
@Slf4j
public class VenueController {
    
    private final VenueService service;
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VenueResponse createVenue(@Valid @RequestBody VenueRequest request) {
        log.info("POST /api/venues");
        return service.createVenue(request);
    }
    
    @PutMapping("/{venueId}")
    public VenueResponse updateVenue(@PathVariable UUID venueId, @Valid @RequestBody VenueRequest request) {
        log.info("PUT /api/venues/{}", venueId);
        return service.updateVenue(venueId, request);
    }
    
    @DeleteMapping("/{venueId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteVenue(@PathVariable UUID venueId) {
        log.info("DELETE /api/venues/{}", venueId);
        service.deleteVenue(venueId);
    }
    
    @GetMapping("/active")
    public Page<VenueResponse> getActiveVenues(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("GET /api/venues/active - Page: {}, Size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by("averageRating").descending().and(Sort.by("createdAt").descending()));
        return service.getActiveVenues(pageable);
    }
    
    @GetMapping("/active/type/{venueType}")
    public Page<VenueResponse> getActiveVenuesByType(
            @PathVariable String venueType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("GET /api/venues/active/type/{} - Page: {}, Size: {}", venueType, page, size);
        Venue.VenueType type;
        try {
            type = Venue.VenueType.valueOf(venueType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid venue type: " + venueType);
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by("averageRating").descending().and(Sort.by("createdAt").descending()));
        return service.getActiveVenuesByType(type, pageable);
    }
    
    @GetMapping("/active/city/{city}")
    public Page<VenueResponse> getActiveVenuesByCity(
            @PathVariable String city,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("GET /api/venues/active/city/{} - Page: {}, Size: {}", city, page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by("averageRating").descending().and(Sort.by("createdAt").descending()));
        return service.getActiveVenuesByCity(city, pageable);
    }
    
    @GetMapping("/active/search")
    public Page<VenueResponse> searchActiveVenues(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("GET /api/venues/active/search?q={} - Page: {}, Size: {}", q, page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by("averageRating").descending().and(Sort.by("createdAt").descending()));
        return service.searchActiveVenues(q, pageable);
    }
    
    @GetMapping("/nearby")
    public List<VenueResponse> getNearbyVenues(
            @RequestParam BigDecimal latitude,
            @RequestParam BigDecimal longitude,
            @RequestParam(defaultValue = "5.0") double radiusKm) {
        log.info("GET /api/venues/nearby - Lat: {}, Lng: {}, Radius: {} km", latitude, longitude, radiusKm);
        return service.getNearbyVenues(latitude, longitude, radiusKm);
    }
    
    @GetMapping("/{venueId}")
    public VenueResponse getVenueById(@PathVariable UUID venueId) {
        log.info("GET /api/venues/{}", venueId);
        return service.getVenueById(venueId);
    }
}

