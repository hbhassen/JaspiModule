package com.example.idp;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.Optional;

/**
 * <h2>Rôle de la classe</h2>
 * <p>Gère la persistance des informations d'authentification dans la session HTTP afin d'éviter les revalidations
 * systématiques.</p>
 *
 * <h3>Responsabilités</h3>
 * <ul>
 *     <li>Stocker et récupérer {@link IdpUserInfo} dans la session.</li>
 *     <li>Invalider la session lorsque l'utilisateur est déconnecté.</li>
 * </ul>
 */
public class SessionManager {

    static final String SESSION_ATTRIBUTE = "IDP_JASPIC_USER_INFO";

    /**
     * Sauvegarde l'identité dans la session HTTP.
     *
     * @param request requête courante (précondition : non nulle)
     * @param userInfo informations d'identité à stocker (précondition : non nulle)
     */
    public void store(final HttpServletRequest request, final IdpUserInfo userInfo) {
        final HttpSession session = request.getSession(true);
        session.setAttribute(SESSION_ATTRIBUTE, userInfo);
    }

    /**
     * Récupère l'identité stockée si elle existe.
     *
     * @param request requête courante
     * @return identité optionnelle
     */
    public Optional<IdpUserInfo> retrieve(final HttpServletRequest request) {
        final HttpSession session = request.getSession(false);
        if (session == null) {
            return Optional.empty();
        }
        final Object attribute = session.getAttribute(SESSION_ATTRIBUTE);
        if (attribute instanceof IdpUserInfo info) {
            return Optional.of(info);
        }
        return Optional.empty();
    }

    /**
     * Invalide la session HTTP si présente.
     *
     * @param request requête courante
     */
    public void invalidate(final HttpServletRequest request) {
        final HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }
}
