package com.microservices.hobbygroup.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * GroupMembership Entity
 * Kullanıcıların gruplara üyeliğini temsil eder
 */
@Entity
@Table(name = "group_memberships", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"group_id", "user_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupMembership {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        joinedAt = LocalDateTime.now();
        if (role == null) {
            role = MembershipRole.MEMBER;
        }
    }
    
    /**
     * Grup ID
     */
    @NotNull(message = "Group ID is required")
    @Column(nullable = false)
    private UUID groupId;
    
    /**
     * Kullanıcı ID
     */
    @NotNull(message = "User ID is required")
    @Column(nullable = false)
    private UUID userId;
    
    /**
     * Üyelik Rolü
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MembershipRole role;
    
    /**
     * Katılma Tarihi
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime joinedAt;
    
    /**
     * Ayrılma Tarihi (null ise hala üye)
     */
    @Column
    private LocalDateTime leftAt;
    
    /**
     * Üyelik Rolü Enum
     */
    public enum MembershipRole {
        CREATOR,    // Grup kurucusu
        ADMIN,      // Yönetici
        MODERATOR,  // Moderatör
        MEMBER      // Normal üye
    }
}

