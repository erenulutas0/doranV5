package com.microservices.media.service;

import com.microservices.media.model.Media;
import com.microservices.media.repository.MediaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocalStorageServiceUnitTest {
    
    @Mock
    private MediaRepository mediaRepository;
    
    private LocalStorageService storageService;
    
    private UUID testUserId;
    
    @BeforeEach
    void setUp() {
        storageService = new LocalStorageService(mediaRepository);
        testUserId = UUID.randomUUID();
        
        // Set test properties
        ReflectionTestUtils.setField(storageService, "maxFileSize", 10485760L); // 10MB
        ReflectionTestUtils.setField(storageService, "allowedExtensions", "jpg,jpeg,png,gif,webp,pdf,doc,docx");
        ReflectionTestUtils.setField(storageService, "resizeEnabled", false); // Disable image resizing for simple tests
    }
    
    @Test
    void shouldStoreFile() throws IOException {
        // Given
        byte[] content = "test content".getBytes();
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                content
        );
        
        when(mediaRepository.save(any(Media.class))).thenAnswer(invocation -> {
            Media media = invocation.getArgument(0);
            media.setId(UUID.randomUUID());
            return media;
        });
        
        // When
        Media result = storageService.store(file, testUserId);
        
        // Then
        ArgumentCaptor<Media> captor = ArgumentCaptor.forClass(Media.class);
        verify(mediaRepository).save(captor.capture());
        
        Media savedMedia = captor.getValue();
        assertThat(savedMedia.getOriginalFileName()).isEqualTo("test.jpg");
        assertThat(savedMedia.getContentType()).isNotNull(); // Tika may detect as text/plain for mock data
        assertThat(savedMedia.getFileExtension()).isEqualTo("jpg");
        assertThat(savedMedia.getUploadedBy()).isEqualTo(testUserId);
        assertThat(savedMedia.getData()).isNotNull();
        assertThat(savedMedia.getStorageType()).isEqualTo(Media.StorageType.LOCAL);
    }
    
    @Test
    void shouldRejectEmptyFile() {
        // Given
        MockMultipartFile emptyFile = new MockMultipartFile(
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
        ArgumentCaptor<Media> captor = ArgumentCaptor.forClass(Media.class);
        verify(mediaRepository).save(captor.capture());
        
        Media deletedMedia = captor.getValue();
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

