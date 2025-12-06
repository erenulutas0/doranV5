package com.microservices.hobbygroup.dto;

import com.microservices.hobbygroup.model.GroupMembership;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MembershipResponse {
    private UUID id;
    private UUID groupId;
    private UUID userId;
    private String role;
    private LocalDateTime joinedAt;
    
    public static MembershipResponse fromEntity(GroupMembership membership) {
        return MembershipResponse.builder()
                .id(membership.getId())
                .groupId(membership.getGroupId())
                .userId(membership.getUserId())
                .role(membership.getRole() != null ? membership.getRole().name() : null)
                .joinedAt(membership.getJoinedAt())
                .build();
    }
}

