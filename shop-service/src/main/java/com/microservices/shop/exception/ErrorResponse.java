package com.microservices.shop.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Standardized Error Response
 * 
 * Tüm HTTP error response'ları için standart format
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    
    /**
     * Timestamp - Error zamanı
     */
    private LocalDateTime timestamp;
    
    /**
     * HTTP Status Code (400, 404, 500, etc.)
     */
    private int status;
    
    /**
     * HTTP Status Text (Bad Request, Not Found, etc.)
     */
    private String error;
    
    /**
     * Error Message - User-friendly message
     */
    private String message;
    
    /**
     * Error Code - Application-specific error code
     * Örnek: SHOP_NOT_FOUND, VALIDATION_ERROR, etc.
     */
    private String errorCode;
    
    /**
     * Request Path
     */
    private String path;
    
    /**
     * Validation Errors (Field-level errors)
     * Örnek: {"email": "Invalid email format", "price": "Price must be positive"}
     */
    private Map<String, String> validationErrors;
    
    /**
     * Additional Details (Optional)
     * Debug bilgileri veya ek context
     */
    private List<String> details;
    
    /**
     * Trace ID - Distributed tracing için
     */
    private String traceId;
}

