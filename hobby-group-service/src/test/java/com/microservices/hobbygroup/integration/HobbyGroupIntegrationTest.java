package com.microservices.hobbygroup.integration;

import com.microservices.hobbygroup.dto.HobbyGroupRequest;
import com.microservices.hobbygroup.dto.HobbyGroupResponse;
import com.microservices.hobbygroup.dto.MembershipResponse;
import com.microservices.hobbygroup.model.GroupMembership;
import com.microservices.hobbygroup.model.HobbyGroup;
import com.microservices.hobbygroup.repository.GroupMembershipRepository;
import com.microservices.hobbygroup.repository.HobbyGroupRepository;
import com.microservices.hobbygroup.service.HobbyGroupService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class HobbyGroupIntegrationTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");
    
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
    
    @Autowired
    private HobbyGroupService service;
    
    @Autowired
    private HobbyGroupRepository groupRepository;
    
    @Autowired
    private GroupMembershipRepository membershipRepository;
    
    private UUID testCreatorId;
    private UUID testUserId;
    
    @BeforeEach
    void setUp() {
        testCreatorId = UUID.randomUUID();
        testUserId = UUID.randomUUID();
        groupRepository.deleteAll();
        membershipRepository.deleteAll();
    }
    
    @Test
    void shouldCreateGroupAndAddCreatorAsMember() {
        // Given
        HobbyGroupRequest request = new HobbyGroupRequest();
        request.setName("Integration Test Group");
        request.setDescription("Integration test description");
        request.setCategory("Photography");
        request.setLocation("Istanbul");
        request.setMaxMembers(50);
        
        // When
        HobbyGroupResponse created = service.createGroup(testCreatorId, request);
        
        // Then
        assertThat(created.getId()).isNotNull();
        assertThat(created.getName()).isEqualTo("Integration Test Group");
        assertThat(created.getMemberCount()).isEqualTo(1);
        
        // Verify creator is added as member
        List<MembershipResponse> members = service.getGroupMembers(created.getId());
        assertThat(members).hasSize(1);
        assertThat(members.get(0).getUserId()).isEqualTo(testCreatorId);
        assertThat(members.get(0).getRole()).isEqualTo("CREATOR");
    }
    
    @Test
    void shouldJoinAndLeaveGroup() {
        // Given
        HobbyGroupRequest request = new HobbyGroupRequest();
        request.setName("Test Group");
        request.setDescription("Description");
        request.setCategory("Music");
        
        HobbyGroupResponse group = service.createGroup(testCreatorId, request);
        
        // When - Join
        MembershipResponse membership = service.joinGroup(testUserId, group.getId());
        
        // Then
        assertThat(membership.getUserId()).isEqualTo(testUserId);
        assertThat(membership.getGroupId()).isEqualTo(group.getId());
        
        HobbyGroupResponse updatedGroup = service.getGroupById(group.getId());
        assertThat(updatedGroup.getMemberCount()).isEqualTo(2);
        
        // When - Leave
        service.leaveGroup(testUserId, group.getId());
        
        // Then
        HobbyGroupResponse finalGroup = service.getGroupById(group.getId());
        assertThat(finalGroup.getMemberCount()).isEqualTo(1);
    }
    
    @Test
    void shouldEnforceMaxMembersLimit() {
        // Given
        HobbyGroupRequest request = new HobbyGroupRequest();
        request.setName("Limited Group");
        request.setDescription("Description");
        request.setCategory("Sports");
        request.setMaxMembers(2); // Creator + 1 more = 2 total
        
        HobbyGroupResponse group = service.createGroup(testCreatorId, request);
        
        UUID user1 = UUID.randomUUID();
        UUID user2 = UUID.randomUUID();
        
        // When - Creator already counts as 1, so only 1 more can join
        service.joinGroup(user1, group.getId());
        
        // Then - Should fail when trying to add second user (maxMembers = 2, already has creator + user1)
        assertThatThrownBy(() -> service.joinGroup(user2, group.getId()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("full");
    }
    
    @Test
    void shouldFilterGroupsByCategory() {
        // Given
        HobbyGroupRequest photoGroup = new HobbyGroupRequest();
        photoGroup.setName("Photo Group");
        photoGroup.setDescription("Description");
        photoGroup.setCategory("Photography");
        service.createGroup(testCreatorId, photoGroup);
        
        UUID creator2 = UUID.randomUUID();
        HobbyGroupRequest musicGroup = new HobbyGroupRequest();
        musicGroup.setName("Music Group");
        musicGroup.setDescription("Description");
        musicGroup.setCategory("Music");
        service.createGroup(creator2, musicGroup);
        
        // When
        Pageable pageable = PageRequest.of(0, 20);
        Page<HobbyGroupResponse> photoGroups = service.getActiveGroupsByCategory("Photography", pageable);
        
        // Then
        assertThat(photoGroups.getContent()).isNotEmpty();
        assertThat(photoGroups.getContent().stream()
                .allMatch(g -> g.getCategory().equals("Photography"))).isTrue();
    }
    
    @Test
    void shouldSearchGroups() {
        // Given
        HobbyGroupRequest request1 = new HobbyGroupRequest();
        request1.setName("Java Developers");
        request1.setDescription("Java programming group");
        request1.setCategory("Programming");
        service.createGroup(testCreatorId, request1);
        
        UUID creator2 = UUID.randomUUID();
        HobbyGroupRequest request2 = new HobbyGroupRequest();
        request2.setName("Python Developers");
        request2.setDescription("Python programming group");
        request2.setCategory("Programming");
        service.createGroup(creator2, request2);
        
        // When
        Pageable pageable = PageRequest.of(0, 20);
        Page<HobbyGroupResponse> javaGroups = service.searchActiveGroups("Java", pageable);
        
        // Then
        assertThat(javaGroups.getContent()).isNotEmpty();
        assertThat(javaGroups.getContent().stream()
                .anyMatch(g -> g.getName().contains("Java"))).isTrue();
    }
}

