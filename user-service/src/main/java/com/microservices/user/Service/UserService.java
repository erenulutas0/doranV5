package com.microservices.user.Service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.microservices.user.Exception.DuplicateResourceException;
import com.microservices.user.Exception.ResourceNotFoundException;
import com.microservices.user.Model.User;
import com.microservices.user.Repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    @Transactional
    public User createUser(User user) {
        // Email unique kontrolü
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new DuplicateResourceException("User", "email", user.getEmail());
        }
        
        // Username unique kontrolü
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new DuplicateResourceException("User", "username", user.getUsername());
        }
        
        return userRepository.save(user);
    }

    @Transactional
    public User updateUser(UUID id, User userDetails) {
        User user = getUserById(id);
        
        // Email değişiyorsa unique kontrolü yap
        if (!user.getEmail().equals(userDetails.getEmail())) {
            if (userRepository.findByEmail(userDetails.getEmail()).isPresent()) {
                throw new DuplicateResourceException("User", "email", userDetails.getEmail());
            }
        }
        
        // Username değişiyorsa unique kontrolü yap
        if (!user.getUsername().equals(userDetails.getUsername())) {
            if (userRepository.findByUsername(userDetails.getUsername()).isPresent()) {
                throw new DuplicateResourceException("User", "username", userDetails.getUsername());
            }
        }
        
        // Güncelleme
        user.setUsername(userDetails.getUsername());
        user.setEmail(userDetails.getEmail());
        user.setPassword(userDetails.getPassword());
        user.setFirstName(userDetails.getFirstName());
        user.setLastName(userDetails.getLastName());
        user.setPhone(userDetails.getPhone());
        user.setAddress(userDetails.getAddress());
        user.setCity(userDetails.getCity());
        user.setState(userDetails.getState());
        user.setZip(userDetails.getZip());
        
        return userRepository.save(user);
    }

    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", "id", id);
        }
        userRepository.deleteById(id);
    }
}
