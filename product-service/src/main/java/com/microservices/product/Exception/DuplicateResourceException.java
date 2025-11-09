package com.microservices.product.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Duplicate (tekrar eden) kaynak için exception
 * @ResponseStatus: Otomatik olarak 400 BAD_REQUEST döner
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Resource already exists")
public class DuplicateResourceException extends RuntimeException {
    
    public DuplicateResourceException(String message) {
        super(message);
    }
    
    public DuplicateResourceException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s already exists with %s: '%s'", resourceName, fieldName, fieldValue));
    }
}

