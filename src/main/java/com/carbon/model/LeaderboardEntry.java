package com.carbon.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

/**
 * Entity class representing an entry in the leaderboard.
 * Mapped to the "leaderboard" table in the database.
 * Stores a snapshot of user rankings by points.
 * Separate from User table to allow for caching/optimization of leaderboard queries.
 * Can be rebuilt from User data via LeaderboardService.rebuildLeaderboard().
 */
@Entity
@Table(name = "leaderboard")
public class LeaderboardEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId; // Reference to User.id
    private String username; // Cached username for display
    private int points; // Cached points for sorting

    // Constructors
    
    //Default constructor for JPA
    public LeaderboardEntry() {}

    /**
     * Convenience constructor to create leaderboard entry from User entity.
     * Copies userId, username, and points at time of creation.
     * @param user - User entity to snapshot
     */
    public LeaderboardEntry(User user) {
        this.userId = user.getId();
        this.username = user.getUsername();
        this.points = user.getPoints();
    }

    // Getter and Setter Methods
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}

