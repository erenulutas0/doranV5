package com.microservices.media.dto;

import com.microservices.media.model.Media;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaResponse {
    private UUID id;
    private String fileName;
    private String originalFileName;
    private String contentType;
    private Long fileSize;
    private String fileExtension;
    private Media.MediaType mediaType;
    private String url; // API endpoint to fetch media
    private String thumbnailUrl; // For images
    private String mediumUrl; // For images
    private LocalDateTime uploadedAt;
    
    public static MediaResponse fromEntity(Media media) {
        String baseUrl = "/api/media/";
        return MediaResponse.builder()
                .id(media.getId())
                .fileName(media.getFileName())
                .originalFileName(media.getOriginalFileName())
                .contentType(media.getContentType())
                .fileSize(media.getFileSize())
                .fileExtension(media.getFileExtension())
                .mediaType(media.getMediaType())
                .url(baseUrl + media.getId())
                .thumbnailUrl(media.getThumbnailData() != null ? baseUrl + media.getId() + "/thumbnail" : null)
                .mediumUrl(media.getMediumData() != null ? baseUrl + media.getId() + "/medium" : null)
                .uploadedAt(media.getUploadedAt())
                .build();
    }
}

