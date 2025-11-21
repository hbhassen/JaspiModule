package com.example.idp;

/**
 * <p>Indicates validation issues related to IdP responses (signature, expiration, audience).</p>
 */
public class IdpValidationException extends RuntimeException {
    public IdpValidationException(String message) {
        super(message);
    }

    public IdpValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
