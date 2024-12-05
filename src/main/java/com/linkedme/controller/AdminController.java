package com.linkedme.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing admin-related endpoints.
 *
 * <p>This controller defines the API endpoints for interacting with admin-specific features
 * in the application. It ensures proper authorization and serves JSON responses.</p>
 *
 * <p>Annotations:</p>
 * <ul>
 *     <li>@RestController: Marks this class as a Spring REST controller, making it capable
 *     of handling HTTP requests and returning HTTP responses.</li>
 *     <li>@RequestMapping: Maps the base URL for all endpoints in this controller to "/admins".</li>
 * </ul>
 */
@RestController
@RequestMapping("/admins")
public class AdminController {

    /**
     * Endpoint to retrieve a greeting for admins.
     *
     * <p>Authorization:</p>
     * <ul>
     *     <li>@PreAuthorize: Ensures that only users with the role "ROLE_ADMIN" can access this endpoint.</li>
     * </ul>
     *
     * <p>Response:</p>
     * <ul>
     *     <li>Returns an HTTP 200 OK status with a JSON message "Hello Admin".</li>
     *     <li>Produces content in JSON format.</li>
     * </ul>
     *
     * @return a ResponseEntity containing a greeting message for admins.
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getUserS() {
        return ResponseEntity.ok("Hello Admin");
    }
}