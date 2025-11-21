package com.example.idp;

import jakarta.servlet.http.HttpServletRequest;

/**
 * <h2>Rôle de la classe</h2>
 * <p>Valide la réponse provenant de l'IdP (SAMLResponse, id_token, etc.) et produit un {@link IdpUserInfo} prêt
 * à être converti en {@link IdpPrincipal}.</p>
 */
public class IdpResponseValidator {

    private final TokenValidator tokenValidator;

    /**
     * @param tokenValidator implémentation de validation spécifique au protocole
     */
    public IdpResponseValidator(final TokenValidator tokenValidator) {
        this.tokenValidator = tokenValidator;
    }

    /**
     * Extrait le jeton depuis la requête et le valide.
     *
     * @param request requête HTTP contenant la réponse IdP
     * @return informations d'utilisateur validées
     * @throws IdpValidationException    si le jeton est invalide
     * @throws IdpConfigurationException configuration manquante
     * @throws IdpCommunicationException erreur de communication avec l'IdP
     */
    public IdpUserInfo validateResponse(final HttpServletRequest request)
            throws IdpValidationException, IdpConfigurationException, IdpCommunicationException {
        final String samlResponse = request.getParameter("SAMLResponse");
        final String idToken = request.getParameter("id_token");
        final String token = samlResponse != null ? samlResponse : idToken;
        if (token == null || token.isBlank()) {
            throw new IdpValidationException("No IdP response found in request");
        }
        return tokenValidator.validate(token, request.getRequestURL().toString());
    }
}
