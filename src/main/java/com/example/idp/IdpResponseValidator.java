package com.example.idp;

import jakarta.servlet.http.HttpServletRequest;

import org.jboss.logging.Logger;

import java.util.Optional;

/**
 * Validates inbound IdP responses and converts them into {@link IdpUserInfo}.
 * <p>Responsibilities:</p>
 * <ul>
 *     <li>Detect whether a SAML or OpenID Connect token is present.</li>
 *     <li>Delegate cryptographic verification to a pluggable {@link TokenValidator}.</li>
 *     <li>Provide clear validation errors.</li>
 * </ul>
 */
public class IdpResponseValidator {

    private final TokenValidator tokenValidator;
    private final Logger logger = LoggerUtils.logger(IdpResponseValidator.class);

    public IdpResponseValidator(TokenValidator tokenValidator) {
        this.tokenValidator = tokenValidator;
    }

    /**
     * Validates the IdP response parameters extracted from the HTTP request.
     *
     * @param samlResponse optional SAML response parameter
     * @param idToken optional OpenID Connect id_token
     * @param request current request used for logging context
     * @return parsed {@link IdpUserInfo}
     * @throws IdpValidationException when no token is present or validation fails
     */
    public IdpUserInfo validateResponse(String samlResponse, String idToken, HttpServletRequest request) throws IdpValidationException {
        Optional<String> token = selectToken(samlResponse, idToken);
        String tokenType = samlResponse != null ? "SAMLResponse" : "id_token";
        LoggerUtils.logValidation(logger, tokenType);
        try {
            return tokenValidator.validate(token.orElseThrow(() -> new IdpValidationException("No IdP response provided")));
        } catch (IdpValidationException ex) {
            logger.warnf(ex, "Failed to validate %s from %s", tokenType, request.getRemoteAddr());
            throw ex;
        }
    }

    private Optional<String> selectToken(String samlResponse, String idToken) {
        if (samlResponse != null && !samlResponse.isEmpty()) {
            return Optional.of(samlResponse);
        }
        if (idToken != null && !idToken.isEmpty()) {
            return Optional.of(idToken);
        }
        return Optional.empty();
    }
}
