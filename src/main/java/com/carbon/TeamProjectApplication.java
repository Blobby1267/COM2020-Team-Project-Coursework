package com.carbon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Carbon Footprint Challenge Application.
 * This Spring Boot application helps users track and reduce their carbon footprint through challenges.
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
     */
    public static void main(String[] args) {
        SpringApplication.run(TeamProjectApplication.class, args);
    }

}