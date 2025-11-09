package com.microservices.notification.Controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.microservices.notification.Model.Notification;
import com.microservices.notification.Model.NotificationStatus;
import com.microservices.notification.Model.NotificationType;
import com.microservices.notification.Service.NotificationService;

/**
 * Notification Controller
 * 
 * REST API endpoints for notification management
 * 
 * Endpoints:
 * - GET /notifications - Tüm bildirimleri getir
 * - GET /notifications/{id} - ID'ye göre bildirim getir
 * - POST /notifications - Yeni bildirim oluştur
 * - PUT /notifications/{id} - Bildirim güncelle
 * - DELETE /notifications/{id} - Bildirim sil
 * - POST /notifications/{id}/send - Bildirim gönder
 * - PATCH /notifications/{id}/status - Bildirim durumu güncelle
 * - GET /notifications/user/{userId} - Kullanıcıya göre bildirimleri getir
 * - GET /notifications/type/{type} - Türe göre bildirimleri getir
 * - GET /notifications/status/{status} - Duruma göre bildirimleri getir
 * - GET /notifications/recipient/{recipient} - Alıcıya göre bildirimleri getir
 * - GET /notifications/pending - PENDING durumundaki bildirimleri getir
 */
@RestController
@RequestMapping("/notifications")  // Gateway zaten /api/notifications/** alıyor, burada sadece /notifications
public class NotificationController {
    
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * Tüm bildirimleri getir
     * GET /notifications
     */
    @GetMapping
    public ResponseEntity<List<Notification>> getAllNotifications() {
        List<Notification> notifications = notificationService.getAllNotifications();
        return ResponseEntity.ok(notifications);
    }

    /**
     * ID'ye göre bildirim getir
     * GET /notifications/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Notification> getNotificationById(@PathVariable("id") UUID id) {
        Notification notification = notificationService.getNotificationById(id);
        return ResponseEntity.ok(notification);
    }

    /**
     * Yeni bildirim oluştur
     * POST /notifications
     */
    @PostMapping
    public ResponseEntity<Notification> createNotification(@RequestBody Notification notification) {
        Notification createdNotification = notificationService.createNotification(notification);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdNotification);
    }

    /**
     * Bildirim güncelle
     * PUT /notifications/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Notification> updateNotification(
            @PathVariable("id") UUID id,
            @RequestBody Notification notification) {
        Notification updatedNotification = notificationService.updateNotification(id, notification);
        return ResponseEntity.ok(updatedNotification);
    }

    /**
     * Bildirim sil
     * DELETE /notifications/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable("id") UUID id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Bildirim gönder
     * POST /notifications/{id}/send
     */
    @PostMapping("/{id}/send")
    public ResponseEntity<Notification> sendNotification(@PathVariable("id") UUID id) {
        Notification sentNotification = notificationService.sendNotification(id);
        return ResponseEntity.ok(sentNotification);
    }

    /**
     * Bildirim durumu güncelle
     * PATCH /notifications/{id}/status?status=SENT
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<Notification> updateNotificationStatus(
            @PathVariable("id") UUID id,
            @RequestParam("status") NotificationStatus status) {
        Notification updatedNotification = notificationService.updateNotificationStatus(id, status);
        return ResponseEntity.ok(updatedNotification);
    }

    /**
     * Kullanıcıya göre bildirimleri getir
     * GET /notifications/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getNotificationsByUserId(@PathVariable("userId") UUID userId) {
        List<Notification> notifications = notificationService.getNotificationsByUserId(userId);
        return ResponseEntity.ok(notifications);
    }

    /**
     * Türe göre bildirimleri getir
     * GET /notifications/type/{type}
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<List<Notification>> getNotificationsByType(@PathVariable("type") NotificationType type) {
        List<Notification> notifications = notificationService.getNotificationsByType(type);
        return ResponseEntity.ok(notifications);
    }

    /**
     * Duruma göre bildirimleri getir
     * GET /notifications/status/{status}
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Notification>> getNotificationsByStatus(@PathVariable("status") NotificationStatus status) {
        List<Notification> notifications = notificationService.getNotificationsByStatus(status);
        return ResponseEntity.ok(notifications);
    }

    /**
     * Alıcıya göre bildirimleri getir
     * GET /notifications/recipient/{recipient}
     */
    @GetMapping("/recipient/{recipient}")
    public ResponseEntity<List<Notification>> getNotificationsByRecipient(@PathVariable("recipient") String recipient) {
        List<Notification> notifications = notificationService.getNotificationsByRecipient(recipient);
        return ResponseEntity.ok(notifications);
    }

    /**
     * Kullanıcının belirli durumdaki bildirimlerini getir
     * GET /notifications/user/{userId}/status/{status}
     */
    @GetMapping("/user/{userId}/status/{status}")
    public ResponseEntity<List<Notification>> getNotificationsByUserIdAndStatus(
            @PathVariable("userId") UUID userId,
            @PathVariable("status") NotificationStatus status) {
        List<Notification> notifications = notificationService.getNotificationsByUserIdAndStatus(userId, status);
        return ResponseEntity.ok(notifications);
    }

    /**
     * İlgili entity'ye göre bildirimleri getir
     * GET /notifications/related?entityType=ORDER&entityId={id}
     */
    @GetMapping("/related")
    public ResponseEntity<List<Notification>> getNotificationsByRelatedEntity(
            @RequestParam("entityType") String entityType,
            @RequestParam("entityId") UUID entityId) {
        List<Notification> notifications = notificationService.getNotificationsByRelatedEntity(entityType, entityId);
        return ResponseEntity.ok(notifications);
    }

    /**
     * PENDING durumundaki bildirimleri getir
     * GET /notifications/pending
     */
    @GetMapping("/pending")
    public ResponseEntity<List<Notification>> getPendingNotifications() {
        List<Notification> notifications = notificationService.getPendingNotifications();
        return ResponseEntity.ok(notifications);
    }
}

