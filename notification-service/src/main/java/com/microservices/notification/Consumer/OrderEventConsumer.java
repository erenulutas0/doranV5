package com.microservices.notification.Consumer;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.microservices.notification.Config.RabbitMQConfig;
import com.microservices.notification.Event.OrderCreatedEvent;
import com.microservices.notification.Event.OrderStatusChangedEvent;
import com.microservices.notification.Model.Notification;
import com.microservices.notification.Model.NotificationStatus;
import com.microservices.notification.Model.NotificationType;
import com.microservices.notification.Service.NotificationService;

import lombok.extern.slf4j.Slf4j;

/**
 * Order Event Consumer
 * 
 * RabbitMQ'dan gelen event'leri dinler ve bildirim gönderir
 * 
 * @RabbitListener: RabbitMQ queue'larını dinler
 * 
 * Event'ler:
 * - OrderCreatedEvent: Sipariş oluşturulduğunda bildirim gönderir
 * - OrderStatusChangedEvent: Sipariş durumu değiştiğinde bildirim gönderir
 */
@Component
@Slf4j
public class OrderEventConsumer {
    
    private final NotificationService notificationService;

    public OrderEventConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * Order Created Event Listener
     * 
     * Sipariş oluşturulduğunda:
     * - Kullanıcıya e-posta bildirimi gönderir
     * - Bildirim kaydı oluşturur
     */
    @RabbitListener(queues = RabbitMQConfig.ORDER_CREATED_QUEUE, containerFactory = "rabbitListenerContainerFactory")
    public void handleOrderCreated(OrderCreatedEvent event, Message message) {
        log.info("=== OrderEventConsumer.handleOrderCreated called ===");
        log.info("Raw message body: {}", new String(message.getBody()));
        log.info("Message properties: {}", message.getMessageProperties());
        log.info("Event object: {}", event);
        log.info("Event class: {}", event != null ? event.getClass().getName() : "null");
        
        if (event != null) {
            log.info("Event orderId: {}", event.getOrderId());
            log.info("Event userId: {}", event.getUserId());
            log.info("Event userEmail: {}", event.getUserEmail());
        }
        
        try {
            // Null check
            if (event == null) {
                log.error("OrderCreatedEvent is null!");
                return;
            }
            if (event.getOrderId() == null) {
                log.error("OrderCreatedEvent.orderId is null!");
                return;
            }
            if (event.getUserEmail() == null || event.getUserEmail().isEmpty()) {
                log.error("OrderCreatedEvent.userEmail is null or empty!");
                return;
            }
            
            log.info("Received OrderCreatedEvent for order: {}", event.getOrderId());
            
            // Bildirim oluştur
            Notification notification = new Notification();
            notification.setRecipient(event.getUserEmail());
            notification.setType(NotificationType.EMAIL);
            notification.setSubject("Siparişiniz Oluşturuldu - #" + event.getOrderId().toString().substring(0, 8));
            notification.setMessage(buildOrderCreatedMessage(event));
            notification.setUserId(event.getUserId());
            notification.setRelatedEntityType("ORDER");
            notification.setRelatedEntityId(event.getOrderId());
            notification.setStatus(NotificationStatus.PENDING);

            // Bildirimi kaydet
            Notification savedNotification = notificationService.createNotification(notification);
            log.info("Notification created with ID: {}", savedNotification.getId());

            // Bildirimi gönder
            notificationService.sendNotification(savedNotification.getId());
            log.info("Order created notification sent for order: {}", event.getOrderId());
        } catch (Exception e) {
            log.error("Error processing OrderCreatedEvent for order {}: {}", event.getOrderId(), e.getMessage(), e);
        }
    }

    /**
     * Order Status Changed Event Listener
     * 
     * Sipariş durumu değiştiğinde:
     * - Kullanıcıya e-posta bildirimi gönderir
     * - Bildirim kaydı oluşturur
     */
    @RabbitListener(queues = RabbitMQConfig.ORDER_STATUS_CHANGED_QUEUE, containerFactory = "rabbitListenerContainerFactory")
    public void handleOrderStatusChanged(OrderStatusChangedEvent event) {
        log.info("Received OrderStatusChangedEvent for order: {} - Status: {} -> {}", 
            event.getOrderId(), event.getOldStatus(), event.getNewStatus());
        try {
            // Duruma göre bildirim mesajı oluştur
            String subject = buildStatusChangedSubject(event);
            String message = buildStatusChangedMessage(event);

            // Bildirim oluştur
            Notification notification = new Notification();
            notification.setRecipient(event.getUserEmail());
            notification.setType(NotificationType.EMAIL);
            notification.setSubject(subject);
            notification.setMessage(message);
            notification.setUserId(event.getUserId());
            notification.setRelatedEntityType("ORDER");
            notification.setRelatedEntityId(event.getOrderId());
            notification.setStatus(NotificationStatus.PENDING);

            // Bildirimi kaydet
            Notification savedNotification = notificationService.createNotification(notification);
            log.info("Notification created with ID: {}", savedNotification.getId());

            // Bildirimi gönder
            notificationService.sendNotification(savedNotification.getId());
            log.info("Order status changed notification sent for order: {}", event.getOrderId());
        } catch (Exception e) {
            log.error("Error processing OrderStatusChangedEvent for order {}: {}", 
                event.getOrderId(), e.getMessage(), e);
        }
    }

    /**
     * Sipariş oluşturulma bildirimi mesajı oluştur
     */
    private String buildOrderCreatedMessage(OrderCreatedEvent event) {
        StringBuilder message = new StringBuilder();
        message.append("Merhaba ").append(event.getUserName()).append(",\n\n");
        message.append("Siparişiniz başarıyla oluşturuldu!\n\n");
        message.append("Sipariş Bilgileri:\n");
        message.append("- Sipariş No: #").append(event.getOrderId().toString().substring(0, 8)).append("\n");
        message.append("- Toplam Tutar: ").append(event.getTotalAmount()).append(" TL\n");
        message.append("- Sipariş Tarihi: ").append(event.getOrderDate()).append("\n\n");
        
        message.append("Sipariş Detayları:\n");
        if (event.getOrderItems() != null) {
            for (OrderCreatedEvent.OrderItemInfo item : event.getOrderItems()) {
                message.append("- ").append(item.getProductName())
                       .append(" x ").append(item.getQuantity())
                       .append(" = ").append(item.getSubtotal()).append(" TL\n");
            }
        }
        
        message.append("\nTeslimat Adresi:\n");
        message.append(event.getShippingAddress()).append("\n");
        message.append(event.getCity()).append(" ").append(event.getZipCode()).append("\n");
        message.append("Tel: ").append(event.getPhoneNumber()).append("\n\n");
        
        message.append("Siparişinizin durumunu takip edebilirsiniz.\n\n");
        message.append("Teşekkürler!");
        
        return message.toString();
    }

    /**
     * Sipariş durumu değişikliği bildirimi başlığı oluştur
     */
    private String buildStatusChangedSubject(OrderStatusChangedEvent event) {
        String statusText = getStatusText(event.getNewStatus());
        return "Sipariş Durumu Güncellendi - #" + event.getOrderId().toString().substring(0, 8) + " - " + statusText;
    }

    /**
     * Sipariş durumu değişikliği bildirimi mesajı oluştur
     */
    private String buildStatusChangedMessage(OrderStatusChangedEvent event) {
        StringBuilder message = new StringBuilder();
        message.append("Merhaba ").append(event.getUserName()).append(",\n\n");
        message.append("Siparişinizin durumu güncellendi.\n\n");
        message.append("Sipariş Bilgileri:\n");
        message.append("- Sipariş No: #").append(event.getOrderId().toString().substring(0, 8)).append("\n");
        message.append("- Eski Durum: ").append(getStatusText(event.getOldStatus())).append("\n");
        message.append("- Yeni Durum: ").append(getStatusText(event.getNewStatus())).append("\n");
        message.append("- Güncelleme Tarihi: ").append(event.getChangedAt()).append("\n\n");
        
        // Duruma göre özel mesaj
        String statusMessage = getStatusMessage(event.getNewStatus());
        if (statusMessage != null) {
            message.append(statusMessage).append("\n\n");
        }
        
        message.append("Teşekkürler!");
        
        return message.toString();
    }

    /**
     * Durum metnini Türkçe'ye çevir
     */
    private String getStatusText(String status) {
        if (status == null) {
            return "Bilinmiyor";
        }
        switch (status.toUpperCase()) {
            case "PENDING":
                return "Beklemede";
            case "CONFIRMED":
                return "Onaylandı";
            case "PROCESSING":
                return "Hazırlanıyor";
            case "SHIPPED":
                return "Kargoya Verildi";
            case "DELIVERED":
                return "Teslim Edildi";
            case "CANCELLED":
                return "İptal Edildi";
            default:
                return status;
        }
    }

    /**
     * Duruma göre özel mesaj
     */
    private String getStatusMessage(String status) {
        if (status == null) {
            return null;
        }
        switch (status.toUpperCase()) {
            case "CONFIRMED":
                return "Siparişiniz onaylandı ve hazırlık aşamasına geçti.";
            case "PROCESSING":
                return "Siparişiniz hazırlanıyor. Kısa süre içinde kargoya verilecektir.";
            case "SHIPPED":
                return "Siparişiniz kargoya verildi! Kargo takip numaranızı kısa süre içinde alacaksınız.";
            case "DELIVERED":
                return "Siparişiniz teslim edildi! Alışverişinizden memnun kaldıysanız değerlendirme yapabilirsiniz.";
            case "CANCELLED":
                return "Siparişiniz iptal edildi. Ödeme iadeniz 3-5 iş günü içinde hesabınıza yansıyacaktır.";
            default:
                return null;
        }
    }
}

