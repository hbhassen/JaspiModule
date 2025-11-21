package com.example.idp;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * <p>ConfigProperties encapsulates configuration parameters passed to the authentication module.</p>
 * <p>Responsibilities:</p>
 * <ul>
 *     <li>Centralize configuration keys for IdP endpoints and client identifiers.</li>
 *     <li>Expose type-safe accessors with defaults for common options.</li>
 *     <li>Validate mandatory settings when the module starts.</li>
 * </ul>
 * <p>Usage example:</p>
 * <pre>{@code
 * Map<String, String> options = Map.of(
 *     ConfigProperties.IDP_AUTH_ENDPOINT, "https://idp.example.com/auth",
 *     ConfigProperties.CLIENT_ID, "wildfly-module"
 * );
 * ConfigProperties properties = new ConfigProperties(options);
 * String endpoint = properties.getAuthEndpoint();
 * }</pre>
 * <p>Interaction diagram:</p>
 * <ol>
 *     <li>{@link IdpServerAuthModule#initialize(jakarta.security.auth.message.MessagePolicy, jakarta.security.auth.message.MessagePolicy, jakarta.security.auth.message.callback.CallbackHandler, Map)} injects the provided options.</li>
 *     <li>{@link IdpAuthRequestBuilder} reads endpoints and client information.</li>
 *     <li>{@link IdpResponseValidator} leverages secrets or keys to validate tokens.</li>
 * </ol>
 */
public class ConfigProperties {

    public static final String IDP_AUTH_ENDPOINT = "idp.auth.endpoint";
    public static final String IDP_TOKEN_ENDPOINT = "idp.token.endpoint";
    public static final String CLIENT_ID = "idp.client.id";
    public static final String CLIENT_SECRET = "idp.client.secret";
    public static final String REDIRECT_URI = "idp.redirect.uri";
    public static final String ROLE_CLAIM = "idp.role.claim";

    private final Map<String, String> rawOptions;

    public ConfigProperties(Map<String, String> options) {
        Objects.requireNonNull(options, "options must not be null");
        this.rawOptions = Collections.unmodifiableMap(new HashMap<>(options));
    }

    public Map<String, String> asMap() {
        return rawOptions;
    }

    public String getAuthEndpoint() {
        return rawOptions.getOrDefault(IDP_AUTH_ENDPOINT, "");
    }

    public String getTokenEndpoint() {
        return rawOptions.getOrDefault(IDP_TOKEN_ENDPOINT, "");
    }

    public String getClientId() {
        return rawOptions.getOrDefault(CLIENT_ID, "");
    }

    public String getClientSecret() {
        return rawOptions.getOrDefault(CLIENT_SECRET, "");
    }

    public String getRedirectUri() {
        return rawOptions.getOrDefault(REDIRECT_URI, "");
    }

    public String getRoleClaim() {
        return rawOptions.getOrDefault(ROLE_CLAIM, "roles");
    }

    /**
     * Validates minimal required configuration and throws an {@link IdpConfigurationException} if any mandatory
     * field is missing.
     */
    public void validate() {
        if (getAuthEndpoint().isEmpty()) {
            throw new IdpConfigurationException("Authentication endpoint is required");
        }
        if (getClientId().isEmpty()) {
            throw new IdpConfigurationException("Client identifier is required");
        }
        if (getRedirectUri().isEmpty()) {
            throw new IdpConfigurationException("Redirect URI is required");
        }
    }
}
