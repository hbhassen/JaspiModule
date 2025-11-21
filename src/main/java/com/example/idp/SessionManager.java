package com.example.idp;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * <p>SessionManager encapsulates persistence of authentication state between requests.</p>
 * <p>Responsibilities:</p>
 * <ul>
 *     <li>Store {@link IdpPrincipal} in the HTTP session.</li>
 *     <li>Retrieve the principal to skip redundant authentication.</li>
 *     <li>Clear state during logout or {@link jakarta.security.auth.message.module.ServerAuthModule#cleanSubject}.</li>
 * </ul>
 */
public class SessionManager {

    static final String PRINCIPAL_SESSION_KEY = "IDP_PRINCIPAL";

    public void storePrincipal(HttpServletRequest request, IdpPrincipal principal) {
        HttpSession session = request.getSession(true);
        session.setAttribute(PRINCIPAL_SESSION_KEY, principal);
    }

    public IdpPrincipal getPrincipal(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        Object attribute = session.getAttribute(PRINCIPAL_SESSION_KEY);
        if (attribute instanceof IdpPrincipal principal) {
            return principal;
        }
        return null;
    }

    public void clearPrincipal(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute(PRINCIPAL_SESSION_KEY);
        }
    }
}
