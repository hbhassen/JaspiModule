package com.example.idp;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

/**
 * <p>Central place for configuration values needed by the IdP JASPIC module.</p>
 * <p>Responsibilities:
 * <ul>
 *     <li>Expose immutable IdP configuration such as client identifier and endpoints.</li>
 *     <li>Validate required values early to avoid runtime misconfiguration.</li>
 *     <li>Offer builder helpers for loading values from property files or WildFly module options.</li>
 * </ul>
 * Usage example:
 * <pre>
 * {@code
 * ConfigProperties config = ConfigProperties.builder()
 *         .idpAuthorizeEndpoint("https://idp.example.com/auth")
 *         .clientId("app-client")
 *         .redirectUri("https://app.example.com/callback")
 *         .requestedScopes("openid profile")
 *         .additionalParameter("prompt", "login")
 *         .build();
 * }
 * </pre>
 * Interaction: Consumed by {@link IdpAuthRequestBuilder} for redirect URLs and
 * {@link IdpServerAuthModule} during initialization.</p>
 */
public final class ConfigProperties {

    private final String idpAuthorizeEndpoint;
    private final String clientId;
    private final String redirectUri;
    private final String requestedScopes;
    private final Map<String, String> additionalParameters;

    private ConfigProperties(Builder builder) {
        this.idpAuthorizeEndpoint = Objects.requireNonNull(builder.idpAuthorizeEndpoint, "idpAuthorizeEndpoint");
        this.clientId = Objects.requireNonNull(builder.clientId, "clientId");
        this.redirectUri = Objects.requireNonNull(builder.redirectUri, "redirectUri");
        this.requestedScopes = Optional.ofNullable(builder.requestedScopes).orElse("openid profile");
        this.additionalParameters = Collections.unmodifiableMap(new HashMap<>(builder.additionalParameters));
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Creates a {@link ConfigProperties} instance from standard Java properties.
     * @param properties configuration properties (must not be null)
     * @return immutable configuration object
     * @throws IdpConfigurationException when required entries are missing
     */
    public static ConfigProperties fromProperties(Properties properties) {
        Objects.requireNonNull(properties, "properties");
        return builder()
                .idpAuthorizeEndpoint(properties.getProperty("idp.authorize.endpoint"))
                .clientId(properties.getProperty("idp.client.id"))
                .redirectUri(properties.getProperty("idp.redirect.uri"))
                .requestedScopes(properties.getProperty("idp.requested.scopes"))
                .build();
    }

    /**
     * Builds a configuration from the map typically provided in {@code options} of the JASPIC initialize method.
     * Missing values will trigger {@link IdpConfigurationException} to surface configuration issues early.
     *
     * @param options initialization map coming from WildFly configuration
     * @return immutable configuration
     */
    public static ConfigProperties fromOptions(Map<String, Object> options) {
        Objects.requireNonNull(options, "options");
        return builder()
                .idpAuthorizeEndpoint((String) options.get("idp.authorize.endpoint"))
                .clientId((String) options.get("idp.client.id"))
                .redirectUri((String) options.get("idp.redirect.uri"))
                .requestedScopes((String) options.get("idp.requested.scopes"))
                .build();
    }

    public String getIdpAuthorizeEndpoint() {
        return idpAuthorizeEndpoint;
    }

    public String getClientId() {
        return clientId;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public String getRequestedScopes() {
        return requestedScopes;
    }

    public Map<String, String> getAdditionalParameters() {
        return additionalParameters;
    }

    /**
     * Builder supporting mandatory and optional configuration values.
     */
    public static final class Builder {
        private String idpAuthorizeEndpoint;
        private String clientId;
        private String redirectUri;
        private String requestedScopes;
        private final Map<String, String> additionalParameters = new HashMap<>();

        public Builder idpAuthorizeEndpoint(String endpoint) {
            this.idpAuthorizeEndpoint = endpoint;
            return this;
        }

        public Builder clientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        public Builder redirectUri(String redirectUri) {
            this.redirectUri = redirectUri;
            return this;
        }

        public Builder requestedScopes(String scopes) {
            this.requestedScopes = scopes;
            return this;
        }

        public Builder additionalParameter(String key, String value) {
            if (key != null && value != null) {
                this.additionalParameters.put(key, value);
            }
            return this;
        }

        public ConfigProperties build() {
            if (idpAuthorizeEndpoint == null || clientId == null || redirectUri == null) {
                throw new IdpConfigurationException("IdP configuration is incomplete. 'idpAuthorizeEndpoint', 'clientId' and 'redirectUri' are required.");
            }
            return new ConfigProperties(this);
        }
    }
}
