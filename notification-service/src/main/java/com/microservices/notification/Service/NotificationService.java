package com.microservices.notification.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.microservices.notification.Exception.ResourceNotFoundException;
import com.microservices.notification.Model.Notification;
import com.microservices.notification.Model.NotificationStatus;
import com.microservices.notification.Model.NotificationType;
import com.microservices.notification.Repository.NotificationRepository;

/**
 * Notification Service
 * Bildirim yönetimi için business logic
 * 
 * Özellikler:
 * - Bildirim oluşturma
 * - Bildirim gönderme (simüle edilmiş)
 * - Bildirim durumu güncelleme
 * - Kullanıcıya göre bildirimleri listeleme
 * - Durum ve türe göre filtreleme
 */
@Service
public class NotificationService {
    
    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    /**
     * Tüm bildirimleri getir
     * Admin paneli için kullanılır
     */
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    /**
     * ID'ye göre bildirim getir
     */
    public Notification getNotificationById(UUID notificationId) {
        return notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", notificationId));
    }

    /**
     * Yeni bildirim oluştur
     * 
     * @param notification Bildirim bilgileri
     * @return Oluşturulan bildirim
     * 
     * Not: Bildirim oluşturulduğunda status otomatik olarak PENDING olur
     */
    @Transactional
    public Notification createNotification(Notification notification) {
        // Status PENDING olarak set edilir (@PrePersist)
        return notificationRepository.save(notification);
    }

    /**
     * Bildirim gönder (simüle edilmiş)
     * 
     * Gerçek uygulamada:
     * - EMAIL: SMTP server'a gönderilir
     * - SMS: SMS gateway'e gönderilir
     * - PUSH: Firebase/APNS'e gönderilir
     * - IN_APP: Veritabanına kaydedilir
     * 
     * @param notificationId Bildirim ID'si
     * @return Gönderilen bildirim
     */
    @Transactional
    public Notification sendNotification(UUID notificationId) {
        Notification notification = getNotificationById(notificationId);
        
        // Sadece PENDING durumundaki bildirimler gönderilebilir
        if (notification.getStatus() != NotificationStatus.PENDING) {
            throw new IllegalArgumentException(
                "Notification can only be sent when status is PENDING. Current status: " + notification.getStatus());
        }
        
        try {
            // Simüle edilmiş gönderim
            // Gerçek uygulamada burada gerçek gönderim işlemi yapılır
            simulateSendNotification(notification);
            
            // Başarılı gönderim
            notification.setStatus(NotificationStatus.SENT);
            notification.setSentAt(LocalDateTime.now());
            
        } catch (Exception e) {
            // Hata durumunda
            notification.setStatus(NotificationStatus.FAILED);
            notification.setErrorMessage(e.getMessage());
        }
        
        return notificationRepository.save(notification);
    }

    /**
     * Bildirim durumunu güncelle
     * 
     * @param notificationId Bildirim ID'si
     * @param newStatus Yeni durum
     * @return Güncellenmiş bildirim
     */
    @Transactional
    public Notification updateNotificationStatus(UUID notificationId, NotificationStatus newStatus) {
        Notification notification = getNotificationById(notificationId);
        
        notification.setStatus(newStatus);
        
        // Duruma göre timestamp'leri güncelle
        if (newStatus == NotificationStatus.SENT && notification.getSentAt() == null) {
            notification.setSentAt(LocalDateTime.now());
        } else if (newStatus == NotificationStatus.DELIVERED && notification.getDeliveredAt() == null) {
            notification.setDeliveredAt(LocalDateTime.now());
        } else if (newStatus == NotificationStatus.READ && notification.getReadAt() == null) {
            notification.setReadAt(LocalDateTime.now());
        }
        
        return notificationRepository.save(notification);
    }

    /**
     * Bildirim güncelle
     * Partial update yapıyor (null olmayan field'ları günceller)
     */
    @Transactional
    public Notification updateNotification(UUID notificationId, Notification notificationDetails) {
        Notification notification = getNotificationById(notificationId);
        
        // Sadece PENDING durumundaki bildirimler güncellenebilir
        if (notification.getStatus() != NotificationStatus.PENDING) {
            throw new IllegalArgumentException(
                "Notification can only be updated when status is PENDING. Current status: " + notification.getStatus());
        }
        
        // Güncelleme
        if (notificationDetails.getRecipient() != null) {
            notification.setRecipient(notificationDetails.getRecipient());
        }
        if (notificationDetails.getType() != null) {
            notification.setType(notificationDetails.getType());
        }
        if (notificationDetails.getSubject() != null) {
            notification.setSubject(notificationDetails.getSubject());
        }
        if (notificationDetails.getMessage() != null) {
            notification.setMessage(notificationDetails.getMessage());
        }
        if (notificationDetails.getUserId() != null) {
            notification.setUserId(notificationDetails.getUserId());
        }
        if (notificationDetails.getRelatedEntityType() != null) {
            notification.setRelatedEntityType(notificationDetails.getRelatedEntityType());
        }
        if (notificationDetails.getRelatedEntityId() != null) {
            notification.setRelatedEntityId(notificationDetails.getRelatedEntityId());
        }
        
        return notificationRepository.save(notification);
    }

    /**
     * Bildirim sil
     */
    public void deleteNotification(UUID notificationId) {
        if (!notificationRepository.existsById(notificationId)) {
            throw new ResourceNotFoundException("Notification", "id", notificationId);
        }
        notificationRepository.deleteById(notificationId);
    }

    /**
     * User ID'ye göre bildirimleri getir
     */
    public List<Notification> getNotificationsByUserId(UUID userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * Bildirim türüne göre filtrele
     */
    public List<Notification> getNotificationsByType(NotificationType type) {
        return notificationRepository.findByType(type);
    }

    /**
     * Bildirim durumuna göre filtrele
     */
    public List<Notification> getNotificationsByStatus(NotificationStatus status) {
        return notificationRepository.findByStatus(status);
    }

    /**
     * Alıcıya göre filtrele
     */
    public List<Notification> getNotificationsByRecipient(String recipient) {
        return notificationRepository.findByRecipientOrderByCreatedAtDesc(recipient);
    }

    /**
     * Kullanıcının belirli durumdaki bildirimlerini getir
     */
    public List<Notification> getNotificationsByUserIdAndStatus(UUID userId, NotificationStatus status) {
        return notificationRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, status);
    }

    /**
     * İlgili entity'ye göre bildirimleri getir
     */
    public List<Notification> getNotificationsByRelatedEntity(String relatedEntityType, UUID relatedEntityId) {
        return notificationRepository.findByRelatedEntityTypeAndRelatedEntityIdOrderByCreatedAtDesc(
            relatedEntityType, relatedEntityId);
    }

    /**
     * PENDING durumundaki bildirimleri getir
     * Gönderilmeyi bekleyen bildirimler için
     */
    public List<Notification> getPendingNotifications() {
        return notificationRepository.findByStatusOrderByCreatedAtAsc(NotificationStatus.PENDING);
    }

    /**
     * Bildirim gönderimini simüle et
     * 
     * Gerçek uygulamada burada:
     * - EMAIL: SMTP server'a bağlanılır
     * - SMS: SMS gateway'e istek gönderilir
     * - PUSH: Firebase/APNS'e push notification gönderilir
     * - IN_APP: Sadece veritabanına kaydedilir (zaten kayıtlı)
     */
    private void simulateSendNotification(Notification notification) {
        // Simüle edilmiş gönderim
        // Gerçek uygulamada burada gerçek gönderim işlemi yapılır
        
        switch (notification.getType()) {
            case EMAIL:
                // SMTP server'a gönder
                System.out.println("Sending EMAIL to: " + notification.getRecipient());
                break;
            case SMS:
                // SMS gateway'e gönder
                System.out.println("Sending SMS to: " + notification.getRecipient());
                break;
            case PUSH:
                // Push notification gönder
                System.out.println("Sending PUSH notification to: " + notification.getRecipient());
                break;
            case IN_APP:
                // In-app notification (zaten veritabanında)
                System.out.println("IN_APP notification created for: " + notification.getRecipient());
                break;
        }
        
        // Simüle edilmiş başarılı gönderim
        // Gerçek uygulamada burada hata kontrolü yapılır
    }
}

