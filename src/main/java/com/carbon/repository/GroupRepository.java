package com.carbon.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.carbon.model.Group;

public interface GroupRepository extends JpaRepository<Group, Integer>{

    Group findById(int id);
    Group findByInviteCode(String inviteCode);

    List<Group> findByMembers_Id(long user_id);
    boolean existsByInviteCode(String inviteCode);
}