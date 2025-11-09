package com.microservices.notification;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.microservices.notification.Model.Notification;
import com.microservices.notification.Model.NotificationStatus;
import com.microservices.notification.Model.NotificationType;
import com.microservices.notification.Repository.NotificationRepository;

/**
 * NotificationRepository için Unit Test
 * @DataJpaTest: Sadece JPA katmanını test eder, hızlı testler için
 */
@DataJpaTest
@ActiveProfiles("test")
class NotificationRepositoryTest {

    @Autowired
    private NotificationRepository notificationRepository;

    private Notification testNotification;
    private UUID testUserId;
    private UUID testOrderId;

    @BeforeEach
    void setUp() {
        // Her test öncesi çalışır
        testUserId = UUID.randomUUID();
        testOrderId = UUID.randomUUID();
        
        testNotification = new Notification();
        testNotification.setRecipient("test@example.com");
        testNotification.setType(NotificationType.EMAIL);
        testNotification.setSubject("Test Subject");
        testNotification.setMessage("Test message content for notification");
        testNotification.setUserId(testUserId);
        testNotification.setRelatedEntityType("ORDER");
        testNotification.setRelatedEntityId(testOrderId);
    }

    @Test
    void testSaveNotification() {
        // Given: testNotification hazır
        // When: Bildirim kaydediliyor
        Notification savedNotification = notificationRepository.save(testNotification);

        // Then: Bildirim başarıyla kaydedildi
        assertNotNull(savedNotification.getId());
        assertEquals("test@example.com", savedNotification.getRecipient());
        assertEquals(NotificationType.EMAIL, savedNotification.getType());
        assertEquals(NotificationStatus.PENDING, savedNotification.getStatus());
    }

    @Test
    void testFindNotificationById() {
        // Given: Bir bildirim kaydediliyor
        Notification savedNotification = notificationRepository.save(testNotification);
        UUID notificationId = savedNotification.getId();

        // When: ID ile bildirim bulunuyor
        Notification foundNotification = notificationRepository.findById(notificationId).orElse(null);

        // Then: Bildirim bulundu
        assertNotNull(foundNotification);
        assertEquals(notificationId, foundNotification.getId());
    }

    @Test
    void testFindByUserId() {
        // Given: Farklı kullanıcılar için bildirimler
        notificationRepository.save(testNotification); // testUserId
        
        UUID otherUserId = UUID.randomUUID();
        Notification notification2 = new Notification();
        notification2.setRecipient("user2@example.com");
        notification2.setType(NotificationType.EMAIL);
        notification2.setSubject("Subject 2");
        notification2.setMessage("Message 2 content for notification test");
        notification2.setUserId(otherUserId);
        notificationRepository.save(notification2);

        // When: testUserId'nin bildirimleri getiriliyor
        List<Notification> userNotifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(testUserId);

        // Then: Sadece testUserId'nin bildirimleri bulundu
        assertEquals(1, userNotifications.size());
        assertEquals(testUserId, userNotifications.get(0).getUserId());
    }

    @Test
    void testFindByType() {
        // Given: Farklı türlerde bildirimler
        notificationRepository.save(testNotification); // EMAIL
        
        Notification smsNotification = new Notification();
        smsNotification.setRecipient("5551234567");
        smsNotification.setType(NotificationType.SMS);
        smsNotification.setSubject("SMS Subject");
        smsNotification.setMessage("SMS message content for notification test");
        notificationRepository.save(smsNotification);

        // When: EMAIL türündeki bildirimler getiriliyor
        List<Notification> emailNotifications = notificationRepository.findByType(NotificationType.EMAIL);

        // Then: Sadece EMAIL bildirimleri bulundu
        assertEquals(1, emailNotifications.size());
        assertEquals(NotificationType.EMAIL, emailNotifications.get(0).getType());
    }

    @Test
    void testFindByStatus() {
        // Given: Farklı durumlarda bildirimler
        Notification notification1 = notificationRepository.save(testNotification); // PENDING
        notification1.setStatus(NotificationStatus.SENT);
        notificationRepository.save(notification1);
        
        Notification notification2 = new Notification();
        notification2.setRecipient("user2@example.com");
        notification2.setType(NotificationType.EMAIL);
        notification2.setSubject("Subject 2");
        notification2.setMessage("Message 2 content for notification test");
        notificationRepository.save(notification2); // PENDING

        // When: SENT durumundaki bildirimler getiriliyor
        List<Notification> sentNotifications = notificationRepository.findByStatus(NotificationStatus.SENT);

        // Then: Sadece SENT bildirimler bulundu
        assertEquals(1, sentNotifications.size());
        assertEquals(NotificationStatus.SENT, sentNotifications.get(0).getStatus());
    }

    @Test
    void testFindByRecipient() {
        // Given: Farklı alıcılara bildirimler
        notificationRepository.save(testNotification); // test@example.com
        
        Notification notification2 = new Notification();
        notification2.setRecipient("user2@example.com");
        notification2.setType(NotificationType.EMAIL);
        notification2.setSubject("Subject 2");
        notification2.setMessage("Message 2 content for notification test");
        notificationRepository.save(notification2);

        // When: test@example.com'a gönderilen bildirimler getiriliyor
        List<Notification> recipientNotifications = notificationRepository.findByRecipientOrderByCreatedAtDesc("test@example.com");

        // Then: Sadece test@example.com'a gönderilen bildirimler bulundu
        assertEquals(1, recipientNotifications.size());
        assertEquals("test@example.com", recipientNotifications.get(0).getRecipient());
    }

    @Test
    void testFindByUserIdAndStatus() {
        // Given: Farklı durumlarda bildirimler
        Notification notification1 = notificationRepository.save(testNotification); // PENDING
        notification1.setStatus(NotificationStatus.SENT);
        notificationRepository.save(notification1);
        
        Notification notification2 = new Notification();
        notification2.setRecipient("user2@example.com");
        notification2.setType(NotificationType.EMAIL);
        notification2.setSubject("Subject 2");
        notification2.setMessage("Message 2 content for notification test");
        notification2.setUserId(testUserId);
        notificationRepository.save(notification2); // PENDING

        // When: testUserId'nin PENDING durumundaki bildirimleri getiriliyor
        List<Notification> pendingNotifications = notificationRepository.findByUserIdAndStatusOrderByCreatedAtDesc(
            testUserId, NotificationStatus.PENDING);

        // Then: Sadece testUserId'nin PENDING bildirimleri bulundu
        assertEquals(1, pendingNotifications.size());
        assertEquals(NotificationStatus.PENDING, pendingNotifications.get(0).getStatus());
        assertEquals(testUserId, pendingNotifications.get(0).getUserId());
    }

    @Test
    void testFindByRelatedEntity() {
        // Given: Farklı entity'lere bildirimler
        notificationRepository.save(testNotification); // ORDER
        
        UUID productId = UUID.randomUUID();
        Notification notification2 = new Notification();
        notification2.setRecipient("user2@example.com");
        notification2.setType(NotificationType.EMAIL);
        notification2.setSubject("Product Notification");
        notification2.setMessage("Product notification message content");
        notification2.setRelatedEntityType("PRODUCT");
        notification2.setRelatedEntityId(productId);
        notificationRepository.save(notification2);

        // When: ORDER entity'sine ait bildirimler getiriliyor
        List<Notification> orderNotifications = notificationRepository.findByRelatedEntityTypeAndRelatedEntityIdOrderByCreatedAtDesc(
            "ORDER", testOrderId);

        // Then: Sadece ORDER entity'sine ait bildirimler bulundu
        assertEquals(1, orderNotifications.size());
        assertEquals("ORDER", orderNotifications.get(0).getRelatedEntityType());
        assertEquals(testOrderId, orderNotifications.get(0).getRelatedEntityId());
    }

    @Test
    void testNotificationStatusAutoSet() {
        // Given: testNotification hazır (status null)
        // When: Bildirim kaydediliyor
        Notification savedNotification = notificationRepository.save(testNotification);

        // Then: Status otomatik olarak PENDING olmalı
        assertEquals(NotificationStatus.PENDING, savedNotification.getStatus());
    }

    @Test
    void testNotificationTimestampAutoSet() {
        // Given: testNotification hazır
        // When: Bildirim kaydediliyor
        Notification savedNotification = notificationRepository.save(testNotification);

        // Then: Timestamp'ler otomatik set edilmeli
        assertNotNull(savedNotification.getCreatedAt());
        assertNotNull(savedNotification.getUpdatedAt());
    }
}

