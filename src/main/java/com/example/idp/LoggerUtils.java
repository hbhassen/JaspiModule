package com.example.idp;

import org.jboss.logging.Logger;

/**
 * <p>LoggerUtils centralizes logger creation.</p>
 * <p>Responsibilities:</p>
 * <ul>
 *     <li>Provide a consistent logger category for the module.</li>
 *     <li>Offer helper methods for structured logging.</li>
 * </ul>
 * <p>Usage example:</p>
 * <pre>{@code
 * private static final Logger LOG = LoggerUtils.getLogger(MyClass.class);
 * LOG.debugf("redirecting to %s", url);
 * }</pre>
 * <p>Interaction diagram:</p>
 * <ol>
 *     <li>All classes call {@link #getLogger(Class)} to obtain a {@link Logger}.</li>
 *     <li>WildFly logging configuration controls output.</li>
 * </ol>
 */
public final class LoggerUtils {

    private LoggerUtils() {
    }

    public static Logger getLogger(Class<?> type) {
        return Logger.getLogger("idp.module." + type.getSimpleName());
    }
}
