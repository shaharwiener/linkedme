# LinkedMe

LinkedMe is a project that enables users to log in to an application using their LinkedIn credentials. This project integrates LinkedIn's OAuth2 authentication system, providing a seamless and secure login experience.

## Features
- **LinkedIn OAuth2 Integration**: Users can log in using their LinkedIn accounts.
- **User Information Retrieval**: Fetch and display basic user details such as name, profile image, and connections.
- **Friends List Display**: Show a list of the user's LinkedIn connections along with their companies.

## Tech Stack
- **Backend**: Java, Spring Boot
- **Authentication**: Spring Security with LinkedIn OAuth2
- **Frontend**: HTML/CSS (or any frontend framework of choice)
- **Hosting**: Local or Cloud (e.g., AWS, Azure, or Heroku)

## Prerequisites
1. A LinkedIn Developer account.
2. A LinkedIn App created with the following permissions enabled:
    - `profile`
    - `email`
    - `openid`
    - `w_member_social` (if needed for extended functionality)
3. Java Development Kit (JDK 11 or later).
4. Maven or Gradle for dependency management.

## Setup Instructions

### 1. Clone the Repository
```bash
git clone https://github.com/shaharwiener/linkedme.git
cd LinkedMe
```

### 2. Configure LinkedIn App Credentials
Create an `application.properties` file in the `src/main/resources` directory and add the following:
```properties
spring.security.oauth2.client.registration.linkedin.client-id=<your-client-id>
spring.security.oauth2.client.registration.linkedin.client-secret=<your-client-secret>
spring.security.oauth2.client.registration.linkedin.redirect-uri=<your-redirect-uri>
```

### 3. Build and Run the Application
#### Using Maven:
```bash
mvn clean install
mvn spring-boot:run
```

### 4. Access the Application
Open a web browser and go to:
```
http://localhost:8080
```

## Development Notes
- Ensure the LinkedIn App permissions match your application's needs.
- Use a secure environment to store client secrets (e.g., environment variables).
- Customize the UI to match your application's design.

## Future Enhancements
- Extend user profile data retrieval.
- Add support for posting updates to LinkedIn.
- Implement multi-language support for broader usability.

## Contributing
Contributions are welcome! Please fork the repository and submit a pull request.

## License
This project is licensed under the [MIT License](LICENSE).
