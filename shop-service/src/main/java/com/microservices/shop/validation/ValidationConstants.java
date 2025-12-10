package com.microservices.shop.validation;

/**
 * Validation Constants
 * 
 * Tüm validation için kullanılan sabit değerler ve regex pattern'ler
 */
public final class ValidationConstants {
    
    // Constructor (Utility class olduğu için)
    private ValidationConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
    
    // ========== REGEX PATTERNS ==========
    
    /**
     * Email Regex Pattern
     * RFC 5322 compliant (simplified)
     */
    public static final String EMAIL_PATTERN = 
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    
    /**
     * Turkish Phone Number Patterns
     */
    public static final String PHONE_PATTERN_INTERNATIONAL = "^\\+90[1-9][0-9]{9}$";
    public static final String PHONE_PATTERN_NATIONAL = "^0[1-9][0-9]{9}$";
    public static final String PHONE_PATTERN_SHORT = "^[1-9][0-9]{9}$";
    
    /**
     * URL Pattern
     */
    public static final String URL_PATTERN = 
        "^(https?://)?([a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}(/.*)?$";
    
    /**
     * Postal Code Pattern (Turkey)
     * 5 haneli posta kodu
     */
    public static final String POSTAL_CODE_PATTERN = "^[0-9]{5}$";
    
    /**
     * Coordinate Patterns (Latitude/Longitude)
     */
    public static final String LATITUDE_PATTERN = "^-?([0-8]?[0-9]|90)(\\.[0-9]{1,10})?$";
    public static final String LONGITUDE_PATTERN = "^-?((1[0-7][0-9])|([0-9]?[0-9]))(\\.[0-9]{1,10})?$";
    
    /**
     * Alphanumeric with spaces (for names)
     */
    public static final String ALPHANUMERIC_WITH_SPACES = "^[a-zA-Z0-9ğüşıöçĞÜŞİÖÇ ]+$";
    
    // ========== SIZE CONSTRAINTS ==========
    
    /**
     * Name Field Constraints
     */
    public static final int NAME_MIN_LENGTH = 2;
    public static final int NAME_MAX_LENGTH = 100;
    
    /**
     * Description Field Constraints
     */
    public static final int DESCRIPTION_MIN_LENGTH = 10;
    public static final int DESCRIPTION_MAX_LENGTH = 2000;
    
    /**
     * Address Field Constraints
     */
    public static final int ADDRESS_MIN_LENGTH = 5;
    public static final int ADDRESS_MAX_LENGTH = 500;
    
    /**
     * Category Field Constraints
     */
    public static final int CATEGORY_MIN_LENGTH = 2;
    public static final int CATEGORY_MAX_LENGTH = 50;
    
    /**
     * City Field Constraints
     */
    public static final int CITY_MIN_LENGTH = 2;
    public static final int CITY_MAX_LENGTH = 50;
    
    // ========== NUMERIC CONSTRAINTS ==========
    
    /**
     * Price Range
     */
    public static final double PRICE_MIN = 0.01;
    public static final double PRICE_MAX = 999999999.99;
    
    /**
     * Rating Range
     */
    public static final int RATING_MIN = 1;
    public static final int RATING_MAX = 5;
    
    /**
     * Discount Percentage Range
     */
    public static final int DISCOUNT_MIN = 0;
    public static final int DISCOUNT_MAX = 100;
    
    /**
     * Coordinate Ranges
     */
    public static final double LATITUDE_MIN = -90.0;
    public static final double LATITUDE_MAX = 90.0;
    public static final double LONGITUDE_MIN = -180.0;
    public static final double LONGITUDE_MAX = 180.0;
    
    // ========== ERROR MESSAGES ==========
    
    public static final String ERROR_INVALID_EMAIL = "Invalid email format";
    public static final String ERROR_INVALID_PHONE = "Invalid phone number format";
    public static final String ERROR_INVALID_URL = "Invalid URL format";
    public static final String ERROR_INVALID_POSTAL_CODE = "Invalid postal code format (must be 5 digits)";
    public static final String ERROR_INVALID_COORDINATES = "Invalid coordinates";
    
    public static final String ERROR_NAME_SIZE = "Name must be between " + NAME_MIN_LENGTH + " and " + NAME_MAX_LENGTH + " characters";
    public static final String ERROR_DESCRIPTION_SIZE = "Description must be between " + DESCRIPTION_MIN_LENGTH + " and " + DESCRIPTION_MAX_LENGTH + " characters";
    public static final String ERROR_ADDRESS_SIZE = "Address must be between " + ADDRESS_MIN_LENGTH + " and " + ADDRESS_MAX_LENGTH + " characters";
    
    public static final String ERROR_PRICE_RANGE = "Price must be between " + PRICE_MIN + " and " + PRICE_MAX;
    public static final String ERROR_RATING_RANGE = "Rating must be between " + RATING_MIN + " and " + RATING_MAX;
}

