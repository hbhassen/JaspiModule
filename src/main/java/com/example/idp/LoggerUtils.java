package com.example.idp;

import org.jboss.logging.Logger;

/**
 * Utility wrapper around {@link Logger} to standardize log messages across the module.
 * <p>Responsibilities:</p>
 * <ul>
 *     <li>Provide typed loggers per class.</li>
 *     <li>Offer helper methods for common authentication events.</li>
 * </ul>
 */
public final class LoggerUtils {

    private LoggerUtils() {
    }

    public static Logger logger(Class<?> target) {
        return Logger.getLogger(target);
    }

    public static void logRedirect(Logger logger, String redirectUrl) {
        logger.debugf("Redirecting to IdP at %s", redirectUrl);
    }

    public static void logValidation(Logger logger, String tokenType) {
        logger.debugf("Validating IdP %s response", tokenType);
    }

    public static void logSuccess(Logger logger, String username) {
        logger.infof("Successfully authenticated user %s via IdP", username);
    }
}
