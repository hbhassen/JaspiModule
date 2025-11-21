package com.example.idp;

/**
 * <h2>Rôle de l'interface</h2>
 * <p>Abstraction pour valider un jeton IdP (SAMLResponse, id_token, access_token) et produire un {@link IdpUserInfo}.</p>
 */
public interface TokenValidator {

    /**
     * Valide la réponse IdP et construit l'identité applicative.
     *
     * @param token      jeton brut reçu (précondition : non nul)
     * @param requestUri URI cible sur laquelle la réponse a été reçue (utile pour vérifier l'audience ou le redirect URI)
     * @return informations d'identité validées
     * @throws IdpValidationException      jeton invalide
     * @throws IdpConfigurationException   configuration manquante
     * @throws IdpCommunicationException   problème de communication avec les services de l'IdP
     */
    IdpUserInfo validate(String token, String requestUri)
            throws IdpValidationException, IdpConfigurationException, IdpCommunicationException;
}
