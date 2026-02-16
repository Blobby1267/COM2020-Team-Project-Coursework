package com.carbon.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

@Entity // Tells Java this maps to a database table
@Table(name = "users")
public class User {
    @Id  // Primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incrementing ID
    private Long id; // Unique identifier
    private String username;
    private String password;
    private int points = 0;
    private String role;
    private String campus = "streatham";
    @Column(name = "\"year\"") // year is a reserved keyword in SQL, so we need to escape it
    private String year = "First_Year";


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
    public String getYear() {
        return year;
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
    public void setYear(String year) {
        this.year = year;
    }
}