package com.microservices.notification.Exception;

import java.time.LocalDateTime;

/**
 * Error Details DTO
 * Hata response'ları için standart format
 */
public class ErrorDetails {
    private String error;
    private String message;
    private String path;
    private LocalDateTime timestamp;

    public ErrorDetails() {
        this.timestamp = LocalDateTime.now();
    }

    public ErrorDetails(String error, String message, String path) {
        this.error = error;
        this.message = message;
        this.path = path;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters
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

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}

