package com.microservices.user;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.microservices.user.Exception.DuplicateResourceException;
import com.microservices.user.Exception.ResourceNotFoundException;
import com.microservices.user.Model.User;
import com.microservices.user.Repository.UserRepository;
import com.microservices.user.Service.UserService;

/**
 * UserService için Unit Test
 * @DataJpaTest: Sadece JPA katmanını test eder, veritabanı işlemleri için
 */
@DataJpaTest
@Import(UserService.class)  // UserService'i test context'ine ekle
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        // Her test öncesi çalışır
        testUser = new User();
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
    void testCreateUser() {
        // Given: testUser hazır
        // When: Kullanıcı oluşturuluyor
        User createdUser = userService.createUser(testUser);

        // Then: Kullanıcı başarıyla oluşturuldu
        assertNotNull(createdUser.getId());
        assertEquals("testuser", createdUser.getUsername());
        assertEquals("test@example.com", createdUser.getEmail());
    }

    @Test
    void testCreateUserWithDuplicateEmail() {
        // Given: Bir kullanıcı zaten var
        userService.createUser(testUser);

        // When: Aynı email ile yeni kullanıcı oluşturulmaya çalışılıyor
        User duplicateUser = new User();
        duplicateUser.setUsername("anotheruser");
        duplicateUser.setEmail("test@example.com");  // Aynı email
        duplicateUser.setPassword("Test123!@$");
        duplicateUser.setFirstName("Another");
        duplicateUser.setLastName("User");
        duplicateUser.setPhone("0987654321");
        duplicateUser.setAddress("456 Another St");
        duplicateUser.setCity("Another City");
        duplicateUser.setState("AS");
        duplicateUser.setZip("54321");

        // Then: Exception fırlatılmalı
        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class, () -> {
            userService.createUser(duplicateUser);
        });
        assertTrue(exception.getMessage().contains("Email already exists"));
    }

    @Test
    void testCreateUserWithDuplicateUsername() {
        // Given: Bir kullanıcı zaten var
        userService.createUser(testUser);

        // When: Aynı username ile yeni kullanıcı oluşturulmaya çalışılıyor
        User duplicateUser = new User();
        duplicateUser.setUsername("testuser");  // Aynı username
        duplicateUser.setEmail("another@example.com");
        duplicateUser.setPassword("Test123!@$");
        duplicateUser.setFirstName("Another");
        duplicateUser.setLastName("User");
        duplicateUser.setPhone("0987654321");
        duplicateUser.setAddress("456 Another St");
        duplicateUser.setCity("Another City");
        duplicateUser.setState("AS");
        duplicateUser.setZip("54321");

        // Then: Exception fırlatılmalı
        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class, () -> {
            userService.createUser(duplicateUser);
        });
        assertTrue(exception.getMessage().contains("User already exists"));
    }

    @Test
    void testGetAllUsers() {
        // Given: Birkaç kullanıcı oluşturuluyor
        userService.createUser(testUser);
        
        User user2 = new User();
        user2.setUsername("user2");
        user2.setEmail("user2@example.com");
        user2.setPassword("Test123!@$");
        user2.setFirstName("User");
        user2.setLastName("Two");
        user2.setPhone("1111111111");
        user2.setAddress("789 Second St");
        user2.setCity("Second City");
        user2.setState("SC");
        user2.setZip("22222");
        userService.createUser(user2);

        // When: Tüm kullanıcılar getiriliyor
        List<User> users = userService.getAllUsers();

        // Then: 2 kullanıcı olmalı
        assertEquals(2, users.size());
    }

    @Test
    void testGetUserById() {
        // Given: Bir kullanıcı oluşturuluyor
        User createdUser = userService.createUser(testUser);
        UUID userId = createdUser.getId();

        // When: ID ile kullanıcı getiriliyor
        User foundUser = userService.getUserById(userId);

        // Then: Doğru kullanıcı bulundu
        assertNotNull(foundUser);
        assertEquals(userId, foundUser.getId());
        assertEquals("testuser", foundUser.getUsername());
    }

    @Test
    void testGetUserByIdNotFound() {
        // Given: Var olmayan bir ID
        UUID nonExistentId = UUID.randomUUID();

        // When & Then: Exception fırlatılmalı
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            userService.getUserById(nonExistentId);
        });
        assertTrue(exception.getMessage().contains("User not found"));
    }

    @Test
    void testUpdateUser() {
        // Given: Bir kullanıcı oluşturuluyor
        User createdUser = userService.createUser(testUser);
        UUID userId = createdUser.getId();

        // When: Kullanıcı güncelleniyor
        User updateData = new User();
        updateData.setUsername("updateduser");
        updateData.setEmail("updated@example.com");
        updateData.setPassword("NewPass123!@$");
        updateData.setFirstName("Updated");
        updateData.setLastName("Name");
        updateData.setPhone("9999999999");
        updateData.setAddress("999 Updated St");
        updateData.setCity("Updated City");
        updateData.setState("UC");
        updateData.setZip("99999");

        User updatedUser = userService.updateUser(userId, updateData);

        // Then: Kullanıcı güncellendi
        assertEquals(userId, updatedUser.getId());
        assertEquals("updateduser", updatedUser.getUsername());
        assertEquals("updated@example.com", updatedUser.getEmail());
        assertEquals("Updated", updatedUser.getFirstName());
    }

    @Test
    void testDeleteUser() {
        // Given: Bir kullanıcı oluşturuluyor
        User createdUser = userService.createUser(testUser);
        UUID userId = createdUser.getId();

        // When: Kullanıcı siliniyor
        userService.deleteUser(userId);

        // Then: Kullanıcı artık bulunamaz
        assertFalse(userRepository.existsById(userId));
    }

    @Test
    void testDeleteUserNotFound() {
        // Given: Var olmayan bir ID
        UUID nonExistentId = UUID.randomUUID();

        // When & Then: Exception fırlatılmalı
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            userService.deleteUser(nonExistentId);
        });
        assertTrue(exception.getMessage().contains("User not found"));
    }
}

