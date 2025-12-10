package com.microservices.shop.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * Price Range Validation Annotation
 * 
 * Fiyat aralığı validation'ı:
 * - Minimum ve maksimum değer kontrolü
 * - Negative değer kontrolü
 * - Sıfır kontrolü
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PriceRangeValidator.class)
@Documented
public @interface PriceRange {
    String message() default "Price must be between {min} and {max}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    
    double min() default 0.0;
    double max() default Double.MAX_VALUE;
    boolean allowZero() default false;
}

