package com.microservices.hobbygroup.repository;

import com.microservices.hobbygroup.model.GroupMembership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GroupMembershipRepository extends JpaRepository<GroupMembership, UUID> {
    
    /**
     * Kullanıcının üyeliklerini getir (aktif)
     */
    @Query("SELECT gm FROM GroupMembership gm WHERE gm.userId = :userId AND gm.leftAt IS NULL")
    List<GroupMembership> findActiveMembershipsByUserId(@Param("userId") UUID userId);
    
    /**
     * Grubun üyelerini getir (aktif)
     */
    @Query("SELECT gm FROM GroupMembership gm WHERE gm.groupId = :groupId AND gm.leftAt IS NULL ORDER BY gm.joinedAt ASC")
    List<GroupMembership> findActiveMembersByGroupId(@Param("groupId") UUID groupId);
    
    /**
     * Kullanıcının grup üyeliğini kontrol et
     */
    @Query("SELECT gm FROM GroupMembership gm WHERE gm.groupId = :groupId AND gm.userId = :userId AND gm.leftAt IS NULL")
    Optional<GroupMembership> findActiveMembership(@Param("groupId") UUID groupId, @Param("userId") UUID userId);
    
    /**
     * Grubun üye sayısını getir
     */
    @Query("SELECT COUNT(gm) FROM GroupMembership gm WHERE gm.groupId = :groupId AND gm.leftAt IS NULL")
    Long countActiveMembersByGroupId(@Param("groupId") UUID groupId);
}

