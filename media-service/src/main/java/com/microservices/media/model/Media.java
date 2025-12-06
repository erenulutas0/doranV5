package com.microservices.media.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "media")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Media {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false)
    private String fileName;
    
    @Column(nullable = false)
    private String originalFileName;
    
    @Column(nullable = false)
    private String contentType;
    
    @Column(nullable = false)
    private Long fileSize;
    
    @Column(nullable = false)
    private String fileExtension;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MediaType mediaType;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StorageType storageType;
    
    // For local storage: Base64 encoded data
    @Lob
    @Column(columnDefinition = "TEXT")
    private String data;
    
    // For S3 storage: S3 URL (future)
    @Column
    private String url;
    
    // Thumbnails (for images)
    @Lob
    @Column(columnDefinition = "TEXT")
    private String thumbnailData;
    
    @Lob
    @Column(columnDefinition = "TEXT")
    private String mediumData;
    
    private UUID uploadedBy;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime uploadedAt;
    
    private LocalDateTime deletedAt;
    
    @Column(nullable = false)
    private Boolean isDeleted = false;
    
    @PrePersist
    protected void onCreate() {
        uploadedAt = LocalDateTime.now();
    }
    
    public enum MediaType {
        IMAGE,
        DOCUMENT,
        VIDEO,
        OTHER
    }
    
    public enum StorageType {
        LOCAL,
        S3
    }
}

