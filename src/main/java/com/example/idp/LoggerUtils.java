package com.example.idp;

import org.jboss.logging.Logger;

/**
 * <h2>Rôle de la classe</h2>
 * <p>Fournit une façade centralisée pour créer des instances de {@link Logger} et uniformiser les préfixes.</p>
 */
public final class LoggerUtils {

    private LoggerUtils() {
    }

    /**
     * Crée un logger formaté pour la classe fournie.
     *
     * @param clazz classe appelante (précondition : non nulle)
     * @return logger JBoss
     */
    public static Logger getLogger(final Class<?> clazz) {
        return Logger.getLogger("IDP-JASPIC." + clazz.getSimpleName());
    }
}
