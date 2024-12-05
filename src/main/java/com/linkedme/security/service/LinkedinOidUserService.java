package com.linkedme.security.service;

import com.linkedme.persistence.entity.Role;
import com.linkedme.persistence.entity.User;
import com.linkedme.persistence.entity.UserRole;
import com.linkedme.persistence.repository.RoleRepository;
import com.linkedme.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Custom service for processing OAuth2 user information from LinkedIn.
 *
 * <p>This service integrates with LinkedIn's OAuth2 login flow to handle user information
 * and manage user data in the system. It extends the default OIDC user service provided by Spring Security
 * and adds custom behavior for handling roles and user creation.</p>
 *
 * <p>Key responsibilities:</p>
 * <ul>
 *     <li>Loads user information from LinkedIn using the OIDC protocol.</li>
 *     <li>Retrieves or creates users in the local database based on the received email.</li>
 *     <li>Maps LinkedIn roles to application-specific authorities.</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class LinkedinOidUserService implements OAuth2UserService<OidcUserRequest, OidcUser> {

    // Repository for managing user data
    UserRepository userRepository;

    // Repository for managing role data
    RoleRepository roleRepository;

    /**
     * Loads user information from the OIDC request.
     *
     * @param userRequest the OIDC user request containing user details.
     * @return an OIDC user with mapped authorities and roles.
     * @throws OAuth2AuthenticationException if an error occurs during user processing.
     */
    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        // Use the default OIDC user service to load the user
        final OidcUserService delegate = new OidcUserService();
        OidcUser oidcUser = delegate.loadUser(userRequest);

        // Initialize authorities with the ones provided by the OIDC user
        List<GrantedAuthority> mappedAuthorities = new ArrayList<>(oidcUser.getAuthorities());

        // Extract the user's email from the OIDC user attributes
        String email = Objects.requireNonNull(oidcUser.getAttribute("email"));

        // Retrieve the user from the database or create a new one
        var user = userRepository.findByEmail(email)
                .orElseGet(() -> createNewUser(oidcUser));

        // Add roles to the authorities from the database
        user.getRoles().forEach(role -> mappedAuthorities.add(new SimpleGrantedAuthority(role.getRole().getName())));

        // Return a new OIDC user with the updated authorities
        return new DefaultOidcUser(mappedAuthorities, oidcUser.getIdToken(), oidcUser.getUserInfo());
    }

    /**
     * Creates a new user in the system if the email is not found in the database.
     *
     * @param oAuth2User the OAuth2 user containing user details.
     * @return the newly created user entity.
     */
    private User createNewUser(OAuth2User oAuth2User) {
        // Extract the user's name and email from the OAuth2 user attributes
        String name = Objects.requireNonNull(oAuth2User.getAttribute("name"));
        String email = Objects.requireNonNull(oAuth2User.getAttribute("email"));

        // Retrieve the default user role from the database
        var role = roleRepository.findByName(Role.ROLE_USER).orElseThrow(() -> new RuntimeException("Role not found"));

        // Build a new user entity with the extracted details
        var user = User.builder()
                .name(name)
                .email(email)
                .build();

        // Assign the default role to the user
        user.setRoles(List.of(UserRole.builder().user(user).role(role).build()));

        // Save the new user to the database and return it
        return userRepository.save(user);
    }
}