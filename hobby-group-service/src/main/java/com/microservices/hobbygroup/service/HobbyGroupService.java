package com.microservices.hobbygroup.service;

import com.microservices.hobbygroup.dto.HobbyGroupRequest;
import com.microservices.hobbygroup.dto.HobbyGroupResponse;
import com.microservices.hobbygroup.dto.MembershipResponse;
import com.microservices.hobbygroup.model.GroupMembership;
import com.microservices.hobbygroup.model.HobbyGroup;
import com.microservices.hobbygroup.repository.GroupMembershipRepository;
import com.microservices.hobbygroup.repository.HobbyGroupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HobbyGroupService {
    
    private final HobbyGroupRepository groupRepository;
    private final GroupMembershipRepository membershipRepository;
    
    /**
     * Yeni hobi grubu oluştur
     */
    @Transactional
    public HobbyGroupResponse createGroup(UUID creatorId, HobbyGroupRequest request) {
        log.info("Creating hobby group for creator: {}", creatorId);
        
        HobbyGroup group = new HobbyGroup();
        group.setCreatorId(creatorId);
        group.setName(request.getName());
        group.setDescription(request.getDescription());
        group.setCategory(request.getCategory());
        group.setLocation(request.getLocation());
        group.setRules(request.getRules());
        group.setTags(request.getTags());
        group.setImageId(request.getImageId());
        group.setMaxMembers(request.getMaxMembers());
        group.setStatus(HobbyGroup.GroupStatus.ACTIVE);
        group.setIsActive(true);
        group.setMemberCount(0);
        
        group = groupRepository.save(group);
        
        // Creator'ı otomatik olarak CREATOR rolüyle ekle
        GroupMembership creatorMembership = new GroupMembership();
        creatorMembership.setGroupId(group.getId());
        creatorMembership.setUserId(creatorId);
        creatorMembership.setRole(GroupMembership.MembershipRole.CREATOR);
        membershipRepository.save(creatorMembership);
        
        // Member count'u güncelle
        group.setMemberCount(1);
        group = groupRepository.save(group);
        
        log.info("Hobby group created successfully: {}", group.getId());
        return HobbyGroupResponse.fromEntity(group);
    }
    
    /**
     * Grubu güncelle (sadece creator güncelleyebilir)
     */
    @Transactional
    public HobbyGroupResponse updateGroup(UUID creatorId, UUID groupId, HobbyGroupRequest request) {
        log.info("Updating group {} for creator: {}", groupId, creatorId);
        
        HobbyGroup group = groupRepository.findByIdAndCreatorIdAndDeletedAtIsNull(groupId, creatorId)
                .orElseThrow(() -> new RuntimeException("Group not found or you don't have permission"));
        
        group.setName(request.getName());
        group.setDescription(request.getDescription());
        group.setCategory(request.getCategory());
        group.setLocation(request.getLocation());
        group.setRules(request.getRules());
        group.setTags(request.getTags());
        group.setImageId(request.getImageId());
        group.setMaxMembers(request.getMaxMembers());
        
        group = groupRepository.save(group);
        log.info("Group updated successfully: {}", groupId);
        
        return HobbyGroupResponse.fromEntity(group);
    }
    
    /**
     * Grubu sil (soft delete)
     */
    @Transactional
    public void deleteGroup(UUID creatorId, UUID groupId) {
        log.info("Deleting group {} for creator: {}", groupId, creatorId);
        
        HobbyGroup group = groupRepository.findByIdAndCreatorIdAndDeletedAtIsNull(groupId, creatorId)
                .orElseThrow(() -> new RuntimeException("Group not found or you don't have permission"));
        
        group.setStatus(HobbyGroup.GroupStatus.DELETED);
        group.setIsActive(false);
        group.setDeletedAt(LocalDateTime.now());
        
        groupRepository.save(group);
        log.info("Group deleted successfully: {}", groupId);
    }
    
    /**
     * Kullanıcıyı gruba ekle
     */
    @Transactional
    public MembershipResponse joinGroup(UUID userId, UUID groupId) {
        log.info("User {} joining group {}", userId, groupId);
        
        HobbyGroup group = groupRepository.findByIdAndDeletedAtIsNull(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        
        if (!group.getIsActive() || group.getStatus() != HobbyGroup.GroupStatus.ACTIVE) {
            throw new RuntimeException("Group is not active");
        }
        
        // Zaten üye mi kontrol et
        if (membershipRepository.findActiveMembership(groupId, userId).isPresent()) {
            throw new RuntimeException("User is already a member");
        }
        
        // Maksimum üye kontrolü
        if (group.getMaxMembers() != null && group.getMemberCount() >= group.getMaxMembers()) {
            throw new RuntimeException("Group is full");
        }
        
        GroupMembership membership = new GroupMembership();
        membership.setGroupId(groupId);
        membership.setUserId(userId);
        membership.setRole(GroupMembership.MembershipRole.MEMBER);
        membership = membershipRepository.save(membership);
        
        // Member count'u güncelle
        group.setMemberCount(group.getMemberCount() + 1);
        groupRepository.save(group);
        
        log.info("User {} joined group {} successfully", userId, groupId);
        return MembershipResponse.fromEntity(membership);
    }
    
    /**
     * Kullanıcıyı gruptan çıkar
     */
    @Transactional
    public void leaveGroup(UUID userId, UUID groupId) {
        log.info("User {} leaving group {}", userId, groupId);
        
        GroupMembership membership = membershipRepository.findActiveMembership(groupId, userId)
                .orElseThrow(() -> new RuntimeException("Membership not found"));
        
        // Creator gruptan ayrılamaz
        if (membership.getRole() == GroupMembership.MembershipRole.CREATOR) {
            throw new RuntimeException("Creator cannot leave the group");
        }
        
        membership.setLeftAt(LocalDateTime.now());
        membershipRepository.save(membership);
        
        // Member count'u güncelle
        HobbyGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        group.setMemberCount(Math.max(0, group.getMemberCount() - 1));
        groupRepository.save(group);
        
        log.info("User {} left group {} successfully", userId, groupId);
    }
    
    /**
     * Oluşturucunun gruplarını getir
     */
    @Transactional(readOnly = true)
    public List<HobbyGroupResponse> getCreatorGroups(UUID creatorId) {
        log.debug("Fetching groups for creator: {}", creatorId);
        return groupRepository.findByCreatorIdAndDeletedAtIsNullOrderByCreatedAtDesc(creatorId)
                .stream()
                .map(HobbyGroupResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * Aktif grupları sayfalı olarak getir
     */
    @Transactional(readOnly = true)
    public Page<HobbyGroupResponse> getActiveGroups(Pageable pageable) {
        log.debug("Fetching active groups");
        return groupRepository.findActiveGroups(pageable)
                .map(HobbyGroupResponse::fromEntity);
    }
    
    /**
     * Kategoriye göre aktif grupları getir
     */
    @Transactional(readOnly = true)
    public Page<HobbyGroupResponse> getActiveGroupsByCategory(String category, Pageable pageable) {
        log.debug("Fetching active groups by category: {}", category);
        return groupRepository.findActiveGroupsByCategory(category, pageable)
                .map(HobbyGroupResponse::fromEntity);
    }
    
    /**
     * Konuma göre aktif grupları getir
     */
    @Transactional(readOnly = true)
    public Page<HobbyGroupResponse> getActiveGroupsByLocation(String location, Pageable pageable) {
        log.debug("Fetching active groups by location: {}", location);
        return groupRepository.findActiveGroupsByLocation(location, pageable)
                .map(HobbyGroupResponse::fromEntity);
    }
    
    /**
     * Arama sorgusu ile aktif grupları getir
     */
    @Transactional(readOnly = true)
    public Page<HobbyGroupResponse> searchActiveGroups(String query, Pageable pageable) {
        log.debug("Searching active groups with query: {}", query);
        return groupRepository.searchActiveGroups(query, pageable)
                .map(HobbyGroupResponse::fromEntity);
    }
    
    /**
     * Kullanıcının üyeliklerini getir
     */
    @Transactional(readOnly = true)
    public List<MembershipResponse> getUserMemberships(UUID userId) {
        log.debug("Fetching memberships for user: {}", userId);
        return membershipRepository.findActiveMembershipsByUserId(userId)
                .stream()
                .map(MembershipResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * Grubun üyelerini getir
     */
    @Transactional(readOnly = true)
    public List<MembershipResponse> getGroupMembers(UUID groupId) {
        log.debug("Fetching members for group: {}", groupId);
        return membershipRepository.findActiveMembersByGroupId(groupId)
                .stream()
                .map(MembershipResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * Grup detayını getir
     */
    @Transactional(readOnly = true)
    public HobbyGroupResponse getGroupById(UUID groupId) {
        log.debug("Fetching group: {}", groupId);
        
        HobbyGroup group = groupRepository.findByIdAndDeletedAtIsNull(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        
        return HobbyGroupResponse.fromEntity(group);
    }
}

