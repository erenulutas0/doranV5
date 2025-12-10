package com.microservices.review.event;

import com.microservices.review.model.Review;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Review Created Event
 * 
 * Yeni review oluşturulduğunda fırlatılır
 * Async event listener ile işlenir
 */
@Getter
public class ReviewCreatedEvent extends ApplicationEvent {
    
    private final Review review;
    
    public ReviewCreatedEvent(Object source, Review review) {
        super(source);
        this.review = review;
    }
}

