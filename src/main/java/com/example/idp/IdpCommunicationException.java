package com.example.idp;

/**
 * Represents network or protocol errors while talking to the Identity Provider.
 */
public class IdpCommunicationException extends Exception {
    public IdpCommunicationException(String message) {
        super(message);
    }

    public IdpCommunicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
