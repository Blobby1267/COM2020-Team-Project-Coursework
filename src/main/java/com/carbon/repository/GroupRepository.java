package com.carbon.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.carbon.model.Group;

public interface GroupRepository extends JpaRepository<Group, Integer>{

    Group findById(int id);
    Group findByInviteCode(String inviteCode);

    List<Group> findByMembers_Id(long user_id);
    List<Group> findByOwner_Id(long userId);
    long deleteByOwner_Id(long userId);

    @Modifying
    @Query(value = "DELETE FROM group_members WHERE user_id = :userId", nativeQuery = true)
    int deleteMembershipsByUserId(@Param("userId") long userId);

    long countByMembers_Id(long userId);
    boolean existsByInviteCode(String inviteCode);
}