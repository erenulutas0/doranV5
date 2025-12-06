package com.microservices.hobbygroup.dto;

import com.microservices.hobbygroup.model.HobbyGroup;
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
public class HobbyGroupResponse {
    private UUID id;
    private UUID creatorId;
    private String name;
    private String description;
    private String category;
    private String location;
    private String rules;
    private String tags;
    private UUID imageId;
    private String imageUrl;
    private Integer memberCount;
    private Integer maxMembers;
    private String status;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public static HobbyGroupResponse fromEntity(HobbyGroup group) {
        return HobbyGroupResponse.builder()
                .id(group.getId())
                .creatorId(group.getCreatorId())
                .name(group.getName())
                .description(group.getDescription())
                .category(group.getCategory())
                .location(group.getLocation())
                .rules(group.getRules())
                .tags(group.getTags())
                .imageId(group.getImageId())
                .memberCount(group.getMemberCount())
                .maxMembers(group.getMaxMembers())
                .status(group.getStatus() != null ? group.getStatus().name() : null)
                .isActive(group.getIsActive())
                .createdAt(group.getCreatedAt())
                .updatedAt(group.getUpdatedAt())
                .build();
    }
}

