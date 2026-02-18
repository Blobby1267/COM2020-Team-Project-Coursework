package com.carbon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Carbon Footprint Challenge Application.
 * This Spring Boot application helps users track and reduce their carbon footprint through challenges.
 * 
 * Key features:
 * - User authentication and role-based access (USER vs MODERATOR)
 * - Challenge system with points (Daily, Weekly, Monthly challenges)
 * - Photo evidence submission and moderation
 * - Sustainable travel tracking
 * - Leaderboard showing top users by points
 * 
 * @SpringBootApplication enables:
 * - @Configuration: Marks this as a configuration class
 * - @EnableAutoConfiguration: Auto-configures Spring based on classpath dependencies
 * - @ComponentScan: Scans com.carbon package for @Controller, @Service, @Repository components
 */
@SpringBootApplication
public class TeamProjectApplication {

    /**
     * Main method that launches the Spring Boot application.
     * @param args - Command line arguments (none required for standard operation)
     * 
     * This method:
     * 1. Initializes Spring application context
     * 2. Auto-configures database, web server, security based on dependencies
     * 3. Scans for and registers all components (@Controller, @Service, etc.)
     * 4. Starts embedded Tomcat server (default port 8080)
     * 5. Begins accepting HTTP requests
     */
    public static void main(String[] args) {
        SpringApplication.run(TeamProjectApplication.class, args);
    }

}