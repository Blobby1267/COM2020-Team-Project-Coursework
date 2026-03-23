package com.carbon.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "fk_user_badges_user"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

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
