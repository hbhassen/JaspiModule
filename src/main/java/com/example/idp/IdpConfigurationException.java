package com.example.idp;

/**
 * <h2>Rôle de la classe</h2>
 * <p>Exception levée en cas d'absence ou d'incohérence dans la configuration requise par le module JASPIC.</p>
 */
public class IdpConfigurationException extends Exception {

    /**
     * @param message message décrivant la configuration invalide
     */
    public IdpConfigurationException(final String message) {
        super(message);
    }

    /**
     * @param message message décrivant la configuration invalide
     * @param cause   cause d'origine
     */
    public IdpConfigurationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
