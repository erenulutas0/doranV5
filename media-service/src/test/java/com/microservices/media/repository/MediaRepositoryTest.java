package com.microservices.media.repository;

import com.microservices.media.model.Media;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class MediaRepositoryTest {
    
    @Autowired
    private MediaRepository mediaRepository;
    
    private Media testMedia;
    private UUID testUserId;
    
    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        
        testMedia = new Media();
        testMedia.setFileName("test-file.jpg");
        testMedia.setOriginalFileName("original.jpg");
        testMedia.setContentType("image/jpeg");
        testMedia.setFileSize(1024L);
        testMedia.setFileExtension("jpg");
        testMedia.setMediaType(Media.MediaType.IMAGE);
        testMedia.setStorageType(Media.StorageType.LOCAL);
        testMedia.setData("base64encodeddata");
        testMedia.setUploadedBy(testUserId);
        testMedia.setIsDeleted(false);
    }
    
    @Test
    void shouldSaveMedia() {
        // When
        Media saved = mediaRepository.save(testMedia);
        
        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getFileName()).isEqualTo("test-file.jpg");
        assertThat(saved.getUploadedAt()).isNotNull();
    }
    
    @Test
    void shouldFindByIdAndIsDeletedFalse() {
        // Given
        Media saved = mediaRepository.save(testMedia);
        
        // When
        Optional<Media> found = mediaRepository.findByIdAndIsDeletedFalse(saved.getId());
        
        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getFileName()).isEqualTo("test-file.jpg");
    }
    
    @Test
    void shouldNotFindDeletedMedia() {
        // Given
        testMedia.setIsDeleted(true);
        Media saved = mediaRepository.save(testMedia);
        
        // When
        Optional<Media> found = mediaRepository.findByIdAndIsDeletedFalse(saved.getId());
        
        // Then
        assertThat(found).isEmpty();
    }
    
    @Test
    void shouldFindByUploadedByAndIsDeletedFalse() {
        // Given
        mediaRepository.save(testMedia);
        
        Media anotherMedia = new Media();
        anotherMedia.setFileName("another-file.jpg");
        anotherMedia.setOriginalFileName("another.jpg");
        anotherMedia.setContentType("image/jpeg");
        anotherMedia.setFileSize(2048L);
        anotherMedia.setFileExtension("jpg");
        anotherMedia.setMediaType(Media.MediaType.IMAGE);
        anotherMedia.setStorageType(Media.StorageType.LOCAL);
        anotherMedia.setData("anotherbase64data");
        anotherMedia.setUploadedBy(testUserId);
        anotherMedia.setIsDeleted(false);
        mediaRepository.save(anotherMedia);
        
        // When
        List<Media> found = mediaRepository.findByUploadedByAndIsDeletedFalse(testUserId);
        
        // Then
        assertThat(found).hasSize(2);
        assertThat(found).extracting(Media::getUploadedBy)
                .containsOnly(testUserId);
    }
    
    @Test
    void shouldFindByMediaTypeAndIsDeletedFalse() {
        // Given
        mediaRepository.save(testMedia);
        
        Media documentMedia = new Media();
        documentMedia.setFileName("document.pdf");
        documentMedia.setOriginalFileName("document.pdf");
        documentMedia.setContentType("application/pdf");
        documentMedia.setFileSize(3072L);
        documentMedia.setFileExtension("pdf");
        documentMedia.setMediaType(Media.MediaType.DOCUMENT);
        documentMedia.setStorageType(Media.StorageType.LOCAL);
        documentMedia.setData("pdfbase64data");
        documentMedia.setUploadedBy(testUserId);
        documentMedia.setIsDeleted(false);
        mediaRepository.save(documentMedia);
        
        // When
        List<Media> images = mediaRepository.findByMediaTypeAndIsDeletedFalse(Media.MediaType.IMAGE);
        List<Media> documents = mediaRepository.findByMediaTypeAndIsDeletedFalse(Media.MediaType.DOCUMENT);
        
        // Then
        assertThat(images).hasSize(1);
        assertThat(images.get(0).getMediaType()).isEqualTo(Media.MediaType.IMAGE);
        
        assertThat(documents).hasSize(1);
        assertThat(documents.get(0).getMediaType()).isEqualTo(Media.MediaType.DOCUMENT);
    }
}

