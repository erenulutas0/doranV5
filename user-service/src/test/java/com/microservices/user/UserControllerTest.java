package com.microservices.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.user.Controller.UserController;
import com.microservices.user.Exception.DuplicateResourceException;
import com.microservices.user.Exception.ResourceNotFoundException;
import com.microservices.user.Model.User;
import com.microservices.user.Service.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * UserController için Integration Test
 * @WebMvcTest: Sadece web katmanını test eder, Controller'lar için
 * MockMvc: HTTP istekleri simüle eder
 */
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;  // HTTP isteklerini simüle eder

    @MockBean
    private UserService userService;  // UserService mock'lanıyor

    @Autowired
    private ObjectMapper objectMapper;  // JSON dönüşümleri için

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("Test123!@$");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setPhone("1234567890");
        testUser.setAddress("123 Test St");
        testUser.setCity("Test City");
        testUser.setState("TS");
        testUser.setZip("12345");
    }

    @Test
    void testCreateUser() throws Exception {
        // Given: Mock service davranışı
        when(userService.createUser(any(User.class))).thenReturn(testUser);

        // When & Then: POST isteği gönderiliyor
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));

        // Verify: Service metodu çağrıldı
        verify(userService, times(1)).createUser(any(User.class));
    }

    @Test
    void testCreateUserWithDuplicateEmail() throws Exception {
        // Given: Service exception fırlatıyor
        when(userService.createUser(any(User.class)))
                .thenThrow(new DuplicateResourceException("User", "email", "test@example.com"));

        // When & Then: Bad request dönmeli
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetUserById() throws Exception {
        // Given: Mock service davranışı
        UUID userId = testUser.getId();
        when(userService.getUserById(userId)).thenReturn(testUser);

        // When & Then: GET isteği gönderiliyor
        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));

        verify(userService, times(1)).getUserById(userId);
    }

    @Test
    void testGetUserByIdNotFound() throws Exception {
        // Given: Kullanıcı bulunamıyor
        UUID userId = UUID.randomUUID();
        when(userService.getUserById(userId))
                .thenThrow(new ResourceNotFoundException("User", "id", userId));

        // When & Then: 404 dönmeli
        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).getUserById(userId);
    }

    @Test
    void testUpdateUser() throws Exception {
        // Given: Mock service davranışı
        UUID userId = testUser.getId();
        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setUsername("updateduser");
        updatedUser.setEmail("updated@example.com");
        updatedUser.setPassword("NewPass123!@#");
        updatedUser.setFirstName("Updated");
        updatedUser.setLastName("Name");
        updatedUser.setPhone("9999999999");
        updatedUser.setAddress("999 Updated St");
        updatedUser.setCity("Updated City");
        updatedUser.setState("UC");
        updatedUser.setZip("99999");

        when(userService.updateUser(eq(userId), any(User.class))).thenReturn(updatedUser);

        // When & Then: PUT isteği gönderiliyor
        mockMvc.perform(put("/users/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("updateduser"))
                .andExpect(jsonPath("$.email").value("updated@example.com"));

        verify(userService, times(1)).updateUser(eq(userId), any(User.class));
    }

    @Test
    void testDeleteUser() throws Exception {
        // Given: Mock service davranışı
        UUID userId = testUser.getId();
        doNothing().when(userService).deleteUser(userId);

        // When & Then: DELETE isteği gönderiliyor
        mockMvc.perform(delete("/users/{id}", userId))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(userId);
    }

    @Test
    void testDeleteUserNotFound() throws Exception {
        // Given: Kullanıcı bulunamıyor
        UUID userId = UUID.randomUUID();
        doThrow(new ResourceNotFoundException("User", "id", userId))
                .when(userService).deleteUser(userId);

        // When & Then: 404 dönmeli
        mockMvc.perform(delete("/users/{id}", userId))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).deleteUser(userId);
    }
}

