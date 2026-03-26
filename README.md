# Table of Contents
[Overview](#overview)  
[Authors](#authors)  
[Installation and Usage](#installation-and-usage)   
[Projects/Issues](#projects--issues)    
[Content](#content)  


# Overview
COM2020 Team Project, this is our solution for the Campus Carbon Challenge Game and Dashboard. Footprint is a Spring Boot web application that encourages sustainable behaviour through a challenge based game where users complete carbon friendly tasks and track their impact over time. The platform includes role-specific views for users and moderators, challenges, travel tracking,leaderboards, group features, and analytics dashboards.

# Authors

**Madeleine Walters** - Project Lead / Scrum Master - mw1104@exeter.ac.uk  
**Ben Hoskins** - Technical Lead - bh618@exeter.ac.uk  
**Marko Parkinson** - Developer - mp983@exeter.ac.uk  
**Phoebe Say** - Developer - ps724@exeter.ac.uk  
**Zarreen Peeroo** - UX - zbp201@exeter.ac.uk  
**Davi Oppes** - Testing - do352@exeter.ac.uk  
**Armin Golahmadi** - Documentation - ag1096@exeter.ac.uk  
**Jai Thacker** - Data Lead - jut201@exeter.ac.uk  

# Installation and Usage
Footprint is publicly hosted on Render and accessible at:

```text
https://com2020-team-project-coursework.onrender.com/
```

> **Note:** The app is on Render's free tier. If it has not received traffic recently it may take up to 60 seconds to wake from a cold start before the login page loads.

Alternatively you can install the source code and run the website locally using the instructions below.

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


## Global deployment (Render + PostgreSQL)

This project can be deployed globally using the included `Dockerfile`.

### 1. Push code to GitHub

Ensure the latest code is pushed to the main branch.

### 2. Create a PostgreSQL database

Create a managed PostgreSQL instance (for example on Render) and note:

- host
- port
- database name
- username
- password

### 3. Create a Render Web Service from this repository

- Choose Docker-based deployment.
- Select this repository and branch.

### 4. Configure environment variables

Set these variables in Render:

```text
SPRING_PROFILES_ACTIVE=prod
SPRING_DATASOURCE_URL=jdbc:postgresql://<host>:<port>/<database>
SPRING_DATASOURCE_USERNAME=<username>
SPRING_DATASOURCE_PASSWORD=<password>
```

### 5. Deploy

Render will build and run the container and assign a public URL.

Open:

```text
https://com2020-team-project-coursework.onrender.com/
```

### 6. Accessing the production database

Connection credentials are shown in the Render dashboard under **your PostgreSQL instance → Connect**. Use the **External Database URL** when connecting from a local machine.

Install `psql` if you don't already have it (included with [PostgreSQL](https://www.postgresql.org/download/)), then connect using the values from the dashboard:

```bash
psql -h <external-host> -U <username> <database-name>
```

You will be prompted for the password.

### 7. Create moderator account

New users are registered as `USER` by default. To promote a user to moderator, run:

```sql
UPDATE users SET role='MODERATOR' WHERE username='your_username';
```

### 8. Backing up the production database

Use `pg_dump` to create a backup of the PostgreSQL database. Connection details are available in the Render dashboard under **your PostgreSQL instance → Connect**.

**Create a backup:**

```bash
pg_dump -h <external-host> -U <username> -d <database-name> -F c -f backup.dump
```

- `-F c` produces a custom-format archive (compressed, suitable for `pg_restore`).
- You will be prompted for the password, or you can set the `PGPASSWORD` environment variable to avoid the prompt:

```bash
PGPASSWORD=<password> pg_dump -h <external-host> -U <username> -d <database-name> -F c -f backup.dump
```

**Restore from a backup:**

```bash
pg_restore -h <external-host> -U <username> -d <database-name> -F c backup.dump
```

> **Note:** `pg_dump` and `pg_restore` are included with the [PostgreSQL client tools](https://www.postgresql.org/download/). 

## Locally Hosting

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

Our [technical scrumboard](https://github.com/users/Blobby1267/projects/1) can be found in the Projects section, and contains issues for all the code aspects that needed to be implemented from our scrumboard.  

To see the full scrumboard please see our sprint plan and scrumboards in ```02_process/scrumboard_export.pdf```.  

# Content

## ```/0_admin```

```/submission.txt```  
Contains submission details for COM2020 Team Project, also see for demo/test account credentials.  
**@Author** Madeleine Walters  

```/team_members.txt```  
Contains all members of the team with their contact emails and student numbers.  
**@Author** Madeleine Walters  

## ```/1_report```

```/cw1_prototype_report.pdf```  
Report documenting an overview what we have built, and the process in order to achieve it, followed by an evaluation of our first Sprint.  
**@Author** Armin Golahmadi, Madeleine Walters, Davi Oppes, Ben Hoskins  

## ```/2_process```

```/scrumboard_export.pdf```  
Contains our sprint one plan and our weekly scrum boards used to track our progress in the project.  
**@Author** Madeleine Walters  

```/meeting_minutes.pdf```  
Log of all the meetings and notes taken, as well as a log of members contributions on specific dates.  
**@Author** Madeleine Walters  

```/risk_register.pdf```  
List of the risks regarding the development of the project and mitigation actions taken, maintained by the Project Lead.  
**@Author** Madeleine Walters  

## ```/3_ethics_and_licensing```

```/ethical_and_legal_considerations.pdf```  
Documents the legal, ethical and accessibility considerations made and upheld in this project and how we have ensured to uphold them.  
**@Author** Zarreen Peeroo  

```/license_decision.pdf```  
Our license for this project and the justification of it.  
**@Author** Zarreen Peeroo  

```/software_data_inventory.xlsx```  
Dependencies required for the project.  
**@Author**  

## ```/4_technical```

```/source_code_snapshot.zip```  
ZIP of the source and testing code.  
**@Author** Ben Hoskins, Marko Parkinson, Phoebe Say, Jai Thacker, Davi Oppes  

```/deployment_guide.pdf```  
Instructions of running the tests.  
**@Author** Davi Oppes, Ben Hoskins  

```/testing_evidence.pdf```  
Report of the E2E testing scenarios.  
**@Author** Davi Oppes  

## ```/5_presentation```

```/cw1_demo_slides.pdf```  
Slides used in the coursework presentation.  
**@Author** Zarreen Peeroo, Phoebe Say, Madeleine Walters  

## ```/src```
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

## ```/target```
Maven build directory generated after compiling and packaging the src folder.

- `target`
    - `classes` - Compiled main classes and copied runtime resources.
        - `com/carbon` - Compiled `.class` files for application packages.
        - `templates` - Processed/copied template files used at runtime.
        - `static` - Copied static assets and data directory.
        - `application.properties` - Copied runtime configuration.
    - `generated-sources` - Build-time generated source files (including annotation-processor output).
    - `maven-status/maven-compiler-plugin` - Maven compiler plugin metadata about compile/test-compile inputs and outputs.
    - `test-classes/com/carbon` - Compiled test classes from `src/test/java`.
