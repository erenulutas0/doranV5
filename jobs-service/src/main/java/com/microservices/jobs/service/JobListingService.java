package com.microservices.jobs.service;

import com.microservices.jobs.dto.JobListingRequest;
import com.microservices.jobs.dto.JobListingResponse;
import com.microservices.jobs.model.JobListing;
import com.microservices.jobs.repository.JobListingRepository;
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
public class JobListingService {
    
    private final JobListingRepository repository;
    
    /**
     * Yeni iş ilanı oluştur
     */
    @Transactional
    public JobListingResponse createJobListing(UUID ownerId, JobListing.OwnerType ownerType, JobListingRequest request) {
        log.info("Creating job listing for owner: {} (type: {})", ownerId, ownerType);
        
        JobListing job = new JobListing();
        job.setOwnerId(ownerId);
        job.setOwnerType(ownerType);
        job.setTitle(request.getTitle());
        job.setDescription(request.getDescription());
        job.setCategory(request.getCategory());
        job.setSalaryMin(request.getSalaryMin());
        job.setSalaryMax(request.getSalaryMax());
        job.setSalaryCurrency(request.getSalaryCurrency());
        job.setLocation(request.getLocation());
        job.setCity(request.getCity());
        job.setIsRemote(request.getIsRemote() != null ? request.getIsRemote() : false);
        job.setApplicationDeadline(request.getApplicationDeadline());
        job.setRequiredSkills(request.getRequiredSkills());
        
        // Job Type
        if (request.getJobType() != null) {
            try {
                job.setJobType(JobListing.JobType.valueOf(request.getJobType().toUpperCase()));
            } catch (IllegalArgumentException e) {
                job.setJobType(JobListing.JobType.FULL_TIME);
            }
        } else {
            job.setJobType(JobListing.JobType.FULL_TIME);
        }
        
        // Experience Level
        if (request.getExperienceLevel() != null) {
            try {
                job.setExperienceLevel(JobListing.ExperienceLevel.valueOf(request.getExperienceLevel().toUpperCase()));
            } catch (IllegalArgumentException e) {
                log.warn("Invalid experience level: {}", request.getExperienceLevel());
            }
        }
        
        // Status
        if (request.getStatus() != null) {
            try {
                JobListing.JobStatus status = JobListing.JobStatus.valueOf(request.getStatus().toUpperCase());
                job.setStatus(status);
                if (status == JobListing.JobStatus.PUBLISHED && job.getPublishedAt() == null) {
                    job.setPublishedAt(LocalDateTime.now());
                }
            } catch (IllegalArgumentException e) {
                job.setStatus(JobListing.JobStatus.DRAFT);
            }
        } else {
            job.setStatus(JobListing.JobStatus.DRAFT);
        }
        
        job.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        
        job = repository.save(job);
        log.info("Job listing created successfully: {}", job.getId());
        
        return JobListingResponse.fromEntity(job);
    }
    
    /**
     * İlanı güncelle (sadece sahibi güncelleyebilir)
     */
    @Transactional
    public JobListingResponse updateJobListing(UUID ownerId, UUID jobId, JobListingRequest request) {
        log.info("Updating job listing {} for owner: {}", jobId, ownerId);
        
        JobListing job = repository.findByIdAndOwnerIdAndDeletedAtIsNull(jobId, ownerId)
                .orElseThrow(() -> new RuntimeException("Job listing not found or you don't have permission"));
        
        job.setTitle(request.getTitle());
        job.setDescription(request.getDescription());
        job.setCategory(request.getCategory());
        job.setSalaryMin(request.getSalaryMin());
        job.setSalaryMax(request.getSalaryMax());
        job.setSalaryCurrency(request.getSalaryCurrency());
        job.setLocation(request.getLocation());
        job.setCity(request.getCity());
        job.setIsRemote(request.getIsRemote());
        job.setApplicationDeadline(request.getApplicationDeadline());
        job.setRequiredSkills(request.getRequiredSkills());
        
        // Job Type
        if (request.getJobType() != null) {
            try {
                job.setJobType(JobListing.JobType.valueOf(request.getJobType().toUpperCase()));
            } catch (IllegalArgumentException e) {
                log.warn("Invalid job type: {}", request.getJobType());
            }
        }
        
        // Experience Level
        if (request.getExperienceLevel() != null) {
            try {
                job.setExperienceLevel(JobListing.ExperienceLevel.valueOf(request.getExperienceLevel().toUpperCase()));
            } catch (IllegalArgumentException e) {
                log.warn("Invalid experience level: {}", request.getExperienceLevel());
            }
        }
        
        // Status
        if (request.getStatus() != null) {
            try {
                JobListing.JobStatus newStatus = JobListing.JobStatus.valueOf(request.getStatus().toUpperCase());
                job.setStatus(newStatus);
                if (newStatus == JobListing.JobStatus.PUBLISHED && job.getPublishedAt() == null) {
                    job.setPublishedAt(LocalDateTime.now());
                }
            } catch (IllegalArgumentException e) {
                log.warn("Invalid status: {}", request.getStatus());
            }
        }
        
        if (request.getIsActive() != null) {
            job.setIsActive(request.getIsActive());
        }
        
        job = repository.save(job);
        log.info("Job listing updated successfully: {}", jobId);
        
        return JobListingResponse.fromEntity(job);
    }
    
    /**
     * İlanı sil (soft delete)
     */
    @Transactional
    public void deleteJobListing(UUID ownerId, UUID jobId) {
        log.info("Deleting job listing {} for owner: {}", jobId, ownerId);
        
        JobListing job = repository.findByIdAndOwnerIdAndDeletedAtIsNull(jobId, ownerId)
                .orElseThrow(() -> new RuntimeException("Job listing not found or you don't have permission"));
        
        job.setStatus(JobListing.JobStatus.DELETED);
        job.setIsActive(false);
        job.setDeletedAt(LocalDateTime.now());
        
        repository.save(job);
        log.info("Job listing deleted successfully: {}", jobId);
    }
    
    /**
     * Sahibinin ilanlarını getir
     */
    @Transactional(readOnly = true)
    public List<JobListingResponse> getOwnerJobListings(UUID ownerId) {
        log.debug("Fetching job listings for owner: {}", ownerId);
        return repository.findByOwnerIdAndDeletedAtIsNullOrderByCreatedAtDesc(ownerId)
                .stream()
                .map(JobListingResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * Yayında olan aktif ilanları sayfalı olarak getir
     */
    @Transactional(readOnly = true)
    public Page<JobListingResponse> getPublishedActiveJobs(Pageable pageable) {
        log.debug("Fetching published active jobs");
        LocalDateTime now = LocalDateTime.now();
        return repository.findPublishedActiveJobs(now, pageable)
                .map(JobListingResponse::fromEntity);
    }
    
    /**
     * Kategoriye göre yayında olan aktif ilanları getir
     */
    @Transactional(readOnly = true)
    public Page<JobListingResponse> getPublishedActiveJobsByCategory(String category, Pageable pageable) {
        log.debug("Fetching published active jobs by category: {}", category);
        LocalDateTime now = LocalDateTime.now();
        return repository.findPublishedActiveJobsByCategory(category, now, pageable)
                .map(JobListingResponse::fromEntity);
    }
    
    /**
     * İş tipine göre yayında olan aktif ilanları getir
     */
    @Transactional(readOnly = true)
    public Page<JobListingResponse> getPublishedActiveJobsByJobType(JobListing.JobType jobType, Pageable pageable) {
        log.debug("Fetching published active jobs by job type: {}", jobType);
        LocalDateTime now = LocalDateTime.now();
        return repository.findPublishedActiveJobsByJobType(jobType, now, pageable)
                .map(JobListingResponse::fromEntity);
    }
    
    /**
     * Şehre göre yayında olan aktif ilanları getir
     */
    @Transactional(readOnly = true)
    public Page<JobListingResponse> getPublishedActiveJobsByCity(String city, Pageable pageable) {
        log.debug("Fetching published active jobs by city: {}", city);
        LocalDateTime now = LocalDateTime.now();
        return repository.findPublishedActiveJobsByCity(city, now, pageable)
                .map(JobListingResponse::fromEntity);
    }
    
    /**
     * Uzaktan çalışma ilanlarını getir
     */
    @Transactional(readOnly = true)
    public Page<JobListingResponse> getPublishedRemoteJobs(Pageable pageable) {
        log.debug("Fetching published remote jobs");
        LocalDateTime now = LocalDateTime.now();
        return repository.findPublishedRemoteJobs(now, pageable)
                .map(JobListingResponse::fromEntity);
    }
    
    /**
     * Arama sorgusu ile yayında olan aktif ilanları getir
     */
    @Transactional(readOnly = true)
    public Page<JobListingResponse> searchPublishedActiveJobs(String query, Pageable pageable) {
        log.debug("Searching published active jobs with query: {}", query);
        LocalDateTime now = LocalDateTime.now();
        return repository.searchPublishedActiveJobs(query, now, pageable)
                .map(JobListingResponse::fromEntity);
    }
    
    /**
     * İlan detayını getir
     */
    @Transactional(readOnly = true)
    public JobListingResponse getJobListingById(UUID jobId) {
        log.debug("Fetching job listing: {}", jobId);
        
        JobListing job = repository.findByIdAndDeletedAtIsNull(jobId)
                .orElseThrow(() -> new RuntimeException("Job listing not found"));
        
        return JobListingResponse.fromEntity(job);
    }
    
    /**
     * Süresi dolmuş ilanları otomatik kapat (Scheduled job)
     */
    @Scheduled(cron = "0 0 * * * *") // Her saat başı çalışır
    @Transactional
    public void closeExpiredJobs() {
        log.info("Closing expired job listings...");
        LocalDateTime now = LocalDateTime.now();
        repository.closeExpiredJobs(now);
        log.info("Expired job listings closed");
    }
}

