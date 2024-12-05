package com.linkedme.security.config;

import com.linkedme.security.handler.CustomOAuth2AuthenticationFailureHandler;
import com.linkedme.security.handler.LinkedinOAuth2LoginSuccessHandler;
import com.linkedme.security.service.LinkedinOidUserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

/**
 * Security configuration class for the LinkedMe application.
 * This class sets up Spring Security to handle OAuth2 login with LinkedIn.
 *
 * Key Features:
 * - Disables CSRF protection for simplicity.
 * - Configures endpoints that are accessible without authentication.
 * - Implements OAuth2 login with LinkedIn, customizing the request resolver and success/failure handlers.
 * - Uses a custom OAuth2 token response client to handle LinkedIn's specific token requirements.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SecurityConfig {

    /**
     * List of endpoints accessible without authentication.
     */
    private static final String[] AUTH_WHITELIST = {
            "/v3/api-docs/**", // OpenAPI documentation
            "/swagger-ui/**", // Swagger UI for API testing
            "/resources/**", // Static resources like CSS and JS files
            "/favicon.ico", // Default browser favicon
    };

    ClientRegistrationRepository clientRegistrationRepository;
    LinkedinOAuth2LoginSuccessHandler linkedinOAuth2LoginSuccessHandler;
    LinkedinOidUserService oidcUserService;

    /**
     * Configures the SecurityFilterChain for the application.
     *
     * @param http the HttpSecurity object to configure.
     * @return a configured SecurityFilterChain.
     * @throws Exception if any error occurs during configuration.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF protection for simplicity
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers(AUTH_WHITELIST).permitAll(); // Allow access to whitelisted endpoints
                    auth.anyRequest().authenticated(); // Require authentication for all other requests
                })
                .oauth2Login(oauth2Login -> oauth2Login
                        .authorizationEndpoint((authorizationEndpointConfig ->
                                authorizationEndpointConfig.authorizationRequestResolver(
                                        requestResolver(this.clientRegistrationRepository) // Use custom request resolver
                                ))
                        )
                        .userInfoEndpoint(userInfoEndpoint ->
                                userInfoEndpoint
                                        .oidcUserService(oidcUserService) // Use custom user service for OpenID Connect
                        )
                        .tokenEndpoint(tokenEndpoint -> tokenEndpoint
                                .accessTokenResponseClient(accessTokenResponseClient())) // Use custom token response client
                        .successHandler(linkedinOAuth2LoginSuccessHandler) // Handle successful login
                        .failureHandler(new CustomOAuth2AuthenticationFailureHandler()) // Handle login failures
                )
                .build(); // Build the SecurityFilterChain
    }

    /**
     * Creates a custom DefaultOAuth2AuthorizationRequestResolver to customize OAuth2 requests.
     *
     * This resolver removes the 'nonce' parameter which is not supported by LinkedIn.
     *
     * @param clientRegistrationRepository the client registration repository.
     * @return a configured DefaultOAuth2AuthorizationRequestResolver.
     */
    private static DefaultOAuth2AuthorizationRequestResolver requestResolver
    (ClientRegistrationRepository clientRegistrationRepository) {
        DefaultOAuth2AuthorizationRequestResolver requestResolver =
                new DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository,
                        "/oauth2/authorization"); // Define base URI for authorization requests
        requestResolver.setAuthorizationRequestCustomizer(c ->
                c.attributes(stringObjectMap -> stringObjectMap.remove(OidcParameterNames.NONCE)) // Remove 'nonce' attribute
                        .parameters(params -> params.remove(OidcParameterNames.NONCE)) // Remove 'nonce' parameter
        );

        return requestResolver;
    }

    /**
     * Configures a custom OAuth2AccessTokenResponseClient to handle LinkedIn's token response.
     *
     * @return a configured OAuth2AccessTokenResponseClient.
     */
    private OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient() {
        DefaultAuthorizationCodeTokenResponseClient client = new DefaultAuthorizationCodeTokenResponseClient();
        RestTemplate restTemplate = new RestTemplate(Arrays.asList(
                new FormHttpMessageConverter(), // Handles form data
                new OAuth2AccessTokenResponseHttpMessageConverter())); // Handles OAuth2 token responses

        restTemplate.getInterceptors().add((request, body, execution) -> {
            return execution.execute(request, body); // Pass the request through unchanged
        });
        restTemplate.setErrorHandler(new OAuth2ErrorResponseErrorHandler()); // Handle OAuth2-specific errors
        client.setRestOperations(restTemplate); // Set custom RestTemplate for the client
        return client;
    }
}
