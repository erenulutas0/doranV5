package com.microservices.notification.Repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.microservices.notification.Model.Notification;
import com.microservices.notification.Model.NotificationStatus;
import com.microservices.notification.Model.NotificationType;

/**
 * Notification Repository
 * 
 * Notification entity'si için JPA repository
 * 
 * Spring Data JPA otomatik olarak şu method'ları sağlar:
 * - save(Notification): Kaydet/güncelle
 * - findById(UUID): ID'ye göre bul
 * - findAll(): Tümünü getir
 * - deleteById(UUID): ID'ye göre sil
 * - existsById(UUID): ID var mı kontrol et
 * 
 * Custom query'ler:
 * - findByUserId: Kullanıcıya göre bildirimleri getir
 * - findByType: Bildirim türüne göre filtrele
 * - findByStatus: Duruma göre filtrele
 * - findByRecipient: Alıcıya göre filtrele
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    
    /**
     * User ID'ye göre bildirimleri getir
     * 
     * @param userId Kullanıcı ID'si
     * @return Kullanıcının bildirimleri (en yeni önce)
     */
    List<Notification> findByUserIdOrderByCreatedAtDesc(UUID userId);
    
    /**
     * Bildirim türüne göre filtrele
     * 
     * @param type Bildirim türü (EMAIL, SMS, PUSH, IN_APP)
     * @return Belirtilen türdeki bildirimler
     */
    List<Notification> findByType(NotificationType type);
    
    /**
     * Duruma göre filtrele
     * 
     * @param status Bildirim durumu (PENDING, SENT, FAILED, vb.)
     * @return Belirtilen durumdaki bildirimler
     */
    List<Notification> findByStatus(NotificationStatus status);
    
    /**
     * Alıcıya göre filtrele
     * 
     * @param recipient Alıcı (e-posta, telefon, user ID)
     * @return Belirtilen alıcıya gönderilen bildirimler
     */
    List<Notification> findByRecipientOrderByCreatedAtDesc(String recipient);
    
    /**
     * Kullanıcı ID ve duruma göre filtrele
     * 
     * @param userId Kullanıcı ID'si
     * @param status Bildirim durumu
     * @return Kullanıcının belirtilen durumdaki bildirimleri
     */
    List<Notification> findByUserIdAndStatusOrderByCreatedAtDesc(UUID userId, NotificationStatus status);
    
    /**
     * İlgili entity'ye göre filtrele
     * 
     * @param relatedEntityType Entity türü (ORDER, PRODUCT, vb.)
     * @param relatedEntityId Entity ID'si
     * @return İlgili entity'ye ait bildirimler
     */
    List<Notification> findByRelatedEntityTypeAndRelatedEntityIdOrderByCreatedAtDesc(
        String relatedEntityType, 
        UUID relatedEntityId
    );
    
    /**
     * PENDING durumundaki bildirimleri getir
     * Gönderilmeyi bekleyen bildirimler için kullanılır
     * 
     * @return PENDING durumundaki bildirimler
     */
    List<Notification> findByStatusOrderByCreatedAtAsc(NotificationStatus status);
}

