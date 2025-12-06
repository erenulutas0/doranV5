package com.microservices.media.service;

import com.microservices.media.model.Media;
import com.microservices.media.repository.MediaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocalStorageServiceTest {
    
    @Mock
    private MediaRepository mediaRepository;
    
    @InjectMocks
    private LocalStorageService storageService;
    
    private UUID testUserId;
    private MultipartFile testImageFile;
    private MultipartFile testPdfFile;
    
    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        
        // Mock image file
        byte[] imageContent = new byte[1024]; // 1KB
        testImageFile = new MockMultipartFile(
                "file",
                "test-image.jpg",
                "image/jpeg",
                imageContent
        );
        
        // Mock PDF file
        byte[] pdfContent = new byte[2048]; // 2KB
        testPdfFile = new MockMultipartFile(
                "file",
                "test-document.pdf",
                "application/pdf",
                pdfContent
        );
        
        // Set test properties
        ReflectionTestUtils.setField(storageService, "maxFileSize", 10485760L); // 10MB
        ReflectionTestUtils.setField(storageService, "allowedExtensions", "jpg,jpeg,png,gif,webp,pdf,doc,docx");
        ReflectionTestUtils.setField(storageService, "resizeEnabled", true);
        ReflectionTestUtils.setField(storageService, "thumbnailWidth", 150);
        ReflectionTestUtils.setField(storageService, "mediumWidth", 500);
        ReflectionTestUtils.setField(storageService, "quality", 0.9);
    }
    
    @Test
    void shouldStoreImageFile() throws IOException {
        // Given
        when(mediaRepository.save(any(Media.class))).thenAnswer(invocation -> {
            Media media = invocation.getArgument(0);
            media.setId(UUID.randomUUID());
            return media;
        });
        
        // When
        Media result = storageService.store(testImageFile, testUserId);
        
        // Then
        ArgumentCaptor<Media> mediaCaptor = ArgumentCaptor.forClass(Media.class);
        verify(mediaRepository).save(mediaCaptor.capture());
        
        Media savedMedia = mediaCaptor.getValue();
        assertThat(savedMedia.getOriginalFileName()).isEqualTo("test-image.jpg");
        assertThat(savedMedia.getContentType()).contains("image");
        assertThat(savedMedia.getFileExtension()).isEqualTo("jpg");
        assertThat(savedMedia.getMediaType()).isEqualTo(Media.MediaType.IMAGE);
        assertThat(savedMedia.getStorageType()).isEqualTo(Media.StorageType.LOCAL);
        assertThat(savedMedia.getData()).isNotNull();
        assertThat(savedMedia.getUploadedBy()).isEqualTo(testUserId);
    }
    
    @Test
    void shouldStorePdfFile() throws IOException {
        // Given
        when(mediaRepository.save(any(Media.class))).thenAnswer(invocation -> {
            Media media = invocation.getArgument(0);
            media.setId(UUID.randomUUID());
            return media;
        });
        
        // When
        Media result = storageService.store(testPdfFile, testUserId);
        
        // Then
        ArgumentCaptor<Media> mediaCaptor = ArgumentCaptor.forClass(Media.class);
        verify(mediaRepository).save(mediaCaptor.capture());
        
        Media savedMedia = mediaCaptor.getValue();
        assertThat(savedMedia.getOriginalFileName()).isEqualTo("test-document.pdf");
        assertThat(savedMedia.getFileExtension()).isEqualTo("pdf");
        assertThat(savedMedia.getMediaType()).isEqualTo(Media.MediaType.DOCUMENT);
        assertThat(savedMedia.getThumbnailData()).isNull(); // PDFs don't get thumbnails
    }
    
    @Test
    void shouldRejectEmptyFile() {
        // Given
        MultipartFile emptyFile = new MockMultipartFile(
                "file",
                "empty.jpg",
                "image/jpeg",
                new byte[0]
        );
        
        // When & Then
        assertThatThrownBy(() -> storageService.store(emptyFile, testUserId))
                .isInstanceOf(IOException.class)
                .hasMessageContaining("empty");
    }
    
    @Test
    void shouldRejectTooLargeFile() {
        // Given
        byte[] largeContent = new byte[15 * 1024 * 1024]; // 15MB
        MultipartFile largeFile = new MockMultipartFile(
                "file",
                "large.jpg",
                "image/jpeg",
                largeContent
        );
        
        // When & Then
        assertThatThrownBy(() -> storageService.store(largeFile, testUserId))
                .isInstanceOf(IOException.class)
                .hasMessageContaining("exceeds maximum");
    }
    
    @Test
    void shouldRejectDisallowedExtension() {
        // Given
        byte[] content = new byte[1024];
        MultipartFile invalidFile = new MockMultipartFile(
                "file",
                "malicious.exe",
                "application/octet-stream",
                content
        );
        
        // When & Then
        assertThatThrownBy(() -> storageService.store(invalidFile, testUserId))
                .isInstanceOf(IOException.class)
                .hasMessageContaining("not allowed");
    }
    
    @Test
    void shouldRetrieveMedia() throws IOException {
        // Given
        UUID mediaId = UUID.randomUUID();
        Media media = new Media();
        media.setId(mediaId);
        media.setData("dGVzdGRhdGE="); // "testdata" in Base64
        media.setIsDeleted(false);
        
        when(mediaRepository.findByIdAndIsDeletedFalse(mediaId)).thenReturn(Optional.of(media));
        
        // When
        byte[] result = storageService.retrieve(mediaId);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(new String(result)).isEqualTo("testdata");
    }
    
    @Test
    void shouldThrowExceptionWhenMediaNotFound() {
        // Given
        UUID mediaId = UUID.randomUUID();
        when(mediaRepository.findByIdAndIsDeletedFalse(mediaId)).thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> storageService.retrieve(mediaId))
                .isInstanceOf(IOException.class)
                .hasMessageContaining("not found");
    }
    
    @Test
    void shouldSoftDeleteMedia() throws IOException {
        // Given
        UUID mediaId = UUID.randomUUID();
        Media media = new Media();
        media.setId(mediaId);
        media.setIsDeleted(false);
        
        when(mediaRepository.findByIdAndIsDeletedFalse(mediaId)).thenReturn(Optional.of(media));
        when(mediaRepository.save(any(Media.class))).thenReturn(media);
        
        // When
        storageService.delete(mediaId);
        
        // Then
        ArgumentCaptor<Media> mediaCaptor = ArgumentCaptor.forClass(Media.class);
        verify(mediaRepository).save(mediaCaptor.capture());
        
        Media deletedMedia = mediaCaptor.getValue();
        assertThat(deletedMedia.getIsDeleted()).isTrue();
        assertThat(deletedMedia.getDeletedAt()).isNotNull();
    }
    
    @Test
    void shouldSupportLocalStorageType() {
        // When & Then
        assertThat(storageService.supports("local")).isTrue();
        assertThat(storageService.supports("LOCAL")).isTrue();
        assertThat(storageService.supports("s3")).isFalse();
    }
}

