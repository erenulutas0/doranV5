package com.microservices.hobbygroup.service;

import com.microservices.hobbygroup.dto.HobbyGroupRequest;
import com.microservices.hobbygroup.dto.HobbyGroupResponse;
import com.microservices.hobbygroup.model.GroupMembership;
import com.microservices.hobbygroup.model.HobbyGroup;
import com.microservices.hobbygroup.repository.GroupMembershipRepository;
import com.microservices.hobbygroup.repository.HobbyGroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HobbyGroupServiceTest {
    
    @Mock
    private HobbyGroupRepository groupRepository;
    
    @Mock
    private GroupMembershipRepository membershipRepository;
    
    private HobbyGroupService service;
    
    private UUID testCreatorId;
    private UUID testGroupId;
    private HobbyGroup testGroup;
    
    @BeforeEach
    void setUp() {
        service = new HobbyGroupService(groupRepository, membershipRepository);
        testCreatorId = UUID.randomUUID();
        testGroupId = UUID.randomUUID();
        
        testGroup = new HobbyGroup();
        testGroup.setId(testGroupId);
        testGroup.setCreatorId(testCreatorId);
        testGroup.setName("Test Group");
        testGroup.setDescription("Test Description");
        testGroup.setCategory("Photography");
        testGroup.setStatus(HobbyGroup.GroupStatus.ACTIVE);
        testGroup.setIsActive(true);
        testGroup.setMemberCount(5);
        testGroup.setCreatedAt(LocalDateTime.now());
        testGroup.setUpdatedAt(LocalDateTime.now());
    }
    
    @Test
    void shouldCreateGroup() {
        // Given
        HobbyGroupRequest request = new HobbyGroupRequest();
        request.setName("New Group");
        request.setDescription("New Description");
        request.setCategory("Music");
        request.setLocation("Istanbul");
        
        when(groupRepository.save(any(HobbyGroup.class))).thenAnswer(invocation -> {
            HobbyGroup group = invocation.getArgument(0);
            group.setId(UUID.randomUUID());
            group.setCreatedAt(LocalDateTime.now());
            group.setUpdatedAt(LocalDateTime.now());
            group.setMemberCount(0);
            return group;
        });
        when(membershipRepository.save(any(GroupMembership.class))).thenAnswer(invocation -> {
            GroupMembership membership = invocation.getArgument(0);
            membership.setId(UUID.randomUUID());
            membership.setJoinedAt(LocalDateTime.now());
            return membership;
        });
        
        // When
        HobbyGroupResponse response = service.createGroup(testCreatorId, request);
        
        // Then
        ArgumentCaptor<HobbyGroup> groupCaptor = ArgumentCaptor.forClass(HobbyGroup.class);
        verify(groupRepository, times(2)).save(groupCaptor.capture());
        
        HobbyGroup savedGroup = groupCaptor.getAllValues().get(0);
        assertThat(savedGroup.getCreatorId()).isEqualTo(testCreatorId);
        assertThat(savedGroup.getName()).isEqualTo("New Group");
        assertThat(savedGroup.getCategory()).isEqualTo("Music");
        assertThat(savedGroup.getStatus()).isEqualTo(HobbyGroup.GroupStatus.ACTIVE);
        
        verify(membershipRepository).save(any(GroupMembership.class));
        assertThat(response).isNotNull();
    }
    
    @Test
    void shouldJoinGroup() {
        // Given
        UUID userId = UUID.randomUUID();
        when(groupRepository.findByIdAndDeletedAtIsNull(testGroupId)).thenReturn(Optional.of(testGroup));
        when(membershipRepository.findActiveMembership(testGroupId, userId)).thenReturn(Optional.empty());
        when(membershipRepository.save(any(GroupMembership.class))).thenAnswer(invocation -> {
            GroupMembership membership = invocation.getArgument(0);
            membership.setId(UUID.randomUUID());
            membership.setJoinedAt(LocalDateTime.now());
            return membership;
        });
        when(groupRepository.save(any(HobbyGroup.class))).thenReturn(testGroup);
        
        // When
        service.joinGroup(userId, testGroupId);
        
        // Then
        verify(membershipRepository).save(any(GroupMembership.class));
        verify(groupRepository).save(any(HobbyGroup.class));
    }
    
    @Test
    void shouldThrowExceptionWhenJoiningFullGroup() {
        // Given
        UUID userId = UUID.randomUUID();
        testGroup.setMaxMembers(10);
        testGroup.setMemberCount(10);
        
        when(groupRepository.findByIdAndDeletedAtIsNull(testGroupId)).thenReturn(Optional.of(testGroup));
        when(membershipRepository.findActiveMembership(testGroupId, userId)).thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> service.joinGroup(userId, testGroupId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("full");
    }
    
    @Test
    void shouldLeaveGroup() {
        // Given
        UUID userId = UUID.randomUUID();
        GroupMembership membership = new GroupMembership();
        membership.setId(UUID.randomUUID());
        membership.setGroupId(testGroupId);
        membership.setUserId(userId);
        membership.setRole(GroupMembership.MembershipRole.MEMBER);
        
        when(membershipRepository.findActiveMembership(testGroupId, userId)).thenReturn(Optional.of(membership));
        when(groupRepository.findById(testGroupId)).thenReturn(Optional.of(testGroup));
        when(membershipRepository.save(any(GroupMembership.class))).thenReturn(membership);
        when(groupRepository.save(any(HobbyGroup.class))).thenReturn(testGroup);
        
        // When
        service.leaveGroup(userId, testGroupId);
        
        // Then
        ArgumentCaptor<GroupMembership> captor = ArgumentCaptor.forClass(GroupMembership.class);
        verify(membershipRepository).save(captor.capture());
        assertThat(captor.getValue().getLeftAt()).isNotNull();
    }
    
    @Test
    void shouldThrowExceptionWhenCreatorLeaves() {
        // Given
        GroupMembership membership = new GroupMembership();
        membership.setRole(GroupMembership.MembershipRole.CREATOR);
        
        when(membershipRepository.findActiveMembership(testGroupId, testCreatorId)).thenReturn(Optional.of(membership));
        
        // When & Then
        assertThatThrownBy(() -> service.leaveGroup(testCreatorId, testGroupId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Creator");
    }
}

