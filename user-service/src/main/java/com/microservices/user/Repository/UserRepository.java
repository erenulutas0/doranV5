package com.microservices.user.Repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.microservices.user.Model.User;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    // Email'e göre kullanıcı bulma (unique kontrol için)
    Optional<User> findByEmail(String email);
    
    // Username'e göre kullanıcı bulma (unique kontrol için)
    Optional<User> findByUsername(String username);
}
