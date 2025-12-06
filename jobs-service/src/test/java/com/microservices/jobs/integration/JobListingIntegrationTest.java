package com.microservices.jobs.integration;

import com.microservices.jobs.dto.JobListingRequest;
import com.microservices.jobs.dto.JobListingResponse;
import com.microservices.jobs.model.JobListing;
import com.microservices.jobs.repository.JobListingRepository;
import com.microservices.jobs.service.JobListingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
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
class JobListingIntegrationTest {
    
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
    }
    
    @Autowired
    private JobListingService service;
    
    @Autowired
    private JobListingRepository repository;
    
    private UUID testOwnerId;
    
    @BeforeEach
    void setUp() {
        testOwnerId = UUID.randomUUID();
        repository.deleteAll();
    }
    
    @Test
    void shouldCreateAndRetrieveJobListing() {
        // Given
        JobListingRequest request = new JobListingRequest();
        request.setTitle("Integration Test Job");
        request.setDescription("Integration test description");
        request.setCategory("IT");
        request.setJobType("FULL_TIME");
        request.setSalaryMin(new BigDecimal("50000"));
        request.setSalaryMax(new BigDecimal("80000"));
        request.setCity("Istanbul");
        request.setIsRemote(false);
        
        // When
        JobListingResponse created = service.createJobListing(testOwnerId, JobListing.OwnerType.USER, request);
        
        // Then
        assertThat(created.getId()).isNotNull();
        assertThat(created.getTitle()).isEqualTo("Integration Test Job");
        assertThat(created.getCategory()).isEqualTo("IT");
        assertThat(created.getOwnerId()).isEqualTo(testOwnerId);
        
        // Verify retrieval
        JobListingResponse retrieved = service.getJobListingById(created.getId());
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getTitle()).isEqualTo("Integration Test Job");
    }
    
    @Test
    void shouldUpdateJobListing() {
        // Given
        JobListingRequest createRequest = new JobListingRequest();
        createRequest.setTitle("Original Title");
        createRequest.setDescription("Original description");
        createRequest.setCategory("IT");
        createRequest.setJobType("FULL_TIME");
        
        JobListingResponse created = service.createJobListing(testOwnerId, JobListing.OwnerType.USER, createRequest);
        
        // When
        JobListingRequest updateRequest = new JobListingRequest();
        updateRequest.setTitle("Updated Title");
        updateRequest.setDescription("Updated description");
        updateRequest.setCategory("Sales");
        updateRequest.setJobType("PART_TIME");
        
        JobListingResponse updated = service.updateJobListing(testOwnerId, created.getId(), updateRequest);
        
        // Then
        assertThat(updated.getTitle()).isEqualTo("Updated Title");
        assertThat(updated.getCategory()).isEqualTo("Sales");
        assertThat(updated.getJobType()).isEqualTo("PART_TIME");
    }
    
    @Test
    void shouldDeleteJobListing() {
        // Given
        JobListingRequest request = new JobListingRequest();
        request.setTitle("Job to Delete");
        request.setDescription("Description");
        request.setCategory("IT");
        request.setJobType("FULL_TIME");
        
        JobListingResponse created = service.createJobListing(testOwnerId, JobListing.OwnerType.USER, request);
        
        // When
        service.deleteJobListing(testOwnerId, created.getId());
        
        // Then
        List<JobListingResponse> ownerJobs = service.getOwnerJobListings(testOwnerId);
        assertThat(ownerJobs).isEmpty();
    }
    
    @Test
    void shouldFilterPublishedJobs() {
        // Given
        // Create published job
        JobListingRequest publishedRequest = new JobListingRequest();
        publishedRequest.setTitle("Published Job");
        publishedRequest.setDescription("Published description");
        publishedRequest.setCategory("IT");
        publishedRequest.setJobType("FULL_TIME");
        publishedRequest.setStatus("PUBLISHED");
        
        JobListingResponse published = service.createJobListing(testOwnerId, JobListing.OwnerType.USER, publishedRequest);
        
        // Create draft job
        JobListingRequest draftRequest = new JobListingRequest();
        draftRequest.setTitle("Draft Job");
        draftRequest.setDescription("Draft description");
        draftRequest.setCategory("IT");
        draftRequest.setJobType("FULL_TIME");
        draftRequest.setStatus("DRAFT");
        
        service.createJobListing(testOwnerId, JobListing.OwnerType.USER, draftRequest);
        
        // When
        Pageable pageable = PageRequest.of(0, 20);
        Page<JobListingResponse> publishedJobs = service.getPublishedActiveJobs(pageable);
        
        // Then
        assertThat(publishedJobs.getContent()).isNotEmpty();
        assertThat(publishedJobs.getContent().stream()
                .anyMatch(j -> j.getId().equals(published.getId()))).isTrue();
    }
    
    @Test
    void shouldFilterJobsByCategory() {
        // Given
        JobListingRequest itJob = new JobListingRequest();
        itJob.setTitle("IT Job");
        itJob.setDescription("IT description");
        itJob.setCategory("IT");
        itJob.setJobType("FULL_TIME");
        itJob.setStatus("PUBLISHED");
        service.createJobListing(testOwnerId, JobListing.OwnerType.USER, itJob);
        
        JobListingRequest salesJob = new JobListingRequest();
        salesJob.setTitle("Sales Job");
        salesJob.setDescription("Sales description");
        salesJob.setCategory("Sales");
        salesJob.setJobType("FULL_TIME");
        salesJob.setStatus("PUBLISHED");
        service.createJobListing(testOwnerId, JobListing.OwnerType.USER, salesJob);
        
        // When
        Pageable pageable = PageRequest.of(0, 20);
        Page<JobListingResponse> itJobs = service.getPublishedActiveJobsByCategory("IT", pageable);
        
        // Then
        assertThat(itJobs.getContent()).isNotEmpty();
        assertThat(itJobs.getContent().stream()
                .allMatch(j -> j.getCategory().equals("IT"))).isTrue();
    }
    
    @Test
    void shouldSearchJobs() {
        // Given
        JobListingRequest request1 = new JobListingRequest();
        request1.setTitle("Java Developer");
        request1.setDescription("Looking for Java developer");
        request1.setCategory("IT");
        request1.setJobType("FULL_TIME");
        request1.setStatus("PUBLISHED");
        service.createJobListing(testOwnerId, JobListing.OwnerType.USER, request1);
        
        JobListingRequest request2 = new JobListingRequest();
        request2.setTitle("Python Developer");
        request2.setDescription("Looking for Python developer");
        request2.setCategory("IT");
        request2.setJobType("FULL_TIME");
        request2.setStatus("PUBLISHED");
        service.createJobListing(testOwnerId, JobListing.OwnerType.USER, request2);
        
        // When
        Pageable pageable = PageRequest.of(0, 20);
        Page<JobListingResponse> javaJobs = service.searchPublishedActiveJobs("Java", pageable);
        
        // Then
        assertThat(javaJobs.getContent()).isNotEmpty();
        assertThat(javaJobs.getContent().stream()
                .anyMatch(j -> j.getTitle().contains("Java"))).isTrue();
    }
}

