# Time Management System

**Development Note:** Approximately 80% of this project's initial codebase was generated with the assistance of AI.

## Description

A Spring Boot application for tracking time spent by users on different projects. Includes user/project management and budget utilization tracking.

## Core Features

* CRUD operations for Users, Projects, and Time Entries.
* User roles (ADMINISTRATOR, MANAGER, EMPLOYEE) for authorization.
* Assigning users to projects.
* Filtering and pagination for user/project lists.
* Project budget utilization calculation.
* Automatic database setup/migration via Liquibase.
* Swagger UI for API documentation.

## Technologies

* Java 21 / Spring Boot 3.4
* Spring Data JPA / Spring Web / Spring Security
* PostgreSQL
* Liquibase
* Lombok
* Maven
* Docker

## Quick Start

1.  **Prerequisites:** JDK 21+, Maven, Docker.
2.  **Clone** the repository.
3.  **Start Database:** `docker-compose up -d` (in project root)
4.  **Build:** `mvn clean install`
5.  **Run:** `mvn spring-boot:run` or `java -jar target/TimeManagmentSystem-0.0.1-SNAPSHOT.jar`

The application will be available at `http://localhost:8080`.

## API Documentation

* **Swagger UI:** `http://localhost:8080/swagger-ui.html`

## Security

* Uses **HTTP Basic Authentication**.
* Default **admin** user created on first run:
    * Login: `admin`
    * Password: `adminpassword`
## Postman

Collections for testing are available in the `/postman` directory.