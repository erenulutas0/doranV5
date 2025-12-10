package com.microservices.shop.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.math.BigDecimal;

/**
 * Price Range Validator Implementation
 * 
 * BigDecimal, Double, Float, Integer, Long değerler için fiyat kontrolü
 */
public class PriceRangeValidator implements ConstraintValidator<PriceRange, Number> {
    
    private double min;
    private double max;
    private boolean allowZero;
    
    @Override
    public void initialize(PriceRange constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
        this.allowZero = constraintAnnotation.allowZero();
    }
    
    @Override
    public boolean isValid(Number value, ConstraintValidatorContext context) {
        // Null kontrol (optional field için)
        if (value == null) {
            return true; // @NotNull ile kontrol edilmeli
        }
        
        double price = value.doubleValue();
        
        // Negatif değer kontrolü
        if (price < 0) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Price cannot be negative")
                   .addConstraintViolation();
            return false;
        }
        
        // Sıfır kontrolü
        if (price == 0 && !allowZero) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Price cannot be zero")
                   .addConstraintViolation();
            return false;
        }
        
        // Range kontrolü
        if (price < min || price > max) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                String.format("Price must be between %.2f and %.2f", min, max))
                   .addConstraintViolation();
            return false;
        }
        
        return true;
    }
}

