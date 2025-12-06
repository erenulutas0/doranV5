package com.microservices.entertainment.integration;

import com.microservices.entertainment.dto.EventRequest;
import com.microservices.entertainment.dto.EventResponse;
import com.microservices.entertainment.dto.VenueRequest;
import com.microservices.entertainment.dto.VenueResponse;
import com.microservices.entertainment.model.Event;
import com.microservices.entertainment.model.Venue;
import com.microservices.entertainment.repository.EventRepository;
import com.microservices.entertainment.repository.VenueRepository;
import com.microservices.entertainment.service.EventService;
import com.microservices.entertainment.service.VenueService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class EntertainmentIntegrationTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");
    
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.add("spring.jpa.properties.hibernate.dialect", () -> "org.hibernate.dialect.PostgreSQLDialect");
    }
    
    @Autowired
    private VenueService venueService;
    
    @Autowired
    private EventService eventService;
    
    @Autowired
    private VenueRepository venueRepository;
    
    @Autowired
    private EventRepository eventRepository;
    
    private UUID testVenueId;
    
    @BeforeEach
    void setUp() {
        venueRepository.deleteAll();
        eventRepository.deleteAll();
    }
    
    @Test
    void shouldCreateVenueAndEvent() {
        // Given
        VenueRequest venueRequest = new VenueRequest();
        venueRequest.setName("Integration Test Venue");
        venueRequest.setDescription("Integration test description");
        venueRequest.setVenueType("CAFE");
        venueRequest.setAddress("123 Test Street");
        venueRequest.setCity("Istanbul");
        
        // When
        VenueResponse venue = venueService.createVenue(venueRequest);
        testVenueId = venue.getId();
        
        EventRequest eventRequest = new EventRequest();
        eventRequest.setVenueId(testVenueId);
        eventRequest.setName("Integration Test Event");
        eventRequest.setDescription("Integration test event description");
        eventRequest.setStartDateTime(LocalDateTime.now().plusDays(1));
        eventRequest.setEndDateTime(LocalDateTime.now().plusDays(1).plusHours(3));
        
        EventResponse event = eventService.createEvent(eventRequest);
        
        // Then
        assertThat(venue.getId()).isNotNull();
        assertThat(venue.getName()).isEqualTo("Integration Test Venue");
        
        assertThat(event.getId()).isNotNull();
        assertThat(event.getVenueId()).isEqualTo(testVenueId);
        assertThat(event.getName()).isEqualTo("Integration Test Event");
    }
    
    @Test
    void shouldGetVenueEvents() {
        // Given
        VenueRequest venueRequest = new VenueRequest();
        venueRequest.setName("Test Venue");
        venueRequest.setVenueType("CLUB");
        venueRequest.setAddress("123 Street");
        venueRequest.setCity("Istanbul");
        VenueResponse venue = venueService.createVenue(venueRequest);
        
        EventRequest event1 = new EventRequest();
        event1.setVenueId(venue.getId());
        event1.setName("Event 1");
        event1.setStartDateTime(LocalDateTime.now().plusDays(1));
        eventService.createEvent(event1);
        
        EventRequest event2 = new EventRequest();
        event2.setVenueId(venue.getId());
        event2.setName("Event 2");
        event2.setStartDateTime(LocalDateTime.now().plusDays(2));
        eventService.createEvent(event2);
        
        // When
        List<EventResponse> events = eventService.getVenueEvents(venue.getId());
        
        // Then
        assertThat(events).hasSize(2);
        assertThat(events.get(0).getName()).isEqualTo("Event 1");
    }
    
    @Test
    void shouldFilterUpcomingEvents() {
        // Given
        VenueRequest venueRequest = new VenueRequest();
        venueRequest.setName("Test Venue");
        venueRequest.setVenueType("THEATER");
        venueRequest.setAddress("123 Street");
        venueRequest.setCity("Ankara");
        VenueResponse venue = venueService.createVenue(venueRequest);
        
        EventRequest upcomingEvent = new EventRequest();
        upcomingEvent.setVenueId(venue.getId());
        upcomingEvent.setName("Upcoming Event");
        upcomingEvent.setStartDateTime(LocalDateTime.now().plusDays(1));
        eventService.createEvent(upcomingEvent);
        
        EventRequest pastEvent = new EventRequest();
        pastEvent.setVenueId(venue.getId());
        pastEvent.setName("Past Event");
        pastEvent.setStartDateTime(LocalDateTime.now().minusDays(1));
        EventResponse past = eventService.createEvent(pastEvent);
        // Manually set as completed
        Event pastEntity = eventRepository.findById(past.getId()).orElseThrow();
        pastEntity.setStatus(Event.EventStatus.COMPLETED);
        eventRepository.save(pastEntity);
        
        // When
        Pageable pageable = PageRequest.of(0, 20);
        Page<EventResponse> upcomingEvents = eventService.getUpcomingEvents(pageable);
        
        // Then
        assertThat(upcomingEvents.getContent()).isNotEmpty();
        assertThat(upcomingEvents.getContent().stream()
                .anyMatch(e -> e.getName().equals("Upcoming Event"))).isTrue();
    }
    
    @Test
    void shouldSearchVenues() {
        // Given
        VenueRequest request1 = new VenueRequest();
        request1.setName("Java Cafe");
        request1.setVenueType("CAFE");
        request1.setAddress("123 Street");
        request1.setCity("Istanbul");
        venueService.createVenue(request1);
        
        VenueRequest request2 = new VenueRequest();
        request2.setName("Python Bar");
        request2.setVenueType("BAR");
        request2.setAddress("456 Street");
        request2.setCity("Istanbul");
        venueService.createVenue(request2);
        
        // When
        Pageable pageable = PageRequest.of(0, 20);
        Page<VenueResponse> javaVenues = venueService.searchActiveVenues("Java", pageable);
        
        // Then
        assertThat(javaVenues.getContent()).isNotEmpty();
        assertThat(javaVenues.getContent().stream()
                .anyMatch(v -> v.getName().contains("Java"))).isTrue();
    }
}

