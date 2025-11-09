package com.microservices.order.Config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * RabbitMQ Configuration
 * 
 * Exchange, Queue'ları ve message converter'ı yapılandırır
 * 
 * Exchange:
 * - order.events.exchange: Tüm order event'lerinin gönderildiği exchange
 * 
 * Queue'lar:
 * - order.created: Sipariş oluşturulduğunda gönderilir
 * - order.status.changed: Sipariş durumu değiştiğinde gönderilir
 */
@Configuration
public class RabbitMQConfig {

    // Exchange ismi (Notification Service ile aynı olmalı)
    public static final String ORDER_EXCHANGE = "order.events.exchange";
    
    // Routing Key'ler (Notification Service ile aynı olmalı)
    public static final String ROUTING_KEY_CREATED = "order.created.key";
    public static final String ROUTING_KEY_STATUS_CHANGED = "order.status.changed.key";
    
    // Queue isimleri
    public static final String ORDER_CREATED_QUEUE = "order.created";
    public static final String ORDER_STATUS_CHANGED_QUEUE = "order.status.changed";

    /**
     * Order Created Queue
     * Sipariş oluşturulduğunda mesaj gönderilir
     */
    @Bean
    public Queue orderCreatedQueue() {
        return new Queue(ORDER_CREATED_QUEUE, true);  // durable: true (RabbitMQ restart olsa bile queue kalır)
    }

    /**
     * Order Status Changed Queue
     * Sipariş durumu değiştiğinde mesaj gönderilir
     */
    @Bean
    public Queue orderStatusChangedQueue() {
        return new Queue(ORDER_STATUS_CHANGED_QUEUE, true);
    }

    /**
     * Direct Exchange
     * Tüm order event'lerinin gönderildiği exchange
     */
    @Bean
    public DirectExchange orderExchange() {
        return new DirectExchange(ORDER_EXCHANGE, true, false); // durable: true, auto-delete: false
    }

    /**
     * Order Created Queue Binding
     * order.created queue'sunu exchange'e bağlar
     */
    @Bean
    public Binding orderCreatedBinding(@Qualifier("orderCreatedQueue") Queue orderCreatedQueue, DirectExchange orderExchange) {
        return BindingBuilder.bind(orderCreatedQueue)
                             .to(orderExchange)
                             .with(ROUTING_KEY_CREATED);
    }

    /**
     * Order Status Changed Queue Binding
     * order.status.changed queue'sunu exchange'e bağlar
     */
    @Bean
    public Binding orderStatusChangedBinding(@Qualifier("orderStatusChangedQueue") Queue orderStatusChangedQueue, DirectExchange orderExchange) {
        return BindingBuilder.bind(orderStatusChangedQueue)
                             .to(orderExchange)
                             .with(ROUTING_KEY_STATUS_CHANGED);
    }

    /**
     * Jackson2JsonMessageConverter
     * Java objelerini JSON'a, JSON'ı Java objelerine çevirir
     * JavaTimeModule: LocalDateTime, LocalDate gibi Java 8 Date/Time API tiplerini destekler
     */
    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // LocalDateTime desteği için
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    /**
     * RabbitTemplate
     * Mesaj göndermek için kullanılır
     * JSON formatında mesaj göndermek için Jackson2JsonMessageConverter kullanılır
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, Jackson2JsonMessageConverter jsonMessageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter);
        return rabbitTemplate;
    }
}

