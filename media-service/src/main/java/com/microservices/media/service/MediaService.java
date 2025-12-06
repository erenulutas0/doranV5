package com.microservices.media.service;

import com.microservices.media.dto.MediaResponse;
import com.microservices.media.model.Media;
import com.microservices.media.repository.MediaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MediaService {
    
    private final StorageService storageService;
    private final MediaRepository mediaRepository;
    
    @Transactional
    public MediaResponse uploadMedia(MultipartFile file, UUID uploadedBy) throws IOException {
        log.info("Uploading media: {} by user: {}", file.getOriginalFilename(), uploadedBy);
        
        Media media = storageService.store(file, uploadedBy);
        
        return MediaResponse.fromEntity(media);
    }
    
    @Transactional
    public List<MediaResponse> uploadMultipleMedia(List<MultipartFile> files, UUID uploadedBy) throws IOException {
        log.info("Uploading {} media files by user: {}", files.size(), uploadedBy);
        
        return files.stream()
                .map(file -> {
                    try {
                        return uploadMedia(file, uploadedBy);
                    } catch (IOException e) {
                        log.error("Failed to upload file: {}", file.getOriginalFilename(), e);
                        throw new RuntimeException("Failed to upload file: " + file.getOriginalFilename(), e);
                    }
                })
                .collect(Collectors.toList());
    }
    
    public MediaResponse getMedia(UUID mediaId) throws IOException {
        Media media = mediaRepository.findByIdAndIsDeletedFalse(mediaId)
                .orElseThrow(() -> new IOException("Media not found: " + mediaId));
        
        return MediaResponse.fromEntity(media);
    }
    
    public byte[] getMediaData(UUID mediaId) throws IOException {
        return storageService.retrieve(mediaId);
    }
    
    public byte[] getMediaThumbnail(UUID mediaId) throws IOException {
        return storageService.retrieveThumbnail(mediaId);
    }
    
    public byte[] getMediaMedium(UUID mediaId) throws IOException {
        return storageService.retrieveMedium(mediaId);
    }
    
    public List<MediaResponse> getUserMedia(UUID userId) {
        List<Media> mediaList = mediaRepository.findByUploadedByAndIsDeletedFalse(userId);
        return mediaList.stream()
                .map(MediaResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public void deleteMedia(UUID mediaId) throws IOException {
        storageService.delete(mediaId);
    }
}

