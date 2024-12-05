# Explanation of the Code, Technology, and Logical Steps

## Introduction
This document will guide you through the application we developed, explaining the technology used, the logical steps involved, and what each part of the application does. We’ll go step by step, breaking down the code, its purpose, and the best practices we followed to build a secure, scalable, and maintainable system.

---

## 1. **Technology Overview**
The application leverages the following key technologies:

- **Spring Boot:** A Java-based framework that simplifies application development by providing a robust ecosystem and dependency injection.
- **Spring Security:** For managing authentication and authorization.
- **OAuth2 & OpenID Connect (OIDC):** Standards for secure authorization and user identity management.
- **Lombok:** A library to reduce boilerplate code like getters, setters, and constructors.
- **JPA (Java Persistence API):** For managing database interactions.
- **H2 Database:** An in-memory database used for this application for simplicity during development.

---

## 2. **Logical Steps of the Application**

### Step 1: **Create an Application in LinkedIn**
**Objective:** Register the application on LinkedIn to get client credentials for OAuth2 authentication.

- **What to Do:**
    1. Go to the LinkedIn Developer Portal.
    2. Create a new application and provide basic details like the name and redirect URI.
    3. Copy the `Client ID` and `Client Secret` provided after registration.
    4. Note the redirect URI you provided, as this will be used in the Spring Boot configuration.

### Step 2: **Initialize a Basic Spring Boot Application**
**Objective:** Set up the project structure using Spring Initializer and include necessary dependencies.

- **What to Do:**
    1. Go to [Spring Initializer](https://start.spring.io/).
    2. Configure the project with the following:
        - **Dependencies:** Web, Security, OAuth2 Client, JPA, H2 Database, Lombok.
        - **Packaging:** Jar.
        - **Java Version:** 17 or above.
    3. Generate and download the project.

- **Explanation of Packages:**
    - **Web:** Provides support for building RESTful web applications and serves HTTP endpoints.
    - **Security:** Enables authentication and authorization mechanisms.
    - **OAuth2 Client:** Supports the OAuth2 protocol for secure login.
    - **JPA:** Simplifies data persistence with database operations.
    - **H2 Database:** An in-memory database for fast and simple testing and development.

### Step 3: **Configure the Project**
**Objective:** Set up properties and dependencies to connect to LinkedIn and the database.

- **What to Do:**
    1. Use an `application.yaml` file to store configurations:
       ```yaml
       spring:
         datasource:
           url: jdbc:h2:mem:testdb;NON_KEYWORDS=user
           driverClassName: org.h2.Driver
           username: sa
           password:
         jpa:
           database-platform: org.hibernate.dialect.H2Dialect
           hibernate:
             ddl-auto: update
         security:
           oauth2:
             client:
               registration:
                 linkedin:
                   client-id: ${linkedin-client-id}
                   client-secret: ${linkedin-client-secret}
                   scope: openid, profile, email
                   redirect-uri: "{baseUrl}/login/oauth2/code/{registrationId}"
                   client-name: LinkedIn
                   authorization-grant-type: authorization_code
                   provider: linkedin
                   client_authentication_method: client_secret_post
               provider:
                 linkedin:
                   authorization-uri: https://www.linkedin.com/oauth/v2/authorization
                   token-uri: https://www.linkedin.com/oauth/v2/accessToken
                   user-info-uri: https://api.linkedin.com/v2/userinfo
                   jwk-set-uri: https://www.linkedin.com/oauth/openid/jwks
       logging:
         level:
           root: DEBUG
           com.linkedme.security.config: DEBUG
         pattern:
           console: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"
         file:
           name: logs\application.log
         logback:
           include-stacktrace: always
       ```
    2. Configure the database connection, OAuth2 credentials, and scopes.

### Step 4: **Authentication and Authorization**
**Objective:** Ensure only authenticated users can access specific features based on their roles.

- **Code:**
    - The `AuthenticationController` retrieves information about the currently authenticated user.
    - The `@AuthenticationPrincipal` annotation injects the logged-in user's details.
    - The `AdminController` and `UserController` manage role-based endpoints for Admins and Users.

- **What to Do:**
    - Test the `/api/authentication` endpoint to verify user details.
    - Use `@PreAuthorize("hasRole('ROLE_ADMIN')")` to restrict access to admin endpoints.

### Step 5: **Data Persistence**
**Objective:** Store and retrieve user and role data securely.

- **Code:**
    - The `User` and `Role` entities define the database schema.
    - The `UserRole` entity establishes the relationship between users and roles.
    - The `UserRepository` and `RoleRepository` interfaces handle database queries.

- **What to Do:**
    - Use JPA methods like `findByEmail` to fetch users.
    - Test role assignments and relationships in the database.

### Step 6: **Service Layer Logic**
**Objective:** Implement custom logic to handle user authentication and roles.

- **Code:**
    - The `LinkedinOidUserService` processes OAuth2 user data.
    - It checks if a user exists; if not, it creates a new user with a default role.

- **What to Do:**
    - Ensure the service correctly maps user attributes from OAuth2 (e.g., email, name).
    - Verify users are saved to the database with appropriate roles.

### Step 7: **Frontend Integration**
**Objective:** Create a user-friendly interface for interacting with backend APIs.

- **What to Do:**
    - Build a frontend using frameworks like React or Angular.
    - Use REST clients like Axios or Fetch to consume the backend APIs.
    - Test each endpoint thoroughly using tools like Postman before integrating.

### Step 8: **Testing and Debugging**
**Objective:** Ensure application stability and correctness.

- **What to Do:**
    - Write unit tests for services and repositories.
    - Use integration tests to verify end-to-end functionality.
    - Check logs for any errors during authentication or database operations.

### Step 9: **Deployment**
**Objective:** Deploy the application for use in a production environment.

- **What to Do:**
    - Use Docker to containerize the application.
    - Deploy the containers to a cloud provider like AWS or Google Cloud.
    - Set up CI/CD pipelines for automated deployment and updates.

---

## Conclusion
By following these steps, you’ll understand how each component of the application works and how they fit together. Focus on one step at a time, ensuring it works before moving on to the next.

Remember, a good application is not just about writing code but also about understanding the logic, ensuring security, and maintaining scalability. Let me know if you have any questions or need further clarification on any step!
