package com.example.idp;

import jakarta.servlet.http.HttpServletRequest;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.StringJoiner;

/**
 * Composes authentication requests to the Identity Provider.
 * <p>Responsibilities:</p>
 * <ul>
 *     <li>Create redirect URLs with required parameters (client_id, redirect_uri, scope, state).</li>
 *     <li>Allow additional parameters configured in {@link ConfigProperties}.</li>
 * </ul>
 * Typical usage inside {@link IdpServerAuthModule#validateRequest} when authentication is required.</p>
 */
public class IdpAuthRequestBuilder implements IdpClient {

    private final ConfigProperties configProperties;

    public IdpAuthRequestBuilder(ConfigProperties configProperties) {
        this.configProperties = configProperties;
    }

    @Override
    public String buildAuthenticationRedirect(HttpServletRequest request) {
        return buildAuthRequestUrl(request);
    }

    /**
     * Builds an IdP authorization URL for the incoming request.
     *
     * @param request HTTP request in progress
     * @return redirect URL pointing to the IdP authorize endpoint
     */
    public String buildAuthRequestUrl(HttpServletRequest request) {
        String state = request.getSession(true).getId();
        StringJoiner joiner = new StringJoiner("&", configProperties.getIdpAuthorizeEndpoint() + "?", "");
        joiner.add(param("client_id", configProperties.getClientId()));
        joiner.add(param("redirect_uri", configProperties.getRedirectUri()));
        joiner.add(param("response_type", "code"));
        joiner.add(param("scope", configProperties.getRequestedScopes()));
        joiner.add(param("state", state));

        for (Map.Entry<String, String> entry : configProperties.getAdditionalParameters().entrySet()) {
            joiner.add(param(entry.getKey(), entry.getValue()));
        }
        return joiner.toString();
    }

    private String param(String name, String value) {
        try {
            return name + "=" + URLEncoder.encode(value, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            // UTF-8 is always supported; wrap for completeness.
            throw new IllegalStateException("UTF-8 encoding not supported", e);
        }
    }
}
