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

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SecurityConfig {

    private static final String[] AUTH_WHITELIST = {
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/resources/**",
            "/favicon.ico",
    };

    ClientRegistrationRepository clientRegistrationRepository;
    LinkedinOAuth2LoginSuccessHandler linkedinOAuth2LoginSuccessHandler;
    LinkedinOidUserService oidcUserService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        System.out.println("XXXXX Loading SecurityFilterChain...");

        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers(AUTH_WHITELIST).permitAll();
                    auth.anyRequest().authenticated();
                })
                .oauth2Login(oauth2Login -> oauth2Login
                        .authorizationEndpoint((authorizationEndpointConfig ->
                                authorizationEndpointConfig.authorizationRequestResolver(
                                        requestResolver(this.clientRegistrationRepository)
                                ))
                        )
                        .userInfoEndpoint(userInfoEndpoint ->
                                userInfoEndpoint
                                        .oidcUserService(oidcUserService)
                        )
                        .tokenEndpoint(tokenEndpoint -> tokenEndpoint
                                .accessTokenResponseClient(accessTokenResponseClient()))
                        .successHandler(linkedinOAuth2LoginSuccessHandler)
                        .failureHandler(new CustomOAuth2AuthenticationFailureHandler())
                )
                .build();
    }

    private static DefaultOAuth2AuthorizationRequestResolver requestResolver
            (ClientRegistrationRepository clientRegistrationRepository) {
        System.out.println("XXXXXXX  requestResolver");
        DefaultOAuth2AuthorizationRequestResolver requestResolver =
                new DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository,
                        "/oauth2/authorization");
        requestResolver.setAuthorizationRequestCustomizer(c ->
                c.attributes(stringObjectMap -> stringObjectMap.remove(OidcParameterNames.NONCE))
                        .parameters(params -> params.remove(OidcParameterNames.NONCE))
        );

        return requestResolver;
    }

    private OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient() {
        System.out.println("XXXXXXX in accessTokenResponseClient");
        DefaultAuthorizationCodeTokenResponseClient client = new DefaultAuthorizationCodeTokenResponseClient();
        RestTemplate restTemplate = new RestTemplate(Arrays.asList(
                new FormHttpMessageConverter(), new OAuth2AccessTokenResponseHttpMessageConverter()));

        restTemplate.getInterceptors().add((request, body, execution) -> {
            System.out.println("Request URI: " + request.getURI());
            System.out.println("Request Headers: " + request.getHeaders());
            return execution.execute(request, body);
        });
        restTemplate.setErrorHandler(new OAuth2ErrorResponseErrorHandler());
        client.setRestOperations(restTemplate);
        return client;
    }
}
