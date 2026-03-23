package com.carbon.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.carbon.model.UserBadge;

public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {

    Optional<UserBadge> findByUserId(Long userId);

    List<UserBadge> findByUserIdIn(List<Long> userIds);

    long deleteByUserId(Long userId);
}
