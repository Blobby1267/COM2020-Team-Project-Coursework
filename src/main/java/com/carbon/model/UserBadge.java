package com.carbon.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Stores the currently selected badge for a user.
 * One user can have at most one selected badge at a time.
 */
@Entity
@Table(name = "user_badges", uniqueConstraints = {
    @UniqueConstraint(name = "uq_user_badges_user_id", columnNames = {"user_id"})
})
public class UserBadge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "badge_name", nullable = false)
    private String badgeName;

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getBadgeName() {
        return badgeName;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setBadgeName(String badgeName) {
        this.badgeName = badgeName;
    }
}
