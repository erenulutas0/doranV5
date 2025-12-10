package com.microservices.review.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Async Processing Configuration
 * 
 * Background işler için async executor konfigürasyonu:
 * - Email notifications
 * - Analytics processing
 * - Cache warming
 * - Report generation
 */
@Configuration
@EnableAsync
public class AsyncConfiguration {
    
    /**
     * Async Task Executor
     * 
     * Core Pool Size: 5 - Minimum thread sayısı
     * Max Pool Size: 10 - Maksimum thread sayısı
     * Queue Capacity: 100 - Bekleyen task kuyruk kapasitesi
     */
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        
        // Core thread pool size
        executor.setCorePoolSize(5);
        
        // Maximum thread pool size
        executor.setMaxPoolSize(10);
        
        // Queue capacity for waiting tasks
        executor.setQueueCapacity(100);
        
        // Thread name prefix (for debugging)
        executor.setThreadNamePrefix("Async-Review-");
        
        // Wait for tasks to complete on shutdown
        executor.setWaitForTasksToCompleteOnShutdown(true);
        
        // Await termination timeout (seconds)
        executor.setAwaitTerminationSeconds(60);
        
        // Initialize the executor
        executor.initialize();
        
        return executor;
    }
}

