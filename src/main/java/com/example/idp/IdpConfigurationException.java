package com.example.idp;

/**
 * Signals configuration errors detected during initialization.
 * <p>Typical causes:</p>
 * <ul>
 *     <li>Missing IdP endpoints or client identifier.</li>
 *     <li>Invalid URLs or malformed properties.</li>
 * </ul>
 * Interaction: Thrown by {@link ConfigProperties} builder and bubbled to {@link IdpServerAuthModule#initialize}.</p>
 */
public class IdpConfigurationException extends RuntimeException {
    public IdpConfigurationException(String message) {
        super(message);
    }

    public IdpConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
