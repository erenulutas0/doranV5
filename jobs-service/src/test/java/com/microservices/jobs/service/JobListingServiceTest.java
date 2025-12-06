package com.microservices.jobs.service;

import com.microservices.jobs.dto.JobListingRequest;
import com.microservices.jobs.dto.JobListingResponse;
import com.microservices.jobs.model.JobListing;
import com.microservices.jobs.repository.JobListingRepository;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobListingServiceTest {
    
    @Mock
    private JobListingRepository repository;
    
    private JobListingService service;
    
    private UUID testOwnerId;
    private UUID testJobId;
    private JobListing testJob;
    
    @BeforeEach
    void setUp() {
        service = new JobListingService(repository);
        testOwnerId = UUID.randomUUID();
        testJobId = UUID.randomUUID();
        
        testJob = new JobListing();
        testJob.setId(testJobId);
        testJob.setOwnerId(testOwnerId);
        testJob.setOwnerType(JobListing.OwnerType.USER);
        testJob.setTitle("Software Engineer");
        testJob.setDescription("We are looking for a software engineer");
        testJob.setCategory("IT");
        testJob.setJobType(JobListing.JobType.FULL_TIME);
        testJob.setSalaryMin(new BigDecimal("50000"));
        testJob.setSalaryMax(new BigDecimal("80000"));
        testJob.setSalaryCurrency("TRY");
        testJob.setCity("Istanbul");
        testJob.setIsRemote(false);
        testJob.setStatus(JobListing.JobStatus.PUBLISHED);
        testJob.setIsActive(true);
        testJob.setCreatedAt(LocalDateTime.now());
        testJob.setUpdatedAt(LocalDateTime.now());
        testJob.setPublishedAt(LocalDateTime.now());
    }
    
    @Test
    void shouldCreateJobListing() {
        // Given
        JobListingRequest request = new JobListingRequest();
        request.setTitle("Senior Developer");
        request.setDescription("Senior developer position");
        request.setCategory("IT");
        request.setJobType("FULL_TIME");
        request.setSalaryMin(new BigDecimal("60000"));
        request.setSalaryMax(new BigDecimal("90000"));
        request.setCity("Ankara");
        request.setIsRemote(true);
        
        when(repository.save(any(JobListing.class))).thenAnswer(invocation -> {
            JobListing job = invocation.getArgument(0);
            job.setId(UUID.randomUUID());
            job.setCreatedAt(LocalDateTime.now());
            job.setUpdatedAt(LocalDateTime.now());
            job.setStatus(JobListing.JobStatus.DRAFT);
            return job;
        });
        
        // When
        JobListingResponse response = service.createJobListing(testOwnerId, JobListing.OwnerType.USER, request);
        
        // Then
        ArgumentCaptor<JobListing> captor = ArgumentCaptor.forClass(JobListing.class);
        verify(repository).save(captor.capture());
        
        JobListing savedJob = captor.getValue();
        assertThat(savedJob.getOwnerId()).isEqualTo(testOwnerId);
        assertThat(savedJob.getOwnerType()).isEqualTo(JobListing.OwnerType.USER);
        assertThat(savedJob.getTitle()).isEqualTo("Senior Developer");
        assertThat(savedJob.getCategory()).isEqualTo("IT");
        assertThat(savedJob.getJobType()).isEqualTo(JobListing.JobType.FULL_TIME);
        assertThat(savedJob.getIsRemote()).isTrue();
        
        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("Senior Developer");
    }
    
    @Test
    void shouldCreateJobListingAsPublished() {
        // Given
        JobListingRequest request = new JobListingRequest();
        request.setTitle("Published Job");
        request.setDescription("Published job description");
        request.setCategory("Marketing");
        request.setJobType("PART_TIME");
        request.setStatus("PUBLISHED");
        
        when(repository.save(any(JobListing.class))).thenAnswer(invocation -> {
            JobListing job = invocation.getArgument(0);
            job.setId(UUID.randomUUID());
            job.setCreatedAt(LocalDateTime.now());
            job.setUpdatedAt(LocalDateTime.now());
            job.setPublishedAt(LocalDateTime.now());
            return job;
        });
        
        // When
        JobListingResponse response = service.createJobListing(testOwnerId, JobListing.OwnerType.SHOP, request);
        
        // Then
        ArgumentCaptor<JobListing> captor = ArgumentCaptor.forClass(JobListing.class);
        verify(repository).save(captor.capture());
        
        JobListing savedJob = captor.getValue();
        assertThat(savedJob.getStatus()).isEqualTo(JobListing.JobStatus.PUBLISHED);
        assertThat(savedJob.getPublishedAt()).isNotNull();
        assertThat(savedJob.getOwnerType()).isEqualTo(JobListing.OwnerType.SHOP);
    }
    
    @Test
    void shouldUpdateJobListing() {
        // Given
        JobListingRequest request = new JobListingRequest();
        request.setTitle("Updated Title");
        request.setDescription("Updated description");
        request.setCategory("Sales");
        request.setJobType("CONTRACT");
        request.setSalaryMin(new BigDecimal("70000"));
        
        when(repository.findByIdAndOwnerIdAndDeletedAtIsNull(testJobId, testOwnerId))
                .thenReturn(Optional.of(testJob));
        when(repository.save(any(JobListing.class))).thenReturn(testJob);
        
        // When
        JobListingResponse response = service.updateJobListing(testOwnerId, testJobId, request);
        
        // Then
        ArgumentCaptor<JobListing> captor = ArgumentCaptor.forClass(JobListing.class);
        verify(repository).save(captor.capture());
        
        JobListing updatedJob = captor.getValue();
        assertThat(updatedJob.getTitle()).isEqualTo("Updated Title");
        assertThat(updatedJob.getCategory()).isEqualTo("Sales");
        assertThat(updatedJob.getJobType()).isEqualTo(JobListing.JobType.CONTRACT);
    }
    
    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentJob() {
        // Given
        JobListingRequest request = new JobListingRequest();
        request.setTitle("Updated Title");
        
        when(repository.findByIdAndOwnerIdAndDeletedAtIsNull(testJobId, testOwnerId))
                .thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> service.updateJobListing(testOwnerId, testJobId, request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("not found");
    }
    
    @Test
    void shouldDeleteJobListing() {
        // Given
        when(repository.findByIdAndOwnerIdAndDeletedAtIsNull(testJobId, testOwnerId))
                .thenReturn(Optional.of(testJob));
        when(repository.save(any(JobListing.class))).thenReturn(testJob);
        
        // When
        service.deleteJobListing(testOwnerId, testJobId);
        
        // Then
        ArgumentCaptor<JobListing> captor = ArgumentCaptor.forClass(JobListing.class);
        verify(repository).save(captor.capture());
        
        JobListing deletedJob = captor.getValue();
        assertThat(deletedJob.getStatus()).isEqualTo(JobListing.JobStatus.DELETED);
        assertThat(deletedJob.getIsActive()).isFalse();
        assertThat(deletedJob.getDeletedAt()).isNotNull();
    }
    
    @Test
    void shouldGetOwnerJobListings() {
        // Given
        List<JobListing> jobs = Arrays.asList(testJob);
        when(repository.findByOwnerIdAndDeletedAtIsNullOrderByCreatedAtDesc(testOwnerId))
                .thenReturn(jobs);
        
        // When
        List<JobListingResponse> responses = service.getOwnerJobListings(testOwnerId);
        
        // Then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getTitle()).isEqualTo("Software Engineer");
    }
    
    @Test
    void shouldGetPublishedActiveJobs() {
        // Given
        Pageable pageable = PageRequest.of(0, 20);
        Page<JobListing> jobPage = new PageImpl<>(Arrays.asList(testJob), pageable, 1);
        
        when(repository.findPublishedActiveJobs(any(LocalDateTime.class), eq(pageable))).thenReturn(jobPage);
        
        // When
        Page<JobListingResponse> responses = service.getPublishedActiveJobs(pageable);
        
        // Then
        assertThat(responses).isNotNull();
        assertThat(responses.getContent()).hasSize(1);
    }
}

