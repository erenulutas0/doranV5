package com.microservices.user.Exception;

import java.time.LocalDateTime;

/**
 * Error response için DTO (Data Transfer Object)
 * Hata detaylarını tutar
 */
public class ErrorDetails {
    private LocalDateTime timestamp;
    private String error;
    private String message;
    private String path;

    public ErrorDetails() {
        this.timestamp = LocalDateTime.now();
    }

    public ErrorDetails(String error, String message) {
        this();
        this.error = error;
        this.message = message;
    }

    public ErrorDetails(String error, String message, String path) {
        this(error, message);
        this.path = path;
    }

    // Getters and Setters
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}

