package com.example.idp;

import jakarta.servlet.http.HttpServletRequest;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.StringJoiner;

/**
 * <h2>Rôle de la classe</h2>
 * <p>Construit l'URL de redirection vers l'Identity Provider à partir des paramètres fournis par {@link IdpClient}
 * et de la configuration.</p>
 */
public class IdpAuthRequestBuilder {

    private final ConfigProperties configProperties;
    private final IdpClient idpClient;

    /**
     * @param configProperties configuration du module (précondition : non nulle)
     * @param idpClient client IdP pour enrichir la requête (précondition : non nul)
     */
    public IdpAuthRequestBuilder(final ConfigProperties configProperties, final IdpClient idpClient) {
        this.configProperties = configProperties;
        this.idpClient = idpClient;
    }

    /**
     * Génère l'URL complète de redirection.
     *
     * @param request requête HTTP courante
     * @return URL vers l'IdP
     * @throws IdpConfigurationException si une propriété requise est absente
     */
    public String buildRedirectUrl(final HttpServletRequest request) throws IdpConfigurationException {
        final String base = configProperties.getIdpAuthorizationEndpoint();
        final Map<String, String> parameters = idpClient.prepareAuthorizationRequest(request);
        final StringJoiner joiner = new StringJoiner("&", base + "?", "");
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            joiner.add(encode(entry.getKey()) + "=" + encode(entry.getValue()));
        }
        return joiner.toString();
    }

    private String encode(final String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            // UTF-8 always available
            throw new IllegalStateException(e);
        }
    }
}
