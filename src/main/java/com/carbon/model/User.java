package com.carbon.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;

@Entity // Tells Java this maps to a database table
@Table(name = "users")
public class User {
    @Id  // Primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incrementing ID
    private Long id; // Unique identifier
    private String username;
    private String password;
    private int points;
    private String role = "First Year";
    private String campus = "streatham";


    // getters
    public Long getId() {
        return id;
    }
    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
    public int getPoints() {
        return points;
    }
    public String getRole() {
        return role;
    }
    public String getCampus() {
        return campus;
    }

    // setters
    public void setUsername(String username) {
        this.username = username;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void setPoints(int points) {
        this.points = points;
    }
    public void setRole(String role) {
        this.role = role;
    }
    public void setCampus(String campus) {
        this.campus = campus;
    }
}