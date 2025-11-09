package com.microservices.order.Client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * User Service Client
 * User Service ile iletişim kurmak için Feign Client
 * 
 * Order Service'in User Service'e ihtiyacı:
 * 1. Kullanıcı doğrulama: Sipariş veren kullanıcı gerçekten var mı?
 * 2. Adres bilgisi: Kullanıcının adres bilgilerini çek (snapshot için)
 * 3. Kullanıcı bilgisi: Sipariş detaylarında kullanıcı bilgilerini göster
 */
@FeignClient(
    name = "user-service", 
    url = "${user.service.url:}",
    fallback = UserServiceClientFallback.class  // Circuit Breaker açıldığında çağrılacak fallback
)
public interface UserServiceClient {
    
    /**
     * User ID'ye göre kullanıcı bilgisi getir
     * 
     * @param userId Kullanıcı ID'si
     * @return User bilgisi (id, username, email, address, city, zip, phone, vb.)
     * 
     * Kullanım:
     * - Sipariş oluşturulmadan önce kullanıcı doğrulama
     * - Kullanıcının adres bilgilerini çek (snapshot için)
     * - Sipariş detaylarında kullanıcı bilgilerini göster
     * 
     * Örnek Kullanım:
     * UserResponse user = userServiceClient.getUserById(userId);
     * if (user == null) {
     *     throw new ResourceNotFoundException("User not found");
     * }
     * order.setShippingAddress(user.getAddress());
     * order.setCity(user.getCity());
     * order.setZipCode(user.getZip());
     * order.setPhoneNumber(user.getPhone());
     */
    @GetMapping("/users/{userId}")
    UserResponse getUserById(@PathVariable("userId") UUID userId);
    
    /**
     * User Response DTO
     * User Service'den dönen response'u map etmek için
     * 
     * ÖNEMLİ: User Service'deki User entity'si ile aynı field'lara sahip olmalı
     * Ama burada sadece ihtiyacımız olan field'ları tutuyoruz
     */
    class UserResponse {
        private UUID id;
        private String username;
        private String email;
        private String firstName;
        private String lastName;
        private String phone;
        private String address;
        private String city;
        private String state;
        private String zip;
        
        // Getters and Setters
        public UUID getId() {
            return id;
        }
        
        public void setId(UUID id) {
            this.id = id;
        }
        
        public String getUsername() {
            return username;
        }
        
        public void setUsername(String username) {
            this.username = username;
        }
        
        public String getEmail() {
            return email;
        }
        
        public void setEmail(String email) {
            this.email = email;
        }
        
        public String getFirstName() {
            return firstName;
        }
        
        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }
        
        public String getLastName() {
            return lastName;
        }
        
        public void setLastName(String lastName) {
            this.lastName = lastName;
        }
        
        public String getPhone() {
            return phone;
        }
        
        public void setPhone(String phone) {
            this.phone = phone;
        }
        
        public String getAddress() {
            return address;
        }
        
        public void setAddress(String address) {
            this.address = address;
        }
        
        public String getCity() {
            return city;
        }
        
        public void setCity(String city) {
            this.city = city;
        }
        
        public String getState() {
            return state;
        }
        
        public void setState(String state) {
            this.state = state;
        }
        
        public String getZip() {
            return zip;
        }
        
        public void setZip(String zip) {
            this.zip = zip;
        }
    }
}

