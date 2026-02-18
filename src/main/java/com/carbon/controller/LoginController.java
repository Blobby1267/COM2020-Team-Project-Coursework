package com.carbon.controller;

import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.carbon.model.User;
import com.carbon.repository.UserRepository;
import java.util.logging.Logger;
import org.springframework.security.crypto.password.PasswordEncoder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.ServletException;

/**
 * Controller for handling user authentication: login, registration, and logout.
 * Works with Spring Security for authentication and BCrypt for password hashing.
 */
@Controller
public class LoginController {
    private static final Logger LOGGER = Logger.getLogger(LoginController.class.getName());

    // BCrypt password encoder for securely hashing passwords
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    // Repository for user database operations
    @Autowired
    private UserRepository userRepository;

    /**
     * Helper method to register a new user with encrypted password.
     * @param user - User object with plain-text password
     */
    public void registerUser(User user){
        // Hash password with BCrypt before storing
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }


    /* 
     * OLD LOGIN HANDLER - replaced by Spring Security's built-in form login
     * Spring Security now handles authentication automatically via SecurityConfig
     * 
    @PostMapping("/login")
    public String handleLogin(@RequestParam String username, @RequestParam String password) {
        User user = userRepository.findByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            LOGGER.info("User has successfully signed in.");
            return "redirect:/tasks.html"; // Success!
        }
        LOGGER.info("User password is incorrect.");
        return "redirect:/login.html?error=true";
    }
    */ 
    
    /**
     * Handles new user registration from the registration form.
     * @param username - Unique username
     * @param password - Plain-text password (will be encrypted)
     * @param campus - User's campus location
     * @param year - User's academic year
     * @param request - HttpServletRequest for programmatic login after registration
     * @return redirect to tasks page on success, or login page with error
     */
    @PostMapping("/register")
    public String handleRegister(@RequestParam String username, @RequestParam String password, @RequestParam String campus, @RequestParam String year, HttpServletRequest request) {
        // Check if username is available
        if (userRepository.findByUsername(username) == null) {
            // Create new user with default settings
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setPassword(password); // Will be encrypted in registerUser()
            newUser.setRole("USER"); // Default role for new registrations
            newUser.setPoints(0); // Start with zero points
            newUser.setCampus(campus);
            registerUser(newUser); // Encrypts password and saves to database
            
            // Logout any existing user session before logging in the new user
            try {
                request.logout();
            } catch (ServletException e) {
                LOGGER.warning("Logout before auto-login failed: " + e.getMessage());
            }
            
            // Authenticate the user immediately after registration
            try {
                request.login(username, password);
                LOGGER.info("User has been created and logged in.");
            } catch (ServletException e) {
                LOGGER.severe("Auto-login failed after registration: " + e.getMessage());
            }
            
            return "redirect:/tasks?registered=true";
        }
        // Username already taken
        LOGGER.info("User already exists.");
        return "redirect:/login?error=exists";
    }
    
    /**
     * Displays the login page.
     * @return the name of the login template to render
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /**
     * Displays the logout confirmation page.
     * Actual logout is handled by Spring Security.
     * @return the name of the logout template to render
     */
    @GetMapping("/logout")
    public String logout() {
        return "logout";
    }

    /**
     * Redirects login.html URLs to /login endpoint.
     * Maintains backwards compatibility.
     */
    @GetMapping("/login.html")
    public String redirectToLogin() {
        return "redirect:/login";
    }


}
