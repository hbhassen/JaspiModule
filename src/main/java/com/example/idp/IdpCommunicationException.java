package com.example.idp;

/**
 * <p>Signals communication issues with the Identity Provider.</p>
 */
public class IdpCommunicationException extends RuntimeException {
    public IdpCommunicationException(String message) {
        super(message);
    }

    public IdpCommunicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
