package com.carbon.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.carbon.model.Badge;

/**
 * Repository for the badges table.
 * Each row represents a badge completed by a specific user.
 */
public interface BadgeRepository extends JpaRepository<Badge, Long> {

    /** Returns all badge completions for a given user. */
    List<Badge> findByUserId(Long userId);

    /** Returns true if the user has already been awarded the named badge. */
    boolean existsByUserIdAndName(Long userId, String name);
}
