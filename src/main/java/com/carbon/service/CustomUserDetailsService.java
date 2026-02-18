package com.carbon.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetails;

import com.carbon.repository.UserRepository;
import com.carbon.model.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Custom implementation of Spring Security's UserDetailsService.
 * Bridges our User entity with Spring Security's authentication system.
 * Loaded by Spring Security during login to validate credentials and load user roles.
 * Configured in SecurityConfig to be used for authentication.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService{

    // Repository for fetching user data from database
    private final UserRepository userRepository;
    
    // Constructor injection for UserRepository
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Loads user details by username for Spring Security authentication.
     * Called automatically by Spring Security during login process.
     * @param username - The username submitted in login form
     * @return UserDetails object containing username, password, and roles
     * @throws UsernameNotFoundException if user doesn't exist
     * 
     * Process:
     * 1. Fetches User entity from database by username
     * 2. Throws exception if user not found (login fails)
     * 3. Converts our User entity to Spring Security's UserDetails format
     * 4. Spring Security then validates the password against stored hash
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
       // Fetch user from database
       User user = userRepository.findByUsername(username);
       
       // If user not found, authentication fails
       if (user == null) {
           throw new UsernameNotFoundException("User not found: " + username);
       }

       // Convert our User entity to Spring Security's UserDetails format
       return org.springframework.security.core.userdetails.User
            .builder()
            .username(user.getUsername())
            .password(user.getPassword()) // Already BCrypt-hashed
            .roles(user.getRole()) // Sets authorities as ROLE_{role}
            .build();
    }
}
