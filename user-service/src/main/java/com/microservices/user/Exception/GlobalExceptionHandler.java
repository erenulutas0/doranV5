package com.microservices.user.Exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import java.util.HashMap;
import java.util.Map;

/**
 * Global Exception Handler
 * Tüm exception'ları merkezi bir yerden yönetir
 * @ControllerAdvice: Tüm controller'lar için geçerlidir
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * ResourceNotFoundException için handler
     * 404 NOT_FOUND döner
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {
        
        ErrorDetails errorDetails = new ErrorDetails(
            "RESOURCE_NOT_FOUND",
            ex.getMessage(),
            request.getDescription(false).replace("uri=", "")
        );
        
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    /**
     * DuplicateResourceException için handler
     * 400 BAD_REQUEST döner
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorDetails> handleDuplicateResourceException(
            DuplicateResourceException ex, WebRequest request) {
        
        ErrorDetails errorDetails = new ErrorDetails(
            "DUPLICATE_RESOURCE",
            ex.getMessage(),
            request.getDescription(false).replace("uri=", "")
        );
        
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    /**
     * Validation hataları için handler
     * @Valid annotation'ı ile yapılan validasyon hatalarını yakalar
     * 400 BAD_REQUEST döner
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        Map<String, Object> response = new HashMap<>();
        response.put("error", "VALIDATION_ERROR");
        response.put("message", "Validation failed");
        response.put("errors", errors);
        response.put("timestamp", java.time.LocalDateTime.now());
        
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Database constraint violation için handler
     * Unique constraint, foreign key constraint vb. hatalarını yakalar
     * 400 BAD_REQUEST döner
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorDetails> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex, WebRequest request) {
        
        String message = "Data integrity violation";
        String errorMessage = ex.getMessage();
        
        // Unique constraint violation mesajını daha anlaşılır hale getir
        if (errorMessage != null) {
            if (errorMessage.contains("email") || errorMessage.contains("EMAIL")) {
                message = "Email already exists";
            } else if (errorMessage.contains("username") || errorMessage.contains("USERNAME")) {
                message = "Username already exists";
            } else if (errorMessage.contains("unique") || errorMessage.contains("UNIQUE")) {
                message = "Duplicate entry: This record already exists";
            }
        }
        
        ErrorDetails errorDetails = new ErrorDetails(
            "DATA_INTEGRITY_VIOLATION",
            message,
            request.getDescription(false).replace("uri=", "")
        );
        
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    /**
     * JPA/Hibernate constraint violation için handler
     * Entity seviyesinde validation hatalarını yakalar (persist time)
     * 400 BAD_REQUEST döner
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolationException(
            ConstraintViolationException ex) {
        
        Map<String, String> errors = new HashMap<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            String fieldName = violation.getPropertyPath().toString();
            String errorMessage = violation.getMessage();
            errors.put(fieldName, errorMessage);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("error", "VALIDATION_ERROR");
        response.put("message", "Validation failed");
        response.put("errors", errors);
        response.put("timestamp", java.time.LocalDateTime.now());
        
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Genel exception handler (son çare)
     * Beklenmeyen hatalar için
     * 500 INTERNAL_SERVER_ERROR döner
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleGlobalException(
            Exception ex, WebRequest request) {
        
        // Root cause'u bul
        Throwable rootCause = ex;
        while (rootCause.getCause() != null && rootCause.getCause() != rootCause) {
            rootCause = rootCause.getCause();
        }
        
        String errorMessage = ex.getMessage();
        String rootCauseMessage = rootCause.getMessage();
        
        // Daha detaylı hata mesajı
        if (rootCauseMessage != null && !rootCauseMessage.equals(errorMessage)) {
            errorMessage = errorMessage + " (Root cause: " + rootCauseMessage + ")";
        }
        
        ErrorDetails errorDetails = new ErrorDetails(
            "INTERNAL_SERVER_ERROR",
            "An unexpected error occurred: " + errorMessage,
            request.getDescription(false).replace("uri=", "")
        );
        
        // Production'da stack trace gösterme!
        // Log'a yazılmalı
        System.err.println("=== Exception Details ===");
        System.err.println("Exception: " + ex.getClass().getName());
        System.err.println("Message: " + ex.getMessage());
        System.err.println("Root Cause: " + rootCause.getClass().getName());
        System.err.println("Root Cause Message: " + rootCauseMessage);
        ex.printStackTrace();
        System.err.println("========================");
        
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

