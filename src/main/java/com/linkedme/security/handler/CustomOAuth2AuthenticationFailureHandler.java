package com.linkedme.security.handler;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import java.io.IOException;

/**
 * Custom handler for managing authentication failures in an OAuth2 login flow.
 *
 * <p>This class is part of the security layer in an application and is triggered
 * whenever a user fails to authenticate during an OAuth2 login attempt.</p>
 *
 * <p>Key responsibilities:</p>
 * <ul>
 *     <li>Logs the details of the failure for debugging and monitoring.</li>
 *     <li>Redirects the user to a custom error page, allowing the application
 *         to provide feedback about the failure.</li>
 * </ul>
 */
public class CustomOAuth2AuthenticationFailureHandler implements AuthenticationFailureHandler {

    /**
     * Handles authentication failure events.
     *
     * <p>This method is called automatically when a user fails to authenticate.
     * It logs the error details for debugging and then redirects the user to an error page.</p>
     *
     * @param request   The HTTP request during the failed authentication attempt. Contains details about the request.
     * @param response  The HTTP response used to send information back to the user.
     * @param exception The exception that contains details about why the authentication failed.
     * @throws IOException      If an input or output error occurs while handling the failure.
     */
    @Override
    public void onAuthenticationFailure(jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response, AuthenticationException exception) throws IOException, jakarta.servlet.ServletException {
        // Log the full exception details to the server's console for debugging purposes
        System.err.println("Authentication failed: " + exception.getMessage());
        exception.printStackTrace();

        // Redirect the user to a custom error page with a message describing the failure
        response.sendRedirect("/error?message=" + exception.getMessage());
    }
}