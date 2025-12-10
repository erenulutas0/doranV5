package com.microservices.shop.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

/**
 * Phone Number Validator Implementation
 * 
 * Türkiye telefon numarası formatlarını validate eder:
 * - +90XXXXXXXXXX (uluslararası format)
 * - 0XXXXXXXXXX (ulusal format)
 * - 5XXXXXXXXX (başta sıfır olmayan)
 */
public class PhoneNumberValidator implements ConstraintValidator<PhoneNumber, String> {
    
    // Regex patterns
    private static final Pattern INTERNATIONAL_PATTERN = Pattern.compile("^\\+90[1-9][0-9]{9}$");
    private static final Pattern NATIONAL_PATTERN = Pattern.compile("^0[1-9][0-9]{9}$");
    private static final Pattern SHORT_PATTERN = Pattern.compile("^[1-9][0-9]{9}$");
    
    @Override
    public void initialize(PhoneNumber constraintAnnotation) {
        // Initialization logic (if needed)
    }
    
    @Override
    public boolean isValid(String phoneNumber, ConstraintValidatorContext context) {
        // Null veya empty kontrol (optional field için)
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return true; // @NotNull ile kontrol edilmeli
        }
        
        // Whitespace temizle
        String cleanedPhone = phoneNumber.trim().replaceAll("\\s+", "");
        
        // Formatlardan birini kontrol et
        return INTERNATIONAL_PATTERN.matcher(cleanedPhone).matches() ||
               NATIONAL_PATTERN.matcher(cleanedPhone).matches() ||
               SHORT_PATTERN.matcher(cleanedPhone).matches();
    }
}

