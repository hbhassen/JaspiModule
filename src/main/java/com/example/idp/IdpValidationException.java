package com.example.idp;

/**
 * <h2>Rôle de la classe</h2>
 * <p>Exception fonctionnelle indiquant un problème de validation de la réponse IdP (signature invalide, audience
 * incorrecte, expiration, etc.).</p>
 */
public class IdpValidationException extends Exception {

    /**
     * @param message description fonctionnelle
     */
    public IdpValidationException(final String message) {
        super(message);
    }

    /**
     * @param message description fonctionnelle
     * @param cause   cause technique sous-jacente
     */
    public IdpValidationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
