package com.linkedme.security.converter;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A {@link HttpMessageConverter} for handling the serialization and deserialization of
 * {@link OAuth2AccessTokenResponse OAuth 2.0 Access Token Responses}.
 *
 * This class facilitates the conversion of HTTP messages to and from OAuth 2.0 token responses,
 * enabling proper communication between the client and authorization server.
 *
 * Key Features:
 * - Reads OAuth2AccessTokenResponse objects from JSON payloads.
 * - Writes OAuth2AccessTokenResponse objects into JSON payloads.
 */
public class OAuth2AccessTokenResponseHttpMessageConverter extends AbstractHttpMessageConverter<OAuth2AccessTokenResponse> {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    // Type reference for parsing JSON responses into Map<String, Object>
    private static final ParameterizedTypeReference<Map<String, Object>> PARAMETERIZED_RESPONSE_TYPE =
            new ParameterizedTypeReference<Map<String, Object>>() {
            };

    // Converter for JSON to Object and vice versa
    private GenericHttpMessageConverter<Object> jsonMessageConverter = new MappingJackson2HttpMessageConverter();

    // Converts Map<String, Object> to OAuth2AccessTokenResponse
    protected Converter<Map<String, Object>, OAuth2AccessTokenResponse> tokenResponseConverter =
            new OAuth2AccessTokenResponseConverter();

    // Converts OAuth2AccessTokenResponse to Map<String, String>
    protected Converter<OAuth2AccessTokenResponse, Map<String, String>> tokenResponseParametersConverter =
            new OAuth2AccessTokenResponseParametersConverter();

    /**
     * Constructor initializing supported media types and default charset.
     */
    public OAuth2AccessTokenResponseHttpMessageConverter() {
        super(DEFAULT_CHARSET, MediaType.APPLICATION_JSON, new MediaType("application", "*+json"));
    }

    /**
     * Checks if the converter supports the given class.
     *
     * @param clazz the class to check.
     * @return true if the class is supported, false otherwise.
     */
    @Override
    protected boolean supports(Class<?> clazz) {
        return OAuth2AccessTokenResponse.class.isAssignableFrom(clazz);
    }

    /**
     * Reads an OAuth2AccessTokenResponse from the input HTTP message.
     *
     * @param clazz the expected class type.
     * @param inputMessage the HTTP message containing the token response payload.
     * @return the deserialized OAuth2AccessTokenResponse object.
     * @throws IOException if an error occurs during deserialization.
     */
    @Override
    protected OAuth2AccessTokenResponse readInternal(Class<? extends OAuth2AccessTokenResponse> clazz, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {

        try {
            // Parse JSON payload into a Map
            @SuppressWarnings("unchecked")
            Map<String, Object> tokenResponseParameters = (Map<String, Object>) this.jsonMessageConverter.read(
                    PARAMETERIZED_RESPONSE_TYPE.getType(), null, inputMessage);
            // Convert Map to OAuth2AccessTokenResponse
            return this.tokenResponseConverter.convert(tokenResponseParameters);
        } catch (Exception ex) {
            throw new HttpMessageNotReadableException("An error occurred reading the OAuth 2.0 Access Token Response: " +
                    ex.getMessage(), ex, inputMessage);
        }
    }

    /**
     * Writes an OAuth2AccessTokenResponse to the output HTTP message.
     *
     * @param tokenResponse the token response to serialize.
     * @param outputMessage the HTTP message to write to.
     * @throws IOException if an error occurs during serialization.
     */
    @Override
    protected void writeInternal(OAuth2AccessTokenResponse tokenResponse, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {

        try {
            // Convert OAuth2AccessTokenResponse to a Map
            Map<String, String> tokenResponseParameters = this.tokenResponseParametersConverter.convert(tokenResponse);
            // Write the Map as a JSON payload
            this.jsonMessageConverter.write(
                    tokenResponseParameters, PARAMETERIZED_RESPONSE_TYPE.getType(), MediaType.APPLICATION_JSON, outputMessage);
        } catch (Exception ex) {
            throw new HttpMessageNotWritableException("An error occurred writing the OAuth 2.0 Access Token Response: " + ex.getMessage(), ex);
        }
    }

    /**
     * Sets a custom Converter for parsing OAuth 2.0 Access Token Response parameters into an OAuth2AccessTokenResponse.
     *
     * @param tokenResponseConverter the custom Converter.
     */
    public final void setTokenResponseConverter(Converter<Map<String, Object>, OAuth2AccessTokenResponse> tokenResponseConverter) {
        Assert.notNull(tokenResponseConverter, "tokenResponseConverter cannot be null");
        this.tokenResponseConverter = tokenResponseConverter;
    }

    /**
     * Sets a custom Converter for converting an OAuth2AccessTokenResponse into a Map representation.
     *
     * @param tokenResponseParametersConverter the custom Converter.
     */
    public final void setTokenResponseParametersConverter(Converter<OAuth2AccessTokenResponse, Map<String, String>> tokenResponseParametersConverter) {
        Assert.notNull(tokenResponseParametersConverter, "tokenResponseParametersConverter cannot be null");
        this.tokenResponseParametersConverter = tokenResponseParametersConverter;
    }

    /**
     * A Converter implementation for transforming OAuth 2.0 Access Token Response parameters into an OAuth2AccessTokenResponse.
     */
    private static class OAuth2AccessTokenResponseConverter implements Converter<Map<String, Object>, OAuth2AccessTokenResponse> {
        private static final Set<String> TOKEN_RESPONSE_PARAMETER_NAMES = Stream.of(
                OAuth2ParameterNames.ACCESS_TOKEN,
                OAuth2ParameterNames.TOKEN_TYPE,
                OAuth2ParameterNames.EXPIRES_IN,
                OAuth2ParameterNames.REFRESH_TOKEN,
                OAuth2ParameterNames.SCOPE).collect(Collectors.toSet());

        @Override
        public OAuth2AccessTokenResponse convert(Map<String, Object> tokenResponseParameters) {
            // Extract access token value
            String accessToken = tokenResponseParameters.get(OAuth2ParameterNames.ACCESS_TOKEN).toString();

            // Parse and calculate expiration time
            long expiresIn = 0;
            if (tokenResponseParameters.containsKey(OAuth2ParameterNames.EXPIRES_IN)) {
                try {
                    expiresIn = Long.valueOf(tokenResponseParameters.get(OAuth2ParameterNames.EXPIRES_IN).toString());
                } catch (NumberFormatException ex) {
                    // Log or handle parsing error if needed
                }
            }

            // Extract and parse scopes
            Set<String> scopes = Collections.emptySet();
            if (tokenResponseParameters.containsKey(OAuth2ParameterNames.SCOPE)) {
                String scope = tokenResponseParameters.get(OAuth2ParameterNames.SCOPE).toString();
                scopes = Arrays.stream(StringUtils.delimitedListToStringArray(scope, " ")).collect(Collectors.toSet());
            }

            // Collect additional parameters
            Map<String, Object> additionalParameters = new LinkedHashMap<>();
            tokenResponseParameters.entrySet().stream()
                    .filter(e -> !TOKEN_RESPONSE_PARAMETER_NAMES.contains(e.getKey()))
                    .forEach(e -> additionalParameters.put(e.getKey(), e.getValue()));

            // Build the OAuth2AccessTokenResponse object
            return OAuth2AccessTokenResponse.withToken(accessToken)
                    .tokenType(OAuth2AccessToken.TokenType.BEARER)
                    .expiresIn(expiresIn)
                    .scopes(scopes)
                    .additionalParameters(additionalParameters)
                    .build();
        }
    }

    /**
     * A Converter implementation for transforming an OAuth2AccessTokenResponse into a Map representation.
     */
    private static class OAuth2AccessTokenResponseParametersConverter implements Converter<OAuth2AccessTokenResponse, Map<String, String>> {

        @Override
        public Map<String, String> convert(OAuth2AccessTokenResponse tokenResponse) {
            Map<String, String> parameters = new HashMap<>();

            // Calculate expiration time in seconds
            long expiresIn = -1;
            if (tokenResponse.getAccessToken().getExpiresAt() != null) {
                expiresIn = ChronoUnit.SECONDS.between(Instant.now(), tokenResponse.getAccessToken().getExpiresAt());
            }

            // Populate standard token response parameters
            parameters.put(OAuth2ParameterNames.ACCESS_TOKEN, tokenResponse.getAccessToken().getTokenValue());
            parameters.put(OAuth2ParameterNames.TOKEN_TYPE, tokenResponse.getAccessToken().getTokenType().getValue());
            parameters.put(OAuth2ParameterNames.EXPIRES_IN, String.valueOf(expiresIn));
            if (!CollectionUtils.isEmpty(tokenResponse.getAccessToken().getScopes())) {
                parameters.put(OAuth2ParameterNames.SCOPE,
                        StringUtils.collectionToDelimitedString(tokenResponse.getAccessToken().getScopes(), " "));
            }

            // Add refresh token if available
            if (tokenResponse.getRefreshToken() != null) {
                parameters.put(OAuth2ParameterNames.REFRESH_TOKEN, tokenResponse.getRefreshToken().getTokenValue());
            }

            // Add any additional parameters
            if (!CollectionUtils.isEmpty(tokenResponse.getAdditionalParameters())) {
                tokenResponse.getAdditionalParameters().entrySet().stream()
                        .forEach(e -> parameters.put(e.getKey(), e.getValue().toString()));
            }

            return parameters;
        }
    }
}
