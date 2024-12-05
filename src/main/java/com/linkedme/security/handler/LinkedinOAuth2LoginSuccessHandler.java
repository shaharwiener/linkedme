package com.linkedme.security.handler;

import com.linkedme.persistence.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;

import static com.linkedme.controller.AuthenticationController.AUTHENTICATION_ENDPOINT_PATH;

/**
 * Custom handler for successful authentication in OAuth2 login flow with LinkedIn.
 *
 * <p>This handler is triggered whenever a user successfully logs in using OAuth2.
 * It processes the authenticated user's information and redirects them to a specific endpoint.</p>
 *
 * <p>Key responsibilities:</p>
 * <ul>
 *     <li>Retrieve the authenticated user's email from the authentication token.</li>
 *     <li>Check if the user exists in the database using {@link UserRepository}.</li>
 *     <li>Store the user in the session for later use.</li>
 *     <li>Redirect the user to the authentication endpoint.</li>
 * </ul>
 */
@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class LinkedinOAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    // Repository for accessing user data in the database
    UserRepository userRepository;

    /**
     * Handles successful authentication events.
     *
     * <p>This method is automatically called when a user successfully authenticates
     * using LinkedIn. It retrieves the user's email, checks if the user exists in the database,
     * and redirects them to the authentication endpoint.</p>
     *
     * @param request        The HTTP request during the successful authentication attempt.
     * @param response       The HTTP response to send back to the client.
     * @param authentication The authentication object containing the user's details.
     * @throws IOException if an input or output error occurs.
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        // Extract the user's email from the authentication token
        String email = ((OAuth2AuthenticationToken) authentication).getPrincipal().getAttribute("email");

        // Ensure that the email is not null (basic validation)
        Objects.requireNonNull(email);

        // Look up the user in the database using their email
        userRepository.findByEmail(email).ifPresent(
                user -> {
                    // Log the successful lookup for monitoring/debugging
                    log.info("stage=on-authentication-success, message=person-found, person={} ", user);

                    // Store the user in the session for later use
                    request.getSession().setAttribute("person", user);
                });

        // Redirect the user to the predefined authentication endpoint
        response.sendRedirect(AUTHENTICATION_ENDPOINT_PATH);
    }
}
