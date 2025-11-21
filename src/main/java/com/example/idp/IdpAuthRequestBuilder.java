package com.example.idp;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * <p>Builds URLs used to redirect the browser to the Identity Provider.</p>
 * <p>Responsibilities:</p>
 * <ul>
 *     <li>Generate authorization URLs based on configuration.</li>
 *     <li>Allow extension for SAML or OIDC specific parameters.</li>
 * </ul>
 * <p>Usage example:</p>
 * <pre>{@code
 * IdpAuthRequestBuilder builder = new IdpAuthRequestBuilder(properties);
 * String redirect = builder.buildAuthRequest("state-token");
 * }</pre>
 * <p>Interaction diagram:</p>
 * <ol>
 *     <li>{@link IdpServerAuthModule} calls {@link #buildAuthRequest(String)} when authentication is required.</li>
 *     <li>The generated URL is sent back with {@link jakarta.security.auth.message.AuthStatus#SEND_CONTINUE}.</li>
 * </ol>
 */
public class IdpAuthRequestBuilder {

    private final ConfigProperties properties;

    public IdpAuthRequestBuilder(ConfigProperties properties) {
        this.properties = properties;
    }

    /**
     * Builds a redirect URL to the IdP authorization endpoint.
     *
     * @param state random state token used to correlate the response
     * @return full redirect URI
     */
    public String buildAuthRequest(String state) {
        StringBuilder builder = new StringBuilder(properties.getAuthEndpoint())
            .append("?client_id=").append(url(properties.getClientId()))
            .append("&redirect_uri=").append(url(properties.getRedirectUri()))
            .append("&response_type=code")
            .append("&scope=").append(url("openid profile"));
        if (state != null && !state.isBlank()) {
            builder.append("&state=").append(url(state));
        }
        return builder.toString();
    }

    private String url(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
