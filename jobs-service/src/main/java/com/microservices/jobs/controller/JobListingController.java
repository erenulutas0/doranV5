package com.microservices.jobs.controller;

import com.microservices.jobs.dto.JobListingRequest;
import com.microservices.jobs.dto.JobListingResponse;
import com.microservices.jobs.model.JobListing;
import com.microservices.jobs.service.JobListingService;
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
@RequestMapping("/jobs")
@RequiredArgsConstructor
@Slf4j
public class JobListingController {
    
    private final JobListingService service;
    
    /**
     * Yeni iş ilanı oluştur
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public JobListingResponse createJobListing(
            @RequestHeader("X-User-Id") UUID ownerId,
            @RequestHeader(value = "X-Owner-Type", defaultValue = "USER") String ownerTypeStr,
            @Valid @RequestBody JobListingRequest request) {
        log.info("POST /api/jobs - Owner: {}, Type: {}", ownerId, ownerTypeStr);
        
        JobListing.OwnerType ownerType;
        try {
            ownerType = JobListing.OwnerType.valueOf(ownerTypeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            ownerType = JobListing.OwnerType.USER;
        }
        
        return service.createJobListing(ownerId, ownerType, request);
    }
    
    /**
     * İlanı güncelle
     */
    @PutMapping("/{jobId}")
    public JobListingResponse updateJobListing(
            @RequestHeader("X-User-Id") UUID ownerId,
            @PathVariable UUID jobId,
            @Valid @RequestBody JobListingRequest request) {
        log.info("PUT /api/jobs/{} - Owner: {}", jobId, ownerId);
        return service.updateJobListing(ownerId, jobId, request);
    }
    
    /**
     * İlanı sil
     */
    @DeleteMapping("/{jobId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteJobListing(
            @RequestHeader("X-User-Id") UUID ownerId,
            @PathVariable UUID jobId) {
        log.info("DELETE /api/jobs/{} - Owner: {}", jobId, ownerId);
        service.deleteJobListing(ownerId, jobId);
    }
    
    /**
     * Sahibinin ilanlarını getir
     */
    @GetMapping("/my-jobs")
    public List<JobListingResponse> getMyJobListings(@RequestHeader("X-User-Id") UUID ownerId) {
        log.info("GET /api/jobs/my-jobs - Owner: {}", ownerId);
        return service.getOwnerJobListings(ownerId);
    }
    
    /**
     * Yayında olan aktif ilanları getir (sayfalı)
     */
    @GetMapping("/published")
    public Page<JobListingResponse> getPublishedActiveJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "publishedAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {
        log.info("GET /api/jobs/published - Page: {}, Size: {}", page, size);
        
        Sort sort = sortDir.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        return service.getPublishedActiveJobs(pageable);
    }
    
    /**
     * Kategoriye göre yayında olan aktif ilanları getir
     */
    @GetMapping("/published/category/{category}")
    public Page<JobListingResponse> getPublishedActiveJobsByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("GET /api/jobs/published/category/{} - Page: {}, Size: {}", category, page, size);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("publishedAt").descending());
        return service.getPublishedActiveJobsByCategory(category, pageable);
    }
    
    /**
     * İş tipine göre yayında olan aktif ilanları getir
     */
    @GetMapping("/published/job-type/{jobType}")
    public Page<JobListingResponse> getPublishedActiveJobsByJobType(
            @PathVariable String jobType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("GET /api/jobs/published/job-type/{} - Page: {}, Size: {}", jobType, page, size);
        
        JobListing.JobType type;
        try {
            type = JobListing.JobType.valueOf(jobType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid job type: " + jobType);
        }
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("publishedAt").descending());
        return service.getPublishedActiveJobsByJobType(type, pageable);
    }
    
    /**
     * Şehre göre yayında olan aktif ilanları getir
     */
    @GetMapping("/published/city/{city}")
    public Page<JobListingResponse> getPublishedActiveJobsByCity(
            @PathVariable String city,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("GET /api/jobs/published/city/{} - Page: {}, Size: {}", city, page, size);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("publishedAt").descending());
        return service.getPublishedActiveJobsByCity(city, pageable);
    }
    
    /**
     * Uzaktan çalışma ilanlarını getir
     */
    @GetMapping("/published/remote")
    public Page<JobListingResponse> getPublishedRemoteJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("GET /api/jobs/published/remote - Page: {}, Size: {}", page, size);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("publishedAt").descending());
        return service.getPublishedRemoteJobs(pageable);
    }
    
    /**
     * Arama sorgusu ile yayında olan aktif ilanları getir
     */
    @GetMapping("/published/search")
    public Page<JobListingResponse> searchPublishedActiveJobs(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("GET /api/jobs/published/search?q={} - Page: {}, Size: {}", q, page, size);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("publishedAt").descending());
        return service.searchPublishedActiveJobs(q, pageable);
    }
    
    /**
     * İlan detayını getir
     */
    @GetMapping("/{jobId}")
    public JobListingResponse getJobListingById(@PathVariable UUID jobId) {
        log.info("GET /api/jobs/{}", jobId);
        return service.getJobListingById(jobId);
    }
}

