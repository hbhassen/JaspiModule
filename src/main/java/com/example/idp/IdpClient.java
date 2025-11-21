package com.example.idp;

import java.util.Map;

/**
 * Abstraction representing network communication with an external IdP.
 * Responsibilities include exchanging authorization codes for tokens or user information.
 */
public interface IdpClient {

    /**
     * Exchanges an authorization code or token for user attributes.
     *
     * @param payload payload received from the IdP callback (token, code, SAML response)
     * @return map of attributes to be consumed by {@link TokenValidator}
     * @throws IdpCommunicationException when the IdP cannot be reached
     */
    Map<String, String> exchange(String payload);
}
