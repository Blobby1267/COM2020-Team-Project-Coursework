package com.carbon.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.carbon.model.Badge;

/**
 * Repository for the badges table.
 * Each row represents a badge completed by a specific user.
 */
public interface BadgeRepository extends JpaRepository<Badge, Long> {

    /** Returns all badge completions for a given user. */
    List<Badge> findByUserId(Long userId);

    /**
     * Returns only the badge names for a user — avoids loading the image LOB column.
     * Use this whenever you only need to check which badges a user has earned.
     */
    @Query("SELECT LOWER(b.name) FROM Badge b WHERE b.userId = :userId")
    Set<String> findNamesByUserId(@Param("userId") Long userId);

    /** Returns true if the user has already been awarded the named badge. */
    boolean existsByUserIdAndName(Long userId, String name);

    /** Case-insensitive variant used to support legacy badge name casing. */
    boolean existsByUserIdAndNameIgnoreCase(Long userId, String name);

    /** Deletes all badge rows associated with a user. */
    long deleteByUserId(Long userId);
}
