package com.example.idp;

import java.util.Map;

/**
 * <h2>Rôle de l'interface</h2>
 * <p>Abstraction permettant d'envoyer des requêtes à l'Identity Provider ou de préparer les paramètres nécessaires
 * (ex : génération d'URL d'autorisation, récupération de métadonnées).</p>
 *
 * <h3>Responsabilités</h3>
 * <ul>
 *     <li>Construire les paramètres spécifiques au protocole (SAML, OIDC).</li>
 *     <li>Encapsuler la logique réseau ou cryptographique hors du module JASPIC.</li>
 * </ul>
 */
public interface IdpClient {

    /**
     * Prépare les paramètres envoyés à l'IdP pour initier l'authentification.
     *
     * @param requestContext contexte applicatif ou HTTP utile pour contextualiser la demande
     * @return map de paramètres à injecter dans une URL de redirection (jamais nulle)
     * @throws IdpConfigurationException configuration manquante
     */
    Map<String, String> prepareAuthorizationRequest(Object requestContext) throws IdpConfigurationException;
}
