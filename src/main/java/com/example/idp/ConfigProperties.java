package com.example.idp;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * <h2>Rôle de la classe</h2>
 * <p>Centralise la configuration du module (endpoints IdP, clientId, secrets, URLs de redirection). Les valeurs
 * proviennent généralement du fichier module JBoss ou d'une configuration externe.</p>
 *
 * <h3>Responsabilités</h3>
 * <ul>
 *     <li>Expose des accesseurs typés pour les propriétés principales.</li>
 *     <li>Valide la présence des propriétés critiques.</li>
 * </ul>
 *
 * <h3>Exemple d'utilisation</h3>
 * <pre>{@code
 * ConfigProperties properties = new ConfigProperties(Map.of("idp.authorization.endpoint", "https://idp/auth"));
 * String endpoint = properties.getIdpAuthorizationEndpoint();
 * }
 * </pre>
 *
 * <h3>Schéma d'interaction</h3>
 * <p>Initialisé par {@link IdpServerAuthModule#initialize(javax.security.auth.message.MessagePolicy, javax.security.auth.message.MessagePolicy, javax.security.auth.Subject, java.util.Map)}
 * puis partagé avec {@link IdpAuthRequestBuilder} et {@link IdpResponseValidator}.</p>
 */
public class ConfigProperties {

    private final Map<String, String> properties;

    /**
     * @param properties carte de configuration brute (jamais nulle)
     */
    public ConfigProperties(final Map<String, String> properties) {
        this.properties = properties == null ? Collections.emptyMap() : Collections.unmodifiableMap(properties);
    }

    /**
     * Récupère la propriété requise ou lève une exception claire.
     *
     * @param key nom de la propriété
     * @return valeur non vide
     * @throws IdpConfigurationException si absente ou vide
     */
    public String require(final String key) throws IdpConfigurationException {
        final String value = properties.get(key);
        if (value == null || value.isBlank()) {
            throw new IdpConfigurationException("Missing configuration property: " + key);
        }
        return value;
    }

    /**
     * Récupère une valeur optionnelle.
     *
     * @param key nom de la propriété
     * @return valeur optionnelle
     */
    public Optional<String> getOptional(final String key) {
        return Optional.ofNullable(properties.get(key)).filter(v -> !v.isBlank());
    }

    /**
     * @return endpoint d'autorisation IdP
     * @throws IdpConfigurationException si absent
     */
    public String getIdpAuthorizationEndpoint() throws IdpConfigurationException {
        return require("idp.authorization.endpoint");
    }

    /**
     * @return endpoint de logout si fourni
     */
    public Optional<String> getIdpLogoutEndpoint() {
        return getOptional("idp.logout.endpoint");
    }

    /**
     * @return URI de retour après authentification
     * @throws IdpConfigurationException si absent
     */
    public String getRedirectUri() throws IdpConfigurationException {
        return require("app.redirect.uri");
    }

    /**
     * @return identifiant client
     * @throws IdpConfigurationException si absent
     */
    public String getClientId() throws IdpConfigurationException {
        return require("idp.client.id");
    }

    /**
     * @return secret client éventuel
     */
    public Optional<String> getClientSecret() {
        return getOptional("idp.client.secret");
    }
}
