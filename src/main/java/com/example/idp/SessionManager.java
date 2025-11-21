package com.example.idp;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.Optional;

/**
 * <p>Coordinates storage of {@link IdpUserInfo} inside the HTTP session to keep authentication state.</p>
 * Responsibilities:
 * <ul>
 *     <li>Persist user identity after successful IdP validation.</li>
 *     <li>Retrieve identity to support seamless re-authentication.</li>
 *     <li>Clear state during logout or {@link jakarta.security.auth.message.module.ServerAuthModule#cleanSubject}.</li>
 * </ul>
 */
public class SessionManager {

    static final String SESSION_KEY = SessionManager.class.getName() + ".principal";

    public void storeUser(HttpServletRequest request, IdpUserInfo userInfo) {
        HttpSession session = request.getSession(true);
        session.setAttribute(SESSION_KEY, userInfo);
    }

    public Optional<IdpUserInfo> readUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return Optional.empty();
        }
        Object attribute = session.getAttribute(SESSION_KEY);
        if (attribute instanceof IdpUserInfo) {
            return Optional.of((IdpUserInfo) attribute);
        }
        return Optional.empty();
    }

    public void clear(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute(SESSION_KEY);
        }
    }
}
