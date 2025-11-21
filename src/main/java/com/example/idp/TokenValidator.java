package com.example.idp;

/**
 * Strategy interface to validate security tokens received from the IdP.
 * Implementations can validate SAML assertions, JWT id_tokens, or opaque tokens.
 */
public interface TokenValidator {

    /**
     * Validates the provided token and extracts {@link IdpUserInfo}.
     *
     * @param token raw token value (SAMLResponse, id_token, etc.)
     * @return parsed user information
     * @throws IdpValidationException when validation fails
     */
    IdpUserInfo validate(String token) throws IdpValidationException;
}
