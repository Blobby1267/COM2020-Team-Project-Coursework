# Table of Contents
[Overview](#overview)  
[Authors](#authors)  
[Installation and Usage](#installation-and-usage)   
[Projects/Issues](#projects--issues)    
[Content](#content)  


# Overview
COM2020 Team Project, this is our solution for the Campus Carbon Challenge Game and Dashboard. Footprint is a Spring Boot web application that encourages sustainable behaviour through a challenge based game where users complete carbon friendly tasks and track their impact over time. The platform includes role-specific views for users and moderators, challenges, travel tracking,leaderboards, group features, and analytics dashboards.

# Authors

**Madeleine Walters** - Project Lead  
**Ben Hoskins** - Technical Lead  
**Marko Parkinson** - Developer  
**Phoebe Say** - Developer  
**Zarreen Peeroo** - UX  
**Davi Oppes** - Testing  
**Armin Golahmadi** - Documentation  
**Jai Thacker** - Data Lead  

# Installation and Usage
Footprint is not publically hosted, so you must install the source code and run the website locally.

## Pre-Requisites

- Java Development Kit (JDK) **21**
- Apache Maven **3.9+**
- Git (to clone/pull the repository)
- Docker Desktop, if you want to run via Docker instead of Maven

You can verify your setup with:

```bash
java -version
mvn -version
git --version
```

Dependencies can be found in the ```software_data_inventory.xlsx```

## Usage

1. Open a terminal in the project root.
2. Build the dependencies and run the app:

```bash
mvn compile
mvn spring-boot:run
```

3. Open the website in your browser:
```text
localhost:8080/login
```
This takes you to the gateway to the rest of the website


### Database notes

- The project uses an H2 file database.
- H2 console (when app is running): `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:file:./target/classes/static/data/testdb`
- Username: `sa`
- Password: *(leave blank)*

# Running tests
For guidance on running testing, please refer the to deployment guide which can be found at ```4_technical/deployment_guide.pdf```

# Projects / Issues

For our 

# Content

## 0_admin

```submission.txt```  
**@Author** Madeleine Walters  
```team_members.txt```  
**@Author** Madeleine Walters  

## 1_report

```cw1_prototype_report.pdf```  
**@Author** Armin Golahmadi, Jai Thacker  

## 2_process

```scrumboard_export.pdf```  
**@Author** Madeleine Walters  
```meeting_minutes.pdf```  
**@Author** Madeleine Walters  
```risk_register.pdf```  
**@Author** Madeleine Walters  

## 3_ethics_and_licensing

```ethical_and_legal_considerations.pdf```  
**@Author** Zarreen Peeroo  
```license_decision.pdf```  
**@Author** Zarreen Peeroo  
```software_data_inventory.xlsx```  
**@Author**  

## 4_technical

```source_code_snapshot.zip```  
**@Author** Ben Hoskins, Marko Parkinson, Phoebe Say, Jai Thacker, Davi Oppes  
```deployment_guide.pdf```  
**@Author**  
```testing_evidence.pdf```  
**@Author** Davi Oppes  

## 5_presentation

```cw1_demo_slides.pdf```  
**@Author** Zarreen Peeroo, Phoebe Say, Madeleine Walters  

## src
Primary source code and test code for the Spring Boot application.  
**@Author** Ben Hoskins, Marko Parkinson, Phoebe Say, Davi Oppes
- `src`
    - `main` - Main application source code and resources.
        - `java` - Main Java source files.
            - `com/carbon` - Root package for the application.
                - `controller` - Controllers that handle routes and page requests.
                - `model` - Domain model classes.
                - `repository` - Data access layer interfaces for database operations.
                - `service` - Perform logic calculations used by controllers and repositories.
        - `resources` - Non-Java resources loaded at runtime.
            - `application.properties` - App configuration properties.
            - `static` - Static assets served directly (CSS, HTML, data files).
                - `data` - File-based database used by the app.
            - `templates` - Thymeleaf templates for server-rendered pages.

    - `test` - Automated tests.
        - `java` - Test source code.
            - `com/carbon` - Unit and integration tests for models, controllers, and flows.

## target
Maven build directory generated after compiling and packaging the src folder.

-`target`
    - `classes` - Compiled main classes and copied runtime resources.
        - `com/carbon` - Compiled `.class` files for application packages.
        - `templates` - Processed/copied template files used at runtime.
        - `static` - Copied static assets and data directory.
        - `application.properties` - Copied runtime configuration.
    - `generated-sources` - Build-time generated source files (including annotation-processor output).
    - `maven-status/maven-compiler-plugin` - Maven compiler plugin metadata about compile/test-compile inputs and outputs.
    - `test-classes/com/carbon` - Compiled test classes from `src/test/java`.
