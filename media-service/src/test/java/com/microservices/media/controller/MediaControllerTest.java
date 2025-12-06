package com.microservices.media.controller;

import com.microservices.media.dto.MediaResponse;
import com.microservices.media.model.Media;
import com.microservices.media.repository.MediaRepository;
import com.microservices.media.service.MediaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MediaController.class)
@ActiveProfiles("test")
class MediaControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private MediaService mediaService;
    
    @MockBean
    private MediaRepository mediaRepository;
    
    private UUID testMediaId;
    private UUID testUserId;
    private Media testMedia;
    private MediaResponse testMediaResponse;
    
    @BeforeEach
    void setUp() {
        testMediaId = UUID.randomUUID();
        testUserId = UUID.randomUUID();
        
        testMedia = new Media();
        testMedia.setId(testMediaId);
        testMedia.setFileName("test-file.jpg");
        testMedia.setOriginalFileName("original.jpg");
        testMedia.setContentType("image/jpeg");
        testMedia.setFileSize(1024L);
        testMedia.setFileExtension("jpg");
        testMedia.setMediaType(Media.MediaType.IMAGE);
        testMedia.setStorageType(Media.StorageType.LOCAL);
        testMedia.setData("base64encodeddata");
        testMedia.setIsDeleted(false);
        
        testMediaResponse = MediaResponse.fromEntity(testMedia);
    }
    
    @Test
    void shouldUploadMedia() throws Exception {
        // Given
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "test content".getBytes()
        );
        
        when(mediaService.uploadMedia(any(), any())).thenReturn(testMediaResponse);
        
        // When & Then
        mockMvc.perform(multipart("/api/media/upload")
                        .file(file)
                        .param("uploadedBy", testUserId.toString()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(testMediaId.toString()))
                .andExpect(jsonPath("$.fileName").value("test-file.jpg"))
                .andExpect(jsonPath("$.contentType").value("image/jpeg"));
    }
    
    @Test
    void shouldUploadMultipleMedia() throws Exception {
        // Given
        MockMultipartFile file1 = new MockMultipartFile(
                "files",
                "test1.jpg",
                "image/jpeg",
                "test content 1".getBytes()
        );
        
        MockMultipartFile file2 = new MockMultipartFile(
                "files",
                "test2.jpg",
                "image/jpeg",
                "test content 2".getBytes()
        );
        
        List<MediaResponse> responses = Arrays.asList(testMediaResponse, testMediaResponse);
        when(mediaService.uploadMultipleMedia(any(), any())).thenReturn(responses);
        
        // When & Then
        mockMvc.perform(multipart("/api/media/upload/bulk")
                        .file(file1)
                        .file(file2)
                        .param("uploadedBy", testUserId.toString()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }
    
    @Test
    void shouldGetMedia() throws Exception {
        // Given
        byte[] mediaData = "test image data".getBytes();
        when(mediaRepository.findByIdAndIsDeletedFalse(testMediaId)).thenReturn(Optional.of(testMedia));
        when(mediaService.getMediaData(testMediaId)).thenReturn(mediaData);
        
        // When & Then
        mockMvc.perform(get("/api/media/{id}", testMediaId))
                .andExpect(status().isOk())
                .andExpect(content().contentType("image/jpeg"))
                .andExpect(content().bytes(mediaData));
    }
    
    @Test
    void shouldGetMediaThumbnail() throws Exception {
        // Given
        byte[] thumbnailData = "thumbnail data".getBytes();
        when(mediaRepository.findByIdAndIsDeletedFalse(testMediaId)).thenReturn(Optional.of(testMedia));
        when(mediaService.getMediaThumbnail(testMediaId)).thenReturn(thumbnailData);
        
        // When & Then
        mockMvc.perform(get("/api/media/{id}/thumbnail", testMediaId))
                .andExpect(status().isOk())
                .andExpect(content().contentType("image/jpeg"))
                .andExpect(content().bytes(thumbnailData));
    }
    
    @Test
    void shouldGetMediaMedium() throws Exception {
        // Given
        byte[] mediumData = "medium data".getBytes();
        when(mediaRepository.findByIdAndIsDeletedFalse(testMediaId)).thenReturn(Optional.of(testMedia));
        when(mediaService.getMediaMedium(testMediaId)).thenReturn(mediumData);
        
        // When & Then
        mockMvc.perform(get("/api/media/{id}/medium", testMediaId))
                .andExpect(status().isOk())
                .andExpect(content().contentType("image/jpeg"))
                .andExpect(content().bytes(mediumData));
    }
    
    @Test
    void shouldGetMediaInfo() throws Exception {
        // Given
        when(mediaService.getMedia(testMediaId)).thenReturn(testMediaResponse);
        
        // When & Then
        mockMvc.perform(get("/api/media/{id}/info", testMediaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testMediaId.toString()))
                .andExpect(jsonPath("$.fileName").value("test-file.jpg"))
                .andExpect(jsonPath("$.fileSize").value(1024));
    }
    
    @Test
    void shouldGetUserMedia() throws Exception {
        // Given
        List<MediaResponse> mediaList = Arrays.asList(testMediaResponse);
        when(mediaService.getUserMedia(testUserId)).thenReturn(mediaList);
        
        // When & Then
        mockMvc.perform(get("/api/media/user/{userId}", testUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(testMediaId.toString()));
    }
    
    @Test
    void shouldDeleteMedia() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/media/{id}", testMediaId))
                .andExpect(status().isNoContent());
    }
    
    @Test
    void shouldReturn404WhenMediaNotFound() throws Exception {
        // Given
        when(mediaRepository.findByIdAndIsDeletedFalse(testMediaId)).thenReturn(Optional.empty());
        
        // When & Then
        mockMvc.perform(get("/api/media/{id}", testMediaId))
                .andExpect(status().isNotFound());
    }
}

