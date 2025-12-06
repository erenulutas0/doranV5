package com.microservices.product.Config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Jackson Configuration
 * JSON serialization ayarları
 */
@Configuration
public class JacksonConfig implements WebMvcConfigurer {

    @Bean
    @Primary
    public Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder() {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        builder.serializationInclusion(JsonInclude.Include.ALWAYS); // Null değerleri de serialize et
        builder.propertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE);
        builder.modules(new JavaTimeModule()); // LocalDateTime, LocalDate gibi Java 8 time API desteği
        builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // Timestamp yerine ISO-8601 kullan
        return builder;
    }
    
    @Bean
    @Primary
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper mapper = builder.build();
        mapper.registerModule(new JavaTimeModule()); // JSR310 modülünü kaydet
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // Timestamp yerine ISO-8601 kullan
        return mapper;
    }
    
    @Bean
    @Primary
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter(ObjectMapper objectMapper) {
        return new MappingJackson2HttpMessageConverter(objectMapper);
    }
}

