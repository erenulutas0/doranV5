package com.microservices.user.Model;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"email"}),
    @UniqueConstraint(columnNames = {"username"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    private UUID id;
    
    // Entity veritabanına kaydedilmeden önce UUID oluştur
    // Eğer ID null ise yeni bir UUID oluştur
    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = UUID.randomUUID();
        }
    }
    @NotNull 
    @Size(min = 3, max = 100)
    @Column(name = "username", nullable = false, unique = true, length = 100)
    private String username;
    
    @NotNull 
    @Email 
    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;
    
    @NotNull 
    @Size(min = 8, max = 100)  
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z\\d]).{8,}$", 
            message = "Password must contain at least 8 characters with uppercase, lowercase, digit, and special character")
    @Column(name = "password", nullable = false, length = 100)
    private String password;
    
    @NotBlank 
    @Size(min = 2, max = 50)
    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;
    
    @NotBlank 
    @Size(min = 2, max = 50)
    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;
    
    @NotBlank 
    @Pattern(regexp = "^\\d{10}$")
    @Column(name = "phone", nullable = false, length = 10)
    private String phone;
    
    @NotBlank 
    @Size(min = 5, max = 100)
    @Column(name = "address", nullable = false, length = 100)
    private String address;
    
    @NotBlank 
    @Size(min = 2, max = 50)
    @Column(name = "city", nullable = false, length = 50)
    private String city;
    
    @NotBlank 
    @Size(min = 2, max = 5)
    @Column(name = "state", nullable = false, length = 5)
    private String state;
    
    @NotBlank 
    @Size(min = 5, max = 5)
    @Column(name = "zip", nullable = false, length = 5)
    private String zip;
}
