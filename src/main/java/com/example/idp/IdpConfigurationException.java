package com.example.idp;

/**
 * <p>Thrown when mandatory configuration is missing or invalid.</p>
 */
public class IdpConfigurationException extends RuntimeException {
    public IdpConfigurationException(String message) {
        super(message);
    }

    public IdpConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
