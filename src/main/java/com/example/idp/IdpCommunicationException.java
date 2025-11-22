package com.example.idp;

/**
 * <h2>Rôle de la classe</h2>
 * <p>Exception technique utilisée lorsque la communication avec l'Identity Provider échoue (réseau indisponible,
 * time-out, réponse invalide).</p>
 */
public class IdpCommunicationException extends Exception {

    /**
     * @param message détail de l'erreur réseau
     */
    public IdpCommunicationException(final String message) {
        super(message);
    }

    /**
     * @param message détail de l'erreur réseau
     * @param cause   cause racine
     */
    public IdpCommunicationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
