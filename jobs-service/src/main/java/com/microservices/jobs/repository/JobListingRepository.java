package com.microservices.jobs.repository;

import com.microservices.jobs.model.JobListing;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JobListingRepository extends JpaRepository<JobListing, UUID> {
    
    /**
     * Sahibinin tüm ilanlarını getir (silinmemiş)
     */
    List<JobListing> findByOwnerIdAndDeletedAtIsNullOrderByCreatedAtDesc(UUID ownerId);
    
    /**
     * Sahibinin ilanlarını sayfalı olarak getir
     */
    Page<JobListing> findByOwnerIdAndDeletedAtIsNullOrderByCreatedAtDesc(UUID ownerId, Pageable pageable);
    
    /**
     * Yayında olan aktif ilanları getir
     */
    @Query("SELECT j FROM JobListing j WHERE j.status = 'PUBLISHED' AND j.isActive = true AND j.deletedAt IS NULL AND (j.applicationDeadline IS NULL OR j.applicationDeadline > :now) ORDER BY j.publishedAt DESC")
    Page<JobListing> findPublishedActiveJobs(@Param("now") LocalDateTime now, Pageable pageable);
    
    /**
     * Kategoriye göre yayında olan aktif ilanları getir
     */
    @Query("SELECT j FROM JobListing j WHERE j.status = 'PUBLISHED' AND j.isActive = true AND j.category = :category AND j.deletedAt IS NULL AND (j.applicationDeadline IS NULL OR j.applicationDeadline > :now) ORDER BY j.publishedAt DESC")
    Page<JobListing> findPublishedActiveJobsByCategory(@Param("category") String category, @Param("now") LocalDateTime now, Pageable pageable);
    
    /**
     * İş tipine göre yayında olan aktif ilanları getir
     */
    @Query("SELECT j FROM JobListing j WHERE j.status = 'PUBLISHED' AND j.isActive = true AND j.jobType = :jobType AND j.deletedAt IS NULL AND (j.applicationDeadline IS NULL OR j.applicationDeadline > :now) ORDER BY j.publishedAt DESC")
    Page<JobListing> findPublishedActiveJobsByJobType(@Param("jobType") JobListing.JobType jobType, @Param("now") LocalDateTime now, Pageable pageable);
    
    /**
     * Şehre göre yayında olan aktif ilanları getir
     */
    @Query("SELECT j FROM JobListing j WHERE j.status = 'PUBLISHED' AND j.isActive = true AND j.city = :city AND j.deletedAt IS NULL AND (j.applicationDeadline IS NULL OR j.applicationDeadline > :now) ORDER BY j.publishedAt DESC")
    Page<JobListing> findPublishedActiveJobsByCity(@Param("city") String city, @Param("now") LocalDateTime now, Pageable pageable);
    
    /**
     * Uzaktan çalışma ilanlarını getir
     */
    @Query("SELECT j FROM JobListing j WHERE j.status = 'PUBLISHED' AND j.isActive = true AND j.isRemote = true AND j.deletedAt IS NULL AND (j.applicationDeadline IS NULL OR j.applicationDeadline > :now) ORDER BY j.publishedAt DESC")
    Page<JobListing> findPublishedRemoteJobs(@Param("now") LocalDateTime now, Pageable pageable);
    
    /**
     * Arama sorgusu ile yayında olan aktif ilanları getir
     */
    @Query("SELECT j FROM JobListing j WHERE j.status = 'PUBLISHED' AND j.isActive = true AND j.deletedAt IS NULL AND (j.applicationDeadline IS NULL OR j.applicationDeadline > :now) AND (LOWER(j.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(j.description) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(j.category) LIKE LOWER(CONCAT('%', :query, '%'))) ORDER BY j.publishedAt DESC")
    Page<JobListing> searchPublishedActiveJobs(@Param("query") String query, @Param("now") LocalDateTime now, Pageable pageable);
    
    /**
     * Sahibinin belirli bir ilanını getir
     */
    Optional<JobListing> findByIdAndOwnerIdAndDeletedAtIsNull(UUID id, UUID ownerId);
    
    /**
     * ID'ye göre ilan getir (silinmemiş)
     */
    Optional<JobListing> findByIdAndDeletedAtIsNull(UUID id);
    
    /**
     * Süresi dolmuş ilanları kapat
     */
    @Modifying
    @Query("UPDATE JobListing j SET j.status = 'CLOSED' WHERE j.status = 'PUBLISHED' AND j.applicationDeadline IS NOT NULL AND j.applicationDeadline <= :now")
    void closeExpiredJobs(@Param("now") LocalDateTime now);
}

