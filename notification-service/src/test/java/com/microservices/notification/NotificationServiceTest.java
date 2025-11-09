package com.microservices.notification;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.microservices.notification.Exception.ResourceNotFoundException;
import com.microservices.notification.Model.Notification;
import com.microservices.notification.Model.NotificationStatus;
import com.microservices.notification.Model.NotificationType;
import com.microservices.notification.Repository.NotificationRepository;
import com.microservices.notification.Service.NotificationService;

/**
 * NotificationService için Unit Test
 * @DataJpaTest: Sadece JPA katmanını test eder, hızlı testler için
 */
@DataJpaTest
@Import(NotificationService.class)
@ActiveProfiles("test")
class NotificationServiceTest {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NotificationRepository notificationRepository;

    private Notification testNotification;
    private UUID testUserId;

    @BeforeEach
    void setUp() {
        // Her test öncesi çalışır
        testUserId = UUID.randomUUID();
        
        testNotification = new Notification();
        testNotification.setRecipient("test@example.com");
        testNotification.setType(NotificationType.EMAIL);
        testNotification.setSubject("Test Subject");
        testNotification.setMessage("Test message content for notification");
        testNotification.setUserId(testUserId);
    }

    @Test
    void testCreateNotification() {
        // Given: testNotification hazır
        // When: Bildirim oluşturuluyor
        Notification createdNotification = notificationService.createNotification(testNotification);

        // Then: Bildirim başarıyla oluşturuldu
        assertNotNull(createdNotification.getId());
        assertEquals("test@example.com", createdNotification.getRecipient());
        assertEquals(NotificationType.EMAIL, createdNotification.getType());
        assertEquals(NotificationStatus.PENDING, createdNotification.getStatus());
        assertNotNull(createdNotification.getCreatedAt());
    }

    @Test
    void testGetAllNotifications() {
        // Given: Birkaç bildirim oluşturuluyor
        notificationService.createNotification(testNotification);
        
        Notification notification2 = new Notification();
        notification2.setRecipient("user2@example.com");
        notification2.setType(NotificationType.SMS);
        notification2.setSubject("SMS Subject");
        notification2.setMessage("SMS message content");
        notificationService.createNotification(notification2);

        // When: Tüm bildirimler getiriliyor
        List<Notification> notifications = notificationService.getAllNotifications();

        // Then: 2 bildirim olmalı
        assertEquals(2, notifications.size());
    }

    @Test
    void testGetNotificationById() {
        // Given: Bir bildirim oluşturuluyor
        Notification createdNotification = notificationService.createNotification(testNotification);
        UUID notificationId = createdNotification.getId();

        // When: ID ile bildirim getiriliyor
        Notification foundNotification = notificationService.getNotificationById(notificationId);

        // Then: Bildirim bulundu
        assertNotNull(foundNotification);
        assertEquals(notificationId, foundNotification.getId());
    }

    @Test
    void testGetNotificationByIdNotFound() {
        // Given: Var olmayan bir ID
        UUID nonExistentId = UUID.randomUUID();

        // When & Then: Exception fırlatılmalı
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            notificationService.getNotificationById(nonExistentId);
        });
        assertTrue(exception.getMessage().contains("Notification not found"));
    }

    @Test
    void testSendNotification() {
        // Given: Bir bildirim oluşturuluyor
        Notification createdNotification = notificationService.createNotification(testNotification);
        UUID notificationId = createdNotification.getId();
        assertEquals(NotificationStatus.PENDING, createdNotification.getStatus());

        // When: Bildirim gönderiliyor
        Notification sentNotification = notificationService.sendNotification(notificationId);

        // Then: Bildirim gönderildi
        assertEquals(NotificationStatus.SENT, sentNotification.getStatus());
        assertNotNull(sentNotification.getSentAt());
    }

    @Test
    void testSendNotificationAlreadySent() {
        // Given: Gönderilmiş bir bildirim
        Notification createdNotification = notificationService.createNotification(testNotification);
        UUID notificationId = createdNotification.getId();
        notificationService.sendNotification(notificationId);

        // When & Then: Zaten gönderilmiş bildirim tekrar gönderilemez
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            notificationService.sendNotification(notificationId);
        });
        assertTrue(exception.getMessage().contains("Notification can only be sent when status is PENDING"));
    }

    @Test
    void testUpdateNotificationStatus() {
        // Given: Bir bildirim oluşturuluyor
        Notification createdNotification = notificationService.createNotification(testNotification);
        UUID notificationId = createdNotification.getId();

        // When: Bildirim durumu güncelleniyor
        Notification updatedNotification = notificationService.updateNotificationStatus(
            notificationId, NotificationStatus.DELIVERED);

        // Then: Durum güncellendi
        assertEquals(NotificationStatus.DELIVERED, updatedNotification.getStatus());
        assertNotNull(updatedNotification.getDeliveredAt());
    }

    @Test
    void testUpdateNotification() {
        // Given: Bir bildirim oluşturuluyor
        Notification createdNotification = notificationService.createNotification(testNotification);
        UUID notificationId = createdNotification.getId();

        // When: Bildirim güncelleniyor
        Notification updateData = new Notification();
        updateData.setSubject("Updated Subject");
        updateData.setMessage("Updated message content");

        Notification updatedNotification = notificationService.updateNotification(notificationId, updateData);

        // Then: Bildirim güncellendi
        assertEquals(notificationId, updatedNotification.getId());
        assertEquals("Updated Subject", updatedNotification.getSubject());
        assertEquals("Updated message content", updatedNotification.getMessage());
    }

    @Test
    void testUpdateNotificationOnlyPending() {
        // Given: Gönderilmiş bir bildirim
        Notification createdNotification = notificationService.createNotification(testNotification);
        UUID notificationId = createdNotification.getId();
        notificationService.sendNotification(notificationId);

        // When & Then: Sadece PENDING durumundaki bildirimler güncellenebilir
        Notification updateData = new Notification();
        updateData.setSubject("Updated Subject");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            notificationService.updateNotification(notificationId, updateData);
        });
        assertTrue(exception.getMessage().contains("Notification can only be updated when status is PENDING"));
    }

    @Test
    void testDeleteNotification() {
        // Given: Bir bildirim oluşturuluyor
        Notification createdNotification = notificationService.createNotification(testNotification);
        UUID notificationId = createdNotification.getId();

        // When: Bildirim siliniyor
        notificationService.deleteNotification(notificationId);

        // Then: Bildirim artık bulunamaz
        assertFalse(notificationRepository.existsById(notificationId));
    }

    @Test
    void testGetNotificationsByUserId() {
        // Given: Farklı kullanıcılar için bildirimler
        notificationService.createNotification(testNotification); // testUserId
        
        UUID otherUserId = UUID.randomUUID();
        Notification notification2 = new Notification();
        notification2.setRecipient("user2@example.com");
        notification2.setType(NotificationType.EMAIL);
        notification2.setSubject("Subject 2");
        notification2.setMessage("Message 2 content for notification test");
        notification2.setUserId(otherUserId);
        notificationService.createNotification(notification2);

        // When: testUserId'nin bildirimleri getiriliyor
        List<Notification> userNotifications = notificationService.getNotificationsByUserId(testUserId);

        // Then: Sadece testUserId'nin bildirimleri bulundu
        assertEquals(1, userNotifications.size());
        assertEquals(testUserId, userNotifications.get(0).getUserId());
    }

    @Test
    void testGetNotificationsByType() {
        // Given: Farklı türlerde bildirimler
        notificationService.createNotification(testNotification); // EMAIL
        
        Notification smsNotification = new Notification();
        smsNotification.setRecipient("5551234567");
        smsNotification.setType(NotificationType.SMS);
        smsNotification.setSubject("SMS Subject");
        smsNotification.setMessage("SMS message content for notification test");
        notificationService.createNotification(smsNotification);

        // When: EMAIL türündeki bildirimler getiriliyor
        List<Notification> emailNotifications = notificationService.getNotificationsByType(NotificationType.EMAIL);

        // Then: Sadece EMAIL bildirimleri bulundu
        assertEquals(1, emailNotifications.size());
        assertEquals(NotificationType.EMAIL, emailNotifications.get(0).getType());
    }

    @Test
    void testGetNotificationsByStatus() {
        // Given: Farklı durumlarda bildirimler
        Notification notification1 = notificationService.createNotification(testNotification); // PENDING
        notificationService.sendNotification(notification1.getId()); // SENT
        
        Notification notification2 = new Notification();
        notification2.setRecipient("user2@example.com");
        notification2.setType(NotificationType.EMAIL);
        notification2.setSubject("Subject 2");
        notification2.setMessage("Message 2 content for notification test");
        notificationService.createNotification(notification2); // PENDING

        // When: SENT durumundaki bildirimler getiriliyor
        List<Notification> sentNotifications = notificationService.getNotificationsByStatus(NotificationStatus.SENT);

        // Then: Sadece SENT bildirimler bulundu
        assertEquals(1, sentNotifications.size());
        assertEquals(NotificationStatus.SENT, sentNotifications.get(0).getStatus());
    }

    @Test
    void testGetPendingNotifications() {
        // Given: Farklı durumlarda bildirimler
        notificationService.createNotification(testNotification); // PENDING
        
        Notification notification2 = new Notification();
        notification2.setRecipient("user2@example.com");
        notification2.setType(NotificationType.EMAIL);
        notification2.setSubject("Subject 2");
        notification2.setMessage("Message 2 content for notification");
        notification2.setUserId(testUserId);
        Notification created2 = notificationService.createNotification(notification2); // PENDING
        notificationService.sendNotification(created2.getId()); // SENT

        // When: PENDING durumundaki bildirimler getiriliyor
        List<Notification> pendingNotifications = notificationService.getPendingNotifications();

        // Then: Sadece PENDING bildirimler bulundu
        assertEquals(1, pendingNotifications.size());
        assertEquals(NotificationStatus.PENDING, pendingNotifications.get(0).getStatus());
    }

    @Test
    void testNotificationStatusAutoSet() {
        // Given: testNotification hazır (status null)
        // When: Bildirim oluşturuluyor
        Notification createdNotification = notificationService.createNotification(testNotification);

        // Then: Status otomatik olarak PENDING olmalı
        assertEquals(NotificationStatus.PENDING, createdNotification.getStatus());
    }

    @Test
    void testNotificationTimestampAutoSet() {
        // Given: testNotification hazır
        // When: Bildirim oluşturuluyor
        Notification createdNotification = notificationService.createNotification(testNotification);

        // Then: Timestamp'ler otomatik set edilmeli
        assertNotNull(createdNotification.getCreatedAt());
        assertNotNull(createdNotification.getUpdatedAt());
    }
}

