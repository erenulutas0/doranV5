package com.microservices.entertainment.repository;

import com.microservices.entertainment.model.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {
    
    /**
     * Mekanın etkinliklerini getir
     */
    @Query("SELECT e FROM Event e WHERE e.venueId = :venueId AND e.deletedAt IS NULL ORDER BY e.startDateTime ASC")
    List<Event> findByVenueIdAndDeletedAtIsNullOrderByStartDateTime(@Param("venueId") UUID venueId);
    
    /**
     * Yaklaşan etkinlikleri getir
     */
    @Query("SELECT e FROM Event e WHERE e.status IN ('UPCOMING', 'ONGOING') AND e.deletedAt IS NULL AND e.startDateTime >= :now ORDER BY e.startDateTime ASC")
    Page<Event> findUpcomingEvents(@Param("now") LocalDateTime now, Pageable pageable);
    
    /**
     * Şehre göre yaklaşan etkinlikleri getir
     */
    @Query("SELECT e FROM Event e JOIN Venue v ON e.venueId = v.id WHERE e.status IN ('UPCOMING', 'ONGOING') AND e.deletedAt IS NULL AND v.city = :city AND e.startDateTime >= :now ORDER BY e.startDateTime ASC")
    Page<Event> findUpcomingEventsByCity(@Param("city") String city, @Param("now") LocalDateTime now, Pageable pageable);
    
    /**
     * Mekan tipine göre yaklaşan etkinlikleri getir
     */
    @Query("SELECT e FROM Event e JOIN Venue v ON e.venueId = v.id WHERE e.status IN ('UPCOMING', 'ONGOING') AND e.deletedAt IS NULL AND v.venueType = :venueType AND e.startDateTime >= :now ORDER BY e.startDateTime ASC")
    Page<Event> findUpcomingEventsByVenueType(@Param("venueType") com.microservices.entertainment.model.Venue.VenueType venueType, @Param("now") LocalDateTime now, Pageable pageable);
    
    /**
     * Arama sorgusu ile yaklaşan etkinlikleri getir
     */
    @Query("SELECT e FROM Event e WHERE e.status IN ('UPCOMING', 'ONGOING') AND e.deletedAt IS NULL AND e.startDateTime >= :now AND (LOWER(e.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(e.description) LIKE LOWER(CONCAT('%', :query, '%'))) ORDER BY e.startDateTime ASC")
    Page<Event> searchUpcomingEvents(@Param("query") String query, @Param("now") LocalDateTime now, Pageable pageable);
    
    /**
     * ID'ye göre etkinlik getir (silinmemiş)
     */
    Optional<Event> findByIdAndDeletedAtIsNull(UUID id);
}

