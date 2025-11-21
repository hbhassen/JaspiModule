package com.example.idp;

import java.util.Map;

/**
 * <p>Validates IdP responses by delegating network calls to {@link IdpClient} and cryptographic checks to {@link TokenValidator}.</p>
 * <p>Responsibilities:</p>
 * <ul>
 *     <li>Detect whether a response is a SAMLResponse, an id_token or an authorization code.</li>
 *     <li>Delegate the heavy validation to injected collaborators.</li>
 *     <li>Return a normalized {@link IdpUserInfo} structure.</li>
 * </ul>
 */
public class IdpResponseValidator {

    private final IdpClient idpClient;
    private final TokenValidator tokenValidator;
    private final ConfigProperties properties;

    public IdpResponseValidator(IdpClient idpClient, TokenValidator tokenValidator, ConfigProperties properties) {
        this.idpClient = idpClient;
        this.tokenValidator = tokenValidator;
        this.properties = properties;
    }

    /**
     * Validates the given response payload.
     *
     * @param samlResponse encoded SAML response, may be null
     * @param token incoming id_token or authorization code
     * @return user information
     */
    public IdpUserInfo validateResponse(String samlResponse, String token) {
        String payload = samlResponse != null ? samlResponse : token;
        if (payload == null || payload.isBlank()) {
            throw new IdpValidationException("No response payload to validate");
        }
        Map<String, String> attributes = idpClient.exchange(payload);
        return tokenValidator.validate(attributes, properties.getRoleClaim());
    }
}
