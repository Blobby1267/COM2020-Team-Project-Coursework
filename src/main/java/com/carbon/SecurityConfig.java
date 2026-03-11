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
 * Configures form-based login, logout and password encoding.
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
     * - Public access (no login): /, /login, /register, static files
     * - MODERATOR only: /api/moderator/** (evidence approval, challenge management)
     * - Authenticated users: /api/** (all other API endpoints)
     * - All other URLs: require authentication
     * 
     * Security features:
     * - CSRF enabled for APIs; only H2 console is exempted
     * - Frame options set to same-origin (allows local H2 console iframe)
     * - Form login with custom login page at /login
     * - Successful login redirects to /tasks
     * - Logout enabled for all users
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Public endpoints (no authentication required)
                .requestMatchers("/", "/login", "/register", "/**.png", "/**.css", "/**.js", "/favicon.ico").permitAll()
                // Moderator-only endpoints (evidence review, challenge management)
                .requestMatchers("/api/moderator/**").hasRole("MODERATOR")
                // Authenticated user endpoints (challenge completion, evidence submission)
                .requestMatchers("/api/**").authenticated()
                // All other requests require authentication
                .anyRequest().authenticated()
            )
            // Keep CSRF enabled for API endpoints, while allowing local H2 console to function
            .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))
            // Restrict iframe embedding to same origin (needed by H2 console)
            .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
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
     * @return PasswordEncoder instance (BCryptPasswordEncoder)
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
