package com.carbon.model;

@Entity // Tells Java this maps to a database table
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    private int points;

    // Standard getters and setters...
}