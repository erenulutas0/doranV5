package com.microservices.inventory.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Global Exception Handler
 * Tüm exception'ları merkezi bir yerden yönetir
 * 
 * @ControllerAdvice: Tüm controller'lar için geçerlidir
 * 
 * Avantajları:
 * 1. Merkezi hata yönetimi - Tüm exception'lar tek yerden yönetilir
 * 2. Tutarlı hata response'ları - Tüm hatalar aynı formatta döner
 * 3. Clean Controller'lar - Controller'larda try-catch gerekmez
 * 4. Kolay bakım - Hata yönetimi değişiklikleri tek yerden yapılır
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * ResourceNotFoundException için handler
     * 404 NOT_FOUND döner
     * 
     * Örnek: Inventory bulunamadığında
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
     * 
     * Örnek: Aynı productId ile inventory zaten varsa
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
     * IllegalArgumentException için handler
     * 400 BAD_REQUEST döner
     * 
     * Inventory Service'e özel:
     * - Yetersiz stok (Insufficient stock)
     * - Negatif miktar (Negative quantity)
     * - Rezerve ihlali (Reserve violation)
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorDetails> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {
        
        ErrorDetails errorDetails = new ErrorDetails(
            "INVALID_REQUEST",
            ex.getMessage(),
            request.getDescription(false).replace("uri=", "")
        );
        
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    /**
     * Validation hataları için handler
     * @Valid annotation'ı ile yapılan validasyon hatalarını yakalar
     * 400 BAD_REQUEST döner
     * 
     * Örnek: @NotNull, @Min, @NotBlank gibi annotation'lar
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
     * Genel exception handler (son çare)
     * Beklenmeyen hatalar için
     * 500 INTERNAL_SERVER_ERROR döner
     * 
     * ÖNEMLİ: Production'da stack trace gösterme!
     * Sadece log'a yazılmalı
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleGlobalException(
            Exception ex, WebRequest request) {
        
        ErrorDetails errorDetails = new ErrorDetails(
            "INTERNAL_SERVER_ERROR",
            "An unexpected error occurred: " + ex.getMessage(),
            request.getDescription(false).replace("uri=", "")
        );
        
        // Production'da stack trace gösterme!
        // Log'a yazılmalı (örnek: log.error("Unexpected error", ex))
        ex.printStackTrace();
        
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
