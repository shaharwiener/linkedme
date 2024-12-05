package com.linkedme.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing user-related endpoints.
 *
 * <p>This controller defines the API endpoints for interacting with user-specific features
 * in the application. It ensures proper authorization and serves JSON responses.</p>
 *
 * <p>Annotations:</p>
 * <ul>
 *     <li>@RestController: Marks this class as a Spring REST controller, making it capable
 *     of handling HTTP requests and returning HTTP responses.</li>
 *     <li>@RequestMapping: Maps the base URL for all endpoints in this controller to "/users".</li>
 *     <li>@RequiredArgsConstructor: Generates a constructor for final fields, allowing dependency injection.</li>
 * </ul>
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    /**
     * Endpoint to retrieve a greeting for users.
     *
     * <p>Authorization:</p>
     * <ul>
     *     <li>@PreAuthorize: Ensures that only users with the role "ROLE_USER" can access this endpoint.</li>
     * </ul>
     *
     * <p>Response:</p>
     * <ul>
     *     <li>Returns an HTTP 200 OK status with a JSON message "Hello User".</li>
     *     <li>Produces content in JSON format.</li>
     * </ul>
     *
     * @return a ResponseEntity containing a greeting message.
     */
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getUserS() {
        return ResponseEntity.ok("Hello User");
    }
}