package com.microservices.media.service;

import com.microservices.media.model.Media;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * Storage abstraction interface
 * Implementations: LocalStorageService (current), S3StorageService (future)
 */
public interface StorageService {
    
    Media store(MultipartFile file, UUID uploadedBy) throws IOException;
    
    byte[] retrieve(UUID mediaId) throws IOException;
    
    byte[] retrieveThumbnail(UUID mediaId) throws IOException;
    
    byte[] retrieveMedium(UUID mediaId) throws IOException;
    
    void delete(UUID mediaId) throws IOException;
    
    boolean supports(String storageType);
}

