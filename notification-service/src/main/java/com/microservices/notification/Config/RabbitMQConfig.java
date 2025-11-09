package com.microservices.notification.Config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange; // DirectExchange import'u eklendi
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.ConditionalRejectingErrorHandler;
import org.springframework.amqp.rabbit.listener.FatalExceptionStrategy;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class RabbitMQConfig {

    // KODU TAMAMLAMAK İÇİN EKLENEN SABİTLER
    public static final String ORDER_EXCHANGE = "order.events.exchange";
    public static final String ROUTING_KEY_CREATED = "order.created.key";
    public static final String ROUTING_KEY_STATUS_CHANGED = "order.status.changed.key";
    
    // Queue isimleri (Order Service ile aynı olmalı)
    public static final String ORDER_CREATED_QUEUE = "order.created";
    public static final String ORDER_STATUS_CHANGED_QUEUE = "order.status.changed";

    /**
     * Order Created Queue
     */
    @Bean
    public Queue orderCreatedQueue() {
        return new Queue(ORDER_CREATED_QUEUE, true); // durable: true
    }

    /**
     * Order Status Changed Queue
     */
    @Bean
    public Queue orderStatusChangedQueue() {
        return new Queue(ORDER_STATUS_CHANGED_QUEUE, true);
    }

    // YENİ EKLENEN EXCHANGE VE BINDING TANIMLARI

    /**
     * Direct Exchange Tanımı
     * Producer'ın mesajları gönderdiği değişim noktası
     */
    @Bean
    public DirectExchange orderExchange() {
        return new DirectExchange(ORDER_EXCHANGE);
    }

    /**
     * order.created kuyruğunu Exchange'e, belirli bir Routing Key ile bağlar (Binding)
     */
    @Bean
    public Binding createdBinding(@Qualifier("orderCreatedQueue") Queue orderCreatedQueue, DirectExchange orderExchange) {
        return BindingBuilder.bind(orderCreatedQueue)
                             .to(orderExchange)
                             .with(ROUTING_KEY_CREATED); // Producer ile eşleşmeli
    }

    /**
     * order.status.changed kuyruğunu Exchange'e, belirli bir Routing Key ile bağlar (Binding)
     */
    @Bean
    public Binding statusChangedBinding(@Qualifier("orderStatusChangedQueue") Queue orderStatusChangedQueue, DirectExchange orderExchange) {
        return BindingBuilder.bind(orderStatusChangedQueue)
                             .to(orderExchange)
                             .with(ROUTING_KEY_STATUS_CHANGED); // Producer ile eşleşmeli
    }

    // --- Mesaj Dönüştürücüler ---

    /**
     * Jackson2JsonMessageConverter
     * JSON mesajlarını Java objelerine çevirir (Deserialization)
     */
    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // LocalDateTime desteği için
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    /**
     * SimpleRabbitListenerContainerFactory
     * Listener'ların JSON'ı doğru dönüştürmesini sağlar
     */
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter());
        
        // Hata işleyici (Error Handler) eklendi
        factory.setErrorHandler(new ConditionalRejectingErrorHandler(new FatalExceptionStrategy() {
            @Override
            public boolean isFatal(Throwable t) {
                log.error("=== RabbitMQ Listener Error ===");
                log.error("Exception: {}", t.getClass().getName());
                log.error("Message: {}", t.getMessage());
                if (t.getCause() != null) {
                    log.error("Cause: {}", t.getCause().getMessage());
                }
                t.printStackTrace();
                log.error("================================");
                return false; // Mesajı tekrar denemek için fatal değil
            }
        }));
        
        log.info("RabbitMQ Listener Container Factory configured with JSON message converter");
        
        return factory;
    }
}