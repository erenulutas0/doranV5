package com.microservices.hobbygroup.controller;

import com.microservices.hobbygroup.dto.HobbyGroupRequest;
import com.microservices.hobbygroup.dto.HobbyGroupResponse;
import com.microservices.hobbygroup.dto.MembershipResponse;
import com.microservices.hobbygroup.service.HobbyGroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/hobby-groups")
@RequiredArgsConstructor
@Slf4j
public class HobbyGroupController {
    
    private final HobbyGroupService service;
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public HobbyGroupResponse createGroup(
            @RequestHeader("X-User-Id") UUID creatorId,
            @Valid @RequestBody HobbyGroupRequest request) {
        log.info("POST /api/hobby-groups - Creator: {}", creatorId);
        return service.createGroup(creatorId, request);
    }
    
    @PutMapping("/{groupId}")
    public HobbyGroupResponse updateGroup(
            @RequestHeader("X-User-Id") UUID creatorId,
            @PathVariable UUID groupId,
            @Valid @RequestBody HobbyGroupRequest request) {
        log.info("PUT /api/hobby-groups/{} - Creator: {}", groupId, creatorId);
        return service.updateGroup(creatorId, groupId, request);
    }
    
    @DeleteMapping("/{groupId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGroup(
            @RequestHeader("X-User-Id") UUID creatorId,
            @PathVariable UUID groupId) {
        log.info("DELETE /api/hobby-groups/{} - Creator: {}", groupId, creatorId);
        service.deleteGroup(creatorId, groupId);
    }
    
    @PostMapping("/{groupId}/join")
    @ResponseStatus(HttpStatus.CREATED)
    public MembershipResponse joinGroup(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID groupId) {
        log.info("POST /api/hobby-groups/{}/join - User: {}", groupId, userId);
        return service.joinGroup(userId, groupId);
    }
    
    @PostMapping("/{groupId}/leave")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void leaveGroup(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID groupId) {
        log.info("POST /api/hobby-groups/{}/leave - User: {}", groupId, userId);
        service.leaveGroup(userId, groupId);
    }
    
    @GetMapping("/my-groups")
    public List<HobbyGroupResponse> getMyGroups(@RequestHeader("X-User-Id") UUID creatorId) {
        log.info("GET /api/hobby-groups/my-groups - Creator: {}", creatorId);
        return service.getCreatorGroups(creatorId);
    }
    
    @GetMapping("/active")
    public Page<HobbyGroupResponse> getActiveGroups(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("GET /api/hobby-groups/active - Page: {}, Size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by("memberCount").descending().and(Sort.by("createdAt").descending()));
        return service.getActiveGroups(pageable);
    }
    
    @GetMapping("/active/category/{category}")
    public Page<HobbyGroupResponse> getActiveGroupsByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("GET /api/hobby-groups/active/category/{} - Page: {}, Size: {}", category, page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by("memberCount").descending().and(Sort.by("createdAt").descending()));
        return service.getActiveGroupsByCategory(category, pageable);
    }
    
    @GetMapping("/active/location/{location}")
    public Page<HobbyGroupResponse> getActiveGroupsByLocation(
            @PathVariable String location,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("GET /api/hobby-groups/active/location/{} - Page: {}, Size: {}", location, page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by("memberCount").descending().and(Sort.by("createdAt").descending()));
        return service.getActiveGroupsByLocation(location, pageable);
    }
    
    @GetMapping("/active/search")
    public Page<HobbyGroupResponse> searchActiveGroups(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("GET /api/hobby-groups/active/search?q={} - Page: {}, Size: {}", q, page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by("memberCount").descending().and(Sort.by("createdAt").descending()));
        return service.searchActiveGroups(q, pageable);
    }
    
    @GetMapping("/my-memberships")
    public List<MembershipResponse> getMyMemberships(@RequestHeader("X-User-Id") UUID userId) {
        log.info("GET /api/hobby-groups/my-memberships - User: {}", userId);
        return service.getUserMemberships(userId);
    }
    
    @GetMapping("/{groupId}/members")
    public List<MembershipResponse> getGroupMembers(@PathVariable UUID groupId) {
        log.info("GET /api/hobby-groups/{}/members", groupId);
        return service.getGroupMembers(groupId);
    }
    
    @GetMapping("/{groupId}")
    public HobbyGroupResponse getGroupById(@PathVariable UUID groupId) {
        log.info("GET /api/hobby-groups/{}", groupId);
        return service.getGroupById(groupId);
    }
}

