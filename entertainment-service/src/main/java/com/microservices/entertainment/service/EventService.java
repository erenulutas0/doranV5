package com.microservices.entertainment.service;

import com.microservices.entertainment.dto.EventRequest;
import com.microservices.entertainment.dto.EventResponse;
import com.microservices.entertainment.model.Event;
import com.microservices.entertainment.model.Venue;
import com.microservices.entertainment.repository.EventRepository;
import com.microservices.entertainment.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {
    
    private final EventRepository eventRepository;
    private final VenueRepository venueRepository;
    
    @Transactional
    public EventResponse createEvent(EventRequest request) {
        log.info("Creating event: {}", request.getName());
        
        // Venue kontrolü
        Venue venue = venueRepository.findByIdAndDeletedAtIsNull(request.getVenueId())
                .orElseThrow(() -> new RuntimeException("Venue not found"));
        
        Event event = new Event();
        event.setVenueId(request.getVenueId());
        event.setName(request.getName());
        event.setDescription(request.getDescription());
        event.setCategory(request.getCategory());
        event.setStartDateTime(request.getStartDateTime());
        event.setEndDateTime(request.getEndDateTime());
        event.setTicketPrice(request.getTicketPrice());
        event.setMaxCapacity(request.getMaxCapacity());
        event.setImageId(request.getImageId());
        event.setStatus(Event.EventStatus.UPCOMING);
        
        event = eventRepository.save(event);
        log.info("Event created successfully: {}", event.getId());
        
        EventResponse response = EventResponse.fromEntity(event);
        response.setVenueName(venue.getName());
        return response;
    }
    
    @Transactional
    public EventResponse updateEvent(UUID eventId, EventRequest request) {
        log.info("Updating event: {}", eventId);
        
        Event event = eventRepository.findByIdAndDeletedAtIsNull(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        
        if (request.getVenueId() != null && !request.getVenueId().equals(event.getVenueId())) {
            Venue venue = venueRepository.findByIdAndDeletedAtIsNull(request.getVenueId())
                    .orElseThrow(() -> new RuntimeException("Venue not found"));
            event.setVenueId(request.getVenueId());
        }
        
        event.setName(request.getName());
        event.setDescription(request.getDescription());
        event.setCategory(request.getCategory());
        event.setStartDateTime(request.getStartDateTime());
        event.setEndDateTime(request.getEndDateTime());
        event.setTicketPrice(request.getTicketPrice());
        event.setMaxCapacity(request.getMaxCapacity());
        event.setImageId(request.getImageId());
        
        event = eventRepository.save(event);
        log.info("Event updated successfully: {}", eventId);
        
        Venue venue = venueRepository.findById(event.getVenueId()).orElse(null);
        EventResponse response = EventResponse.fromEntity(event);
        if (venue != null) {
            response.setVenueName(venue.getName());
        }
        return response;
    }
    
    @Transactional
    public void deleteEvent(UUID eventId) {
        log.info("Deleting event: {}", eventId);
        
        Event event = eventRepository.findByIdAndDeletedAtIsNull(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        
        event.setStatus(Event.EventStatus.DELETED);
        event.setDeletedAt(LocalDateTime.now());
        
        eventRepository.save(event);
        log.info("Event deleted successfully: {}", eventId);
    }
    
    @Transactional(readOnly = true)
    public List<EventResponse> getVenueEvents(UUID venueId) {
        log.debug("Fetching events for venue: {}", venueId);
        return eventRepository.findByVenueIdAndDeletedAtIsNullOrderByStartDateTime(venueId)
                .stream()
                .map(e -> {
                    EventResponse response = EventResponse.fromEntity(e);
                    Venue venue = venueRepository.findById(venueId).orElse(null);
                    if (venue != null) {
                        response.setVenueName(venue.getName());
                    }
                    return response;
                })
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public Page<EventResponse> getUpcomingEvents(Pageable pageable) {
        log.debug("Fetching upcoming events");
        LocalDateTime now = LocalDateTime.now();
        return eventRepository.findUpcomingEvents(now, pageable)
                .map(e -> {
                    EventResponse response = EventResponse.fromEntity(e);
                    Venue venue = venueRepository.findById(e.getVenueId()).orElse(null);
                    if (venue != null) {
                        response.setVenueName(venue.getName());
                    }
                    return response;
                });
    }
    
    @Transactional(readOnly = true)
    public Page<EventResponse> getUpcomingEventsByCity(String city, Pageable pageable) {
        log.debug("Fetching upcoming events by city: {}", city);
        LocalDateTime now = LocalDateTime.now();
        return eventRepository.findUpcomingEventsByCity(city, now, pageable)
                .map(e -> {
                    EventResponse response = EventResponse.fromEntity(e);
                    Venue venue = venueRepository.findById(e.getVenueId()).orElse(null);
                    if (venue != null) {
                        response.setVenueName(venue.getName());
                    }
                    return response;
                });
    }
    
    @Transactional(readOnly = true)
    public Page<EventResponse> getUpcomingEventsByVenueType(Venue.VenueType venueType, Pageable pageable) {
        log.debug("Fetching upcoming events by venue type: {}", venueType);
        LocalDateTime now = LocalDateTime.now();
        return eventRepository.findUpcomingEventsByVenueType(venueType, now, pageable)
                .map(e -> {
                    EventResponse response = EventResponse.fromEntity(e);
                    Venue venue = venueRepository.findById(e.getVenueId()).orElse(null);
                    if (venue != null) {
                        response.setVenueName(venue.getName());
                    }
                    return response;
                });
    }
    
    @Transactional(readOnly = true)
    public Page<EventResponse> searchUpcomingEvents(String query, Pageable pageable) {
        log.debug("Searching upcoming events with query: {}", query);
        LocalDateTime now = LocalDateTime.now();
        return eventRepository.searchUpcomingEvents(query, now, pageable)
                .map(e -> {
                    EventResponse response = EventResponse.fromEntity(e);
                    Venue venue = venueRepository.findById(e.getVenueId()).orElse(null);
                    if (venue != null) {
                        response.setVenueName(venue.getName());
                    }
                    return response;
                });
    }
    
    @Transactional(readOnly = true)
    public EventResponse getEventById(UUID eventId) {
        log.debug("Fetching event: {}", eventId);
        
        Event event = eventRepository.findByIdAndDeletedAtIsNull(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        
        EventResponse response = EventResponse.fromEntity(event);
        Venue venue = venueRepository.findById(event.getVenueId()).orElse(null);
        if (venue != null) {
            response.setVenueName(venue.getName());
        }
        return response;
    }
    
    /**
     * Etkinlik durumlarını otomatik güncelle (Scheduled job)
     */
    @Scheduled(cron = "0 */30 * * * *") // Her 30 dakikada bir çalışır
    @Transactional
    public void updateEventStatuses() {
        log.info("Updating event statuses...");
        LocalDateTime now = LocalDateTime.now();
        
        // Devam eden etkinlikleri güncelle
        List<Event> ongoingEvents = eventRepository.findAll().stream()
                .filter(e -> e.getDeletedAt() == null)
                .filter(e -> e.getStatus() == Event.EventStatus.UPCOMING)
                .filter(e -> e.getStartDateTime() != null && e.getStartDateTime().isBefore(now))
                .filter(e -> e.getEndDateTime() == null || e.getEndDateTime().isAfter(now))
                .collect(Collectors.toList());
        
        for (Event event : ongoingEvents) {
            event.setStatus(Event.EventStatus.ONGOING);
            eventRepository.save(event);
        }
        
        // Tamamlanan etkinlikleri güncelle
        List<Event> completedEvents = eventRepository.findAll().stream()
                .filter(e -> e.getDeletedAt() == null)
                .filter(e -> e.getStatus() == Event.EventStatus.ONGOING)
                .filter(e -> e.getEndDateTime() != null && e.getEndDateTime().isBefore(now))
                .collect(Collectors.toList());
        
        for (Event event : completedEvents) {
            event.setStatus(Event.EventStatus.COMPLETED);
            eventRepository.save(event);
        }
        
        log.info("Event statuses updated. Ongoing: {}, Completed: {}", ongoingEvents.size(), completedEvents.size());
    }
}

