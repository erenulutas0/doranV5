package com.microservices.shop.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * Phone Number Validation Annotation
 * 
 * Türkiye telefon numaraları için validation:
 * - +90 ile başlayabilir
 * - 5XX formatında olmalı
 * - 10 haneli olmalı (başta 0 varsa)
 * - Örnek: +905551234567, 05551234567, 5551234567
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PhoneNumberValidator.class)
@Documented
public @interface PhoneNumber {
    String message() default "Invalid phone number format. Expected: +905XXXXXXXXX or 05XXXXXXXXX";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

