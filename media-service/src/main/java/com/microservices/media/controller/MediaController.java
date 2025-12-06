package com.microservices.media.controller;

import com.microservices.media.dto.MediaResponse;
import com.microservices.media.model.Media;
import com.microservices.media.repository.MediaRepository;
import com.microservices.media.service.MediaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/media")
@RequiredArgsConstructor
public class MediaController {
    
    private final MediaService mediaService;
    private final MediaRepository mediaRepository;
    
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MediaResponse> uploadMedia(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "uploadedBy", required = false) UUID uploadedBy) {
        try {
            MediaResponse response = mediaService.uploadMedia(file, uploadedBy);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IOException e) {
            log.error("Failed to upload media", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    @PostMapping(value = "/upload/bulk", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<MediaResponse>> uploadMultipleMedia(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam(value = "uploadedBy", required = false) UUID uploadedBy) {
        try {
            List<MediaResponse> responses = mediaService.uploadMultipleMedia(files, uploadedBy);
            return ResponseEntity.status(HttpStatus.CREATED).body(responses);
        } catch (Exception e) {
            log.error("Failed to upload multiple media", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getMedia(@PathVariable UUID id) {
        try {
            Media media = mediaRepository.findByIdAndIsDeletedFalse(id)
                    .orElseThrow(() -> new IOException("Media not found"));
            
            byte[] mediaData = mediaService.getMediaData(id);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(media.getContentType()));
            headers.setContentLength(mediaData.length);
            headers.setContentDispositionFormData("inline", media.getOriginalFileName());
            
            return new ResponseEntity<>(mediaData, headers, HttpStatus.OK);
        } catch (IOException e) {
            log.error("Failed to retrieve media: {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    @GetMapping("/{id}/thumbnail")
    public ResponseEntity<byte[]> getMediaThumbnail(@PathVariable UUID id) {
        try {
            Media media = mediaRepository.findByIdAndIsDeletedFalse(id)
                    .orElseThrow(() -> new IOException("Media not found"));
            
            byte[] thumbnailData = mediaService.getMediaThumbnail(id);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(media.getContentType()));
            headers.setContentLength(thumbnailData.length);
            headers.setContentDispositionFormData("inline", "thumbnail_" + media.getOriginalFileName());
            
            return new ResponseEntity<>(thumbnailData, headers, HttpStatus.OK);
        } catch (IOException e) {
            log.error("Failed to retrieve thumbnail: {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    @GetMapping("/{id}/medium")
    public ResponseEntity<byte[]> getMediaMedium(@PathVariable UUID id) {
        try {
            Media media = mediaRepository.findByIdAndIsDeletedFalse(id)
                    .orElseThrow(() -> new IOException("Media not found"));
            
            byte[] mediumData = mediaService.getMediaMedium(id);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(media.getContentType()));
            headers.setContentLength(mediumData.length);
            headers.setContentDispositionFormData("inline", "medium_" + media.getOriginalFileName());
            
            return new ResponseEntity<>(mediumData, headers, HttpStatus.OK);
        } catch (IOException e) {
            log.error("Failed to retrieve medium size: {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    @GetMapping("/{id}/info")
    public ResponseEntity<MediaResponse> getMediaInfo(@PathVariable UUID id) {
        try {
            MediaResponse response = mediaService.getMedia(id);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            log.error("Failed to get media info: {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<MediaResponse>> getUserMedia(@PathVariable UUID userId) {
        List<MediaResponse> mediaList = mediaService.getUserMedia(userId);
        return ResponseEntity.ok(mediaList);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMedia(@PathVariable UUID id) {
        try {
            mediaService.deleteMedia(id);
            return ResponseEntity.noContent().build();
        } catch (IOException e) {
            log.error("Failed to delete media: {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}

