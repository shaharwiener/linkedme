package com.linkedme.controller;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for handling authentication-related endpoints.
 *
 * <p>This controller defines the API endpoints for user authentication.
 * It retrieves information about the currently authenticated user using OpenID Connect (OIDC).</p>
 *
 * <p>Annotations:</p>
 * <ul>
 *     <li>@RestController: Marks this class as a Spring REST controller, capable of handling HTTP requests and returning HTTP responses.</li>
 *     <li>@RequestMapping: Maps the base URL for all endpoints in this controller to the specified path constant.</li>
 *     <li>@RequiredArgsConstructor: Generates a constructor for final fields, allowing dependency injection.</li>
 *     <li>@FieldDefaults: Sets the default access level for fields to private and makes them final.</li>
 * </ul>
 */
@RestController
@RequestMapping(AuthenticationController.AUTHENTICATION_ENDPOINT_PATH)
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {

    /**
     * Constant defining the base URL for authentication-related endpoints.
     */
    public static final String AUTHENTICATION_ENDPOINT_PATH = "/api/authentication";

    /**
     * Endpoint to retrieve information about the currently authenticated user.
     *
     * <p>Authorization:</p>
     * <ul>
     *     <li>Requires the user to be authenticated via OpenID Connect (OIDC).</li>
     * </ul>
     *
     * <p>Response:</p>
     * <ul>
     *     <li>Returns an HTTP 200 OK status with the authenticated user's details in JSON format.</li>
     * </ul>
     *
     * @param oidcUser the currently authenticated user, automatically injected by Spring Security.
     * @return a ResponseEntity containing the authenticated user's details.
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OidcUser> authenticate(@AuthenticationPrincipal OidcUser oidcUser) {
        return ResponseEntity.ok(oidcUser);
    }
}
