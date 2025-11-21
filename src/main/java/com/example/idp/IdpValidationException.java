package com.example.idp;

/**
 * Raised when an IdP response cannot be validated or parsed.
 * <p>Examples include signature mismatches, expiration, or missing claims.</p>
 */
public class IdpValidationException extends Exception {
    public IdpValidationException(String message) {
        super(message);
    }

    public IdpValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
