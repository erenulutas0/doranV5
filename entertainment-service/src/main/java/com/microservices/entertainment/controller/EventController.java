package com.microservices.entertainment.controller;

import com.microservices.entertainment.dto.EventRequest;
import com.microservices.entertainment.dto.EventResponse;
import com.microservices.entertainment.model.Venue;
import com.microservices.entertainment.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Slf4j
public class EventController {
    
    private final EventService service;
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventResponse createEvent(@Valid @RequestBody EventRequest request) {
        log.info("POST /api/events");
        return service.createEvent(request);
    }
    
    @PutMapping("/{eventId}")
    public EventResponse updateEvent(@PathVariable UUID eventId, @Valid @RequestBody EventRequest request) {
        log.info("PUT /api/events/{}", eventId);
        return service.updateEvent(eventId, request);
    }
    
    @DeleteMapping("/{eventId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEvent(@PathVariable UUID eventId) {
        log.info("DELETE /api/events/{}", eventId);
        service.deleteEvent(eventId);
    }
    
    @GetMapping("/venue/{venueId}")
    public List<EventResponse> getVenueEvents(@PathVariable UUID venueId) {
        log.info("GET /api/events/venue/{}", venueId);
        return service.getVenueEvents(venueId);
    }
    
    @GetMapping("/upcoming")
    public Page<EventResponse> getUpcomingEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("GET /api/events/upcoming - Page: {}, Size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by("startDateTime").ascending());
        return service.getUpcomingEvents(pageable);
    }
    
    @GetMapping("/upcoming/city/{city}")
    public Page<EventResponse> getUpcomingEventsByCity(
            @PathVariable String city,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("GET /api/events/upcoming/city/{} - Page: {}, Size: {}", city, page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by("startDateTime").ascending());
        return service.getUpcomingEventsByCity(city, pageable);
    }
    
    @GetMapping("/upcoming/venue-type/{venueType}")
    public Page<EventResponse> getUpcomingEventsByVenueType(
            @PathVariable String venueType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("GET /api/events/upcoming/venue-type/{} - Page: {}, Size: {}", venueType, page, size);
        Venue.VenueType type;
        try {
            type = Venue.VenueType.valueOf(venueType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid venue type: " + venueType);
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by("startDateTime").ascending());
        return service.getUpcomingEventsByVenueType(type, pageable);
    }
    
    @GetMapping("/upcoming/search")
    public Page<EventResponse> searchUpcomingEvents(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("GET /api/events/upcoming/search?q={} - Page: {}, Size: {}", q, page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by("startDateTime").ascending());
        return service.searchUpcomingEvents(q, pageable);
    }
    
    @GetMapping("/{eventId}")
    public EventResponse getEventById(@PathVariable UUID eventId) {
        log.info("GET /api/events/{}", eventId);
        return service.getEventById(eventId);
    }
}

