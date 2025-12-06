package com.microservices.media.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Data
public class MediaUploadRequest {
    private MultipartFile file;
    private UUID uploadedBy;
    private String description;
}

