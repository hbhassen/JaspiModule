package com.example.idp;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Abstraction over the Identity Provider client capabilities.
 * <p>Responsibilities:</p>
 * <ul>
 *     <li>Construct authentication URLs when a redirect is necessary.</li>
 *     <li>Optionally perform back-channel exchanges if required by the IdP.</li>
 * </ul>
 * Typical usage is through {@link IdpAuthRequestBuilder} which can be injected into an implementation.
 */
public interface IdpClient {

    /**
     * Builds an IdP authentication URL for the given request.
     *
     * @param request current HTTP servlet request
     * @return fully qualified redirect URL to the IdP
     */
    String buildAuthenticationRedirect(HttpServletRequest request);
}
