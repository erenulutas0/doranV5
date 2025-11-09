package com.microservices.notification;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.notification.Controller.NotificationController;
import com.microservices.notification.Exception.ResourceNotFoundException;
import com.microservices.notification.Model.Notification;
import com.microservices.notification.Model.NotificationStatus;
import com.microservices.notification.Model.NotificationType;
import com.microservices.notification.Service.NotificationService;

/**
 * NotificationController için Integration Test
 * @WebMvcTest: Sadece web katmanını test eder, Controller'lar için
 * MockMvc: HTTP isteklerini simüle eder
 */
@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @Autowired
    private ObjectMapper objectMapper;

    private Notification testNotification;
    private UUID testNotificationId;
    private UUID testUserId;

    @BeforeEach
    void setUp() {
        testNotificationId = UUID.randomUUID();
        testUserId = UUID.randomUUID();
        
        testNotification = new Notification();
        testNotification.setId(testNotificationId);
        testNotification.setRecipient("test@example.com");
        testNotification.setType(NotificationType.EMAIL);
        testNotification.setSubject("Test Subject");
        testNotification.setMessage("Test message content for notification");
        testNotification.setStatus(NotificationStatus.PENDING);
        testNotification.setUserId(testUserId);
    }

    @Test
    void testGetAllNotifications() throws Exception {
        // Given: Mock service davranışı
        when(notificationService.getAllNotifications()).thenReturn(List.of(testNotification));

        // When & Then: GET isteği gönderiliyor
        mockMvc.perform(get("/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testNotificationId.toString()))
                .andExpect(jsonPath("$[0].recipient").value("test@example.com"))
                .andExpect(jsonPath("$[0].type").value("EMAIL"));

        verify(notificationService, times(1)).getAllNotifications();
    }

    @Test
    void testGetNotificationById() throws Exception {
        // Given: Mock service davranışı
        when(notificationService.getNotificationById(testNotificationId)).thenReturn(testNotification);

        // When & Then: GET isteği gönderiliyor
        mockMvc.perform(get("/notifications/{id}", testNotificationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testNotificationId.toString()))
                .andExpect(jsonPath("$.recipient").value("test@example.com"));

        verify(notificationService, times(1)).getNotificationById(testNotificationId);
    }

    @Test
    void testGetNotificationByIdNotFound() throws Exception {
        // Given: Mock service exception fırlatıyor
        when(notificationService.getNotificationById(testNotificationId))
            .thenThrow(new ResourceNotFoundException("Notification", "id", testNotificationId));

        // When & Then: 404 dönmeli
        mockMvc.perform(get("/notifications/{id}", testNotificationId))
                .andExpect(status().isNotFound());

        verify(notificationService, times(1)).getNotificationById(testNotificationId);
    }

    @Test
    void testCreateNotification() throws Exception {
        // Given: Mock service davranışı
        when(notificationService.createNotification(any(Notification.class))).thenReturn(testNotification);

        // When & Then: POST isteği gönderiliyor
        mockMvc.perform(post("/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testNotification)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(testNotificationId.toString()))
                .andExpect(jsonPath("$.recipient").value("test@example.com"));

        verify(notificationService, times(1)).createNotification(any(Notification.class));
    }

    @Test
    void testUpdateNotification() throws Exception {
        // Given: Mock service davranışı
        Notification updatedNotification = new Notification();
        updatedNotification.setId(testNotificationId);
        updatedNotification.setSubject("Updated Subject");
        updatedNotification.setStatus(NotificationStatus.PENDING);
        
        when(notificationService.updateNotification(eq(testNotificationId), any(Notification.class)))
            .thenReturn(updatedNotification);

        // When & Then: PUT isteği gönderiliyor
        mockMvc.perform(put("/notifications/{id}", testNotificationId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedNotification)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subject").value("Updated Subject"));

        verify(notificationService, times(1)).updateNotification(eq(testNotificationId), any(Notification.class));
    }

    @Test
    void testDeleteNotification() throws Exception {
        // Given: Mock service davranışı
        doNothing().when(notificationService).deleteNotification(testNotificationId);

        // When & Then: DELETE isteği gönderiliyor
        mockMvc.perform(delete("/notifications/{id}", testNotificationId))
                .andExpect(status().isNoContent());

        verify(notificationService, times(1)).deleteNotification(testNotificationId);
    }

    @Test
    void testSendNotification() throws Exception {
        // Given: Mock service davranışı
        Notification sentNotification = new Notification();
        sentNotification.setId(testNotificationId);
        sentNotification.setStatus(NotificationStatus.SENT);
        
        when(notificationService.sendNotification(testNotificationId)).thenReturn(sentNotification);

        // When & Then: POST isteği gönderiliyor
        mockMvc.perform(post("/notifications/{id}/send", testNotificationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SENT"));

        verify(notificationService, times(1)).sendNotification(testNotificationId);
    }

    @Test
    void testUpdateNotificationStatus() throws Exception {
        // Given: Mock service davranışı
        Notification updatedNotification = new Notification();
        updatedNotification.setId(testNotificationId);
        updatedNotification.setStatus(NotificationStatus.DELIVERED);
        
        when(notificationService.updateNotificationStatus(testNotificationId, NotificationStatus.DELIVERED))
            .thenReturn(updatedNotification);

        // When & Then: PATCH isteği gönderiliyor
        mockMvc.perform(patch("/notifications/{id}/status", testNotificationId)
                .param("status", "DELIVERED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DELIVERED"));

        verify(notificationService, times(1)).updateNotificationStatus(testNotificationId, NotificationStatus.DELIVERED);
    }

    @Test
    void testGetNotificationsByUserId() throws Exception {
        // Given: Mock service davranışı
        when(notificationService.getNotificationsByUserId(testUserId)).thenReturn(List.of(testNotification));

        // When & Then: GET isteği gönderiliyor
        mockMvc.perform(get("/notifications/user/{userId}", testUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(testUserId.toString()));

        verify(notificationService, times(1)).getNotificationsByUserId(testUserId);
    }

    @Test
    void testGetNotificationsByType() throws Exception {
        // Given: Mock service davranışı
        when(notificationService.getNotificationsByType(NotificationType.EMAIL)).thenReturn(List.of(testNotification));

        // When & Then: GET isteği gönderiliyor
        mockMvc.perform(get("/notifications/type/EMAIL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value("EMAIL"));

        verify(notificationService, times(1)).getNotificationsByType(NotificationType.EMAIL);
    }

    @Test
    void testGetNotificationsByStatus() throws Exception {
        // Given: Mock service davranışı
        when(notificationService.getNotificationsByStatus(NotificationStatus.PENDING))
            .thenReturn(List.of(testNotification));

        // When & Then: GET isteği gönderiliyor
        mockMvc.perform(get("/notifications/status/PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("PENDING"));

        verify(notificationService, times(1)).getNotificationsByStatus(NotificationStatus.PENDING);
    }

    @Test
    void testGetPendingNotifications() throws Exception {
        // Given: Mock service davranışı
        when(notificationService.getPendingNotifications()).thenReturn(List.of(testNotification));

        // When & Then: GET isteği gönderiliyor
        mockMvc.perform(get("/notifications/pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("PENDING"));

        verify(notificationService, times(1)).getPendingNotifications();
    }
}

