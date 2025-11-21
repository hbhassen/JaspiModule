package com.example.idp;

import java.util.Map;

/**
 * Validates IdP responses and converts them into {@link IdpUserInfo} instances.
 */
public interface TokenValidator {

    /**
     * Validates the incoming payload and extracts user information.
     *
     * @param attributes attributes returned by {@link IdpClient}
     * @param roleClaimName claim or attribute containing role names
     * @return user information used to build the security principal
     * @throws IdpValidationException when validation fails
     */
    IdpUserInfo validate(Map<String, String> attributes, String roleClaimName);
}
