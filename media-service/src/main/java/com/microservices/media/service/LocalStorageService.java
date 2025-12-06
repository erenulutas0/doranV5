package com.microservices.media.service;

import com.microservices.media.model.Media;
import com.microservices.media.repository.MediaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocalStorageService implements StorageService {
    
    private final MediaRepository mediaRepository;
    private final Tika tika = new Tika();
    
    @Value("${media.storage.max-file-size}")
    private Long maxFileSize;
    
    @Value("${media.storage.allowed-extensions}")
    private String allowedExtensions;
    
    @Value("${media.image.resize.enabled}")
    private Boolean resizeEnabled;
    
    @Value("${media.image.resize.thumbnail-width}")
    private Integer thumbnailWidth;
    
    @Value("${media.image.resize.medium-width}")
    private Integer mediumWidth;
    
    @Value("${media.image.resize.quality}")
    private Double quality;
    
    private static final Set<String> IMAGE_CONTENT_TYPES = Set.of(
        "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    );
    
    @Override
    public Media store(MultipartFile file, UUID uploadedBy) throws IOException {
        // Validate file
        validateFile(file);
        
        String originalFileName = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFileName);
        String contentType = detectContentType(file);
        long fileSize = file.getSize();
        
        // Generate unique file name
        String fileName = UUID.randomUUID().toString() + "." + fileExtension;
        
        // Convert to Base64
        byte[] fileBytes = file.getBytes();
        String encodedData = Base64.getEncoder().encodeToString(fileBytes);
        
        // Create Media entity
        Media media = new Media();
        media.setFileName(fileName);
        media.setOriginalFileName(originalFileName);
        media.setContentType(contentType);
        media.setFileSize(fileSize);
        media.setFileExtension(fileExtension);
        media.setMediaType(determineMediaType(contentType));
        media.setStorageType(Media.StorageType.LOCAL);
        media.setData(encodedData);
        media.setUploadedBy(uploadedBy);
        
        // Generate thumbnails for images
        if (isImage(contentType) && resizeEnabled) {
            try {
                generateThumbnails(media, fileBytes);
            } catch (Exception e) {
                log.warn("Failed to generate thumbnails for {}: {}", fileName, e.getMessage());
            }
        }
        
        media = mediaRepository.save(media);
        log.info("Media stored successfully: {} ({})", media.getId(), originalFileName);
        
        return media;
    }
    
    @Override
    public byte[] retrieve(UUID mediaId) throws IOException {
        Media media = mediaRepository.findByIdAndIsDeletedFalse(mediaId)
                .orElseThrow(() -> new IOException("Media not found: " + mediaId));
        
        if (media.getData() == null) {
            throw new IOException("Media data not available");
        }
        
        return Base64.getDecoder().decode(media.getData());
    }
    
    @Override
    public byte[] retrieveThumbnail(UUID mediaId) throws IOException {
        Media media = mediaRepository.findByIdAndIsDeletedFalse(mediaId)
                .orElseThrow(() -> new IOException("Media not found: " + mediaId));
        
        if (media.getThumbnailData() == null) {
            // Return original if thumbnail not available
            return retrieve(mediaId);
        }
        
        return Base64.getDecoder().decode(media.getThumbnailData());
    }
    
    @Override
    public byte[] retrieveMedium(UUID mediaId) throws IOException {
        Media media = mediaRepository.findByIdAndIsDeletedFalse(mediaId)
                .orElseThrow(() -> new IOException("Media not found: " + mediaId));
        
        if (media.getMediumData() == null) {
            // Return original if medium not available
            return retrieve(mediaId);
        }
        
        return Base64.getDecoder().decode(media.getMediumData());
    }
    
    @Override
    public void delete(UUID mediaId) throws IOException {
        Media media = mediaRepository.findByIdAndIsDeletedFalse(mediaId)
                .orElseThrow(() -> new IOException("Media not found: " + mediaId));
        
        // Soft delete
        media.setIsDeleted(true);
        media.setDeletedAt(java.time.LocalDateTime.now());
        mediaRepository.save(media);
        
        log.info("Media soft deleted: {}", mediaId);
    }
    
    @Override
    public boolean supports(String storageType) {
        return "local".equalsIgnoreCase(storageType);
    }
    
    private void validateFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("File is empty");
        }
        
        if (file.getSize() > maxFileSize) {
            throw new IOException("File size exceeds maximum allowed size: " + maxFileSize);
        }
        
        String extension = getFileExtension(file.getOriginalFilename());
        if (!isAllowedExtension(extension)) {
            throw new IOException("File extension not allowed: " + extension);
        }
    }
    
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }
    
    private boolean isAllowedExtension(String extension) {
        return allowedExtensions.toLowerCase().contains(extension.toLowerCase());
    }
    
    private String detectContentType(MultipartFile file) throws IOException {
        return tika.detect(file.getBytes());
    }
    
    private boolean isImage(String contentType) {
        return IMAGE_CONTENT_TYPES.contains(contentType.toLowerCase());
    }
    
    private Media.MediaType determineMediaType(String contentType) {
        if (isImage(contentType)) {
            return Media.MediaType.IMAGE;
        } else if (contentType.contains("pdf") || contentType.contains("document") || 
                   contentType.contains("msword") || contentType.contains("officedocument")) {
            return Media.MediaType.DOCUMENT;
        } else if (contentType.contains("video")) {
            return Media.MediaType.VIDEO;
        }
        return Media.MediaType.OTHER;
    }
    
    private void generateThumbnails(Media media, byte[] imageData) throws IOException {
        BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(imageData));
        
        if (originalImage == null) {
            throw new IOException("Failed to read image");
        }
        
        // Generate thumbnail
        BufferedImage thumbnail = Scalr.resize(originalImage, 
                Scalr.Method.QUALITY, 
                Scalr.Mode.FIT_TO_WIDTH,
                thumbnailWidth, 
                Scalr.OP_ANTIALIAS);
        media.setThumbnailData(encodeImage(thumbnail, media.getFileExtension()));
        
        // Generate medium size
        if (originalImage.getWidth() > mediumWidth) {
            BufferedImage medium = Scalr.resize(originalImage, 
                    Scalr.Method.QUALITY, 
                    Scalr.Mode.FIT_TO_WIDTH,
                    mediumWidth, 
                    Scalr.OP_ANTIALIAS);
            media.setMediumData(encodeImage(medium, media.getFileExtension()));
        } else {
            // Use original if already smaller than medium size
            media.setMediumData(media.getData());
        }
    }
    
    private String encodeImage(BufferedImage image, String extension) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String formatName = extension.equalsIgnoreCase("jpg") ? "jpeg" : extension;
        ImageIO.write(image, formatName, baos);
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }
}

