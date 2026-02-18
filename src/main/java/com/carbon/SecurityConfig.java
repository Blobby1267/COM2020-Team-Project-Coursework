package com.carbon;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security configuration for the application.
 * Defines which URLs require authentication, authorization rules, and security features.
 * Configures form-based login, logout, CSRF protection, and password encoding.
 * Works with CustomUserDetailsService for loading user credentials during authentication.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    /**
     * Configures the security filter chain with authorization rules and authentication.
     * @param http - HttpSecurity object to configure
     * @return SecurityFilterChain bean for Spring Security
     * @throws Exception if configuration fails
     * 
     * Authorization rules:
     * - Public access (no login): /, /login, /register, /data/**, /h2-console/**, static files (*.png, *.css)
     * - MODERATOR only: /api/moderator/** (evidence approval, challenge management)
     * - Authenticated users: /api/** (all other API endpoints)
     * - All other URLs: require authentication
     * 
     * Security features:
     * - CSRF disabled for /h2-console/** and /api/** (allows REST API calls and H2 console access)
     * - Frame options disabled (allows H2 console to display in iframe)
     * - Form login with custom login page at /login
     * - Successful login redirects to /tasks
     * - Logout enabled for all users
     */
    @Bean
    public SecurityFilterChain SecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Public endpoints (no authentication required)
                .requestMatchers("/", "/login", "/register", "/data/**", "/h2-console/**", "/**.png", "/**.css").permitAll()
                // Moderator-only endpoints (evidence review, challenge management)
                .requestMatchers("/api/moderator/**").hasRole("MODERATOR")
                // Authenticated user endpoints (challenge completion, evidence submission)
                .requestMatchers("/api/**").authenticated()
                // All other requests require authentication
                .anyRequest().authenticated()
            )
            // Disable CSRF for H2 console and API endpoints
            .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**", "/api/**"))
            // Disable frame options to allow H2 console iframe
            .headers(headers -> headers.frameOptions(frame -> frame.disable()))
            // Configure form-based login
            .formLogin(form -> form
                .loginPage("/login") // Custom login page
                .defaultSuccessUrl("/tasks", true) // Redirect to tasks after successful login
                .permitAll() // Allow everyone to access login page
            )
            // Enable logout for all users
            .logout(logout -> logout
                .permitAll()
            );
        return http.build();
    }

    /**
     * Provides BCrypt password encoder bean for secure password hashing.
     * Used by LoginController to hash passwords before storing in database.
     * Used by Spring Security to verify passwords during authentication.
     * BCrypt is a strong, adaptive hashing algorithm resistant to brute-force attacks.
     * @return PasswordEncoder instance (BCryptPasswordEncoder)
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
