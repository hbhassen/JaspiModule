package com.example.idp;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * <h2>Rôle de la classe</h2>
 * <p>Support de données transportant les attributs d'identité et les rôles fournis par l'Identity Provider.</p>
 *
 * <h3>Responsabilités</h3>
 * <ul>
 *     <li>Conserver le sujet, les rôles et les attributs supplémentaires.</li>
 *     <li>Servir de passerelle entre {@link IdpResponseValidator} et {@link IdpPrincipal}.</li>
 * </ul>
 *
 * <h3>Exemple d'utilisation</h3>
 * <pre>{@code
 * IdpUserInfo info = new IdpUserInfo("bob", Set.of("user"));
 * info.getAttributes().put("email", "bob@example.com");
 * }
 * </pre>
 *
 * <h3>Schéma d'interaction</h3>
 * <p>Construit par {@link IdpResponseValidator} puis consommé par {@link IdpServerAuthModule} pour enrichir
 * le {@link IdpPrincipal}.</p>
 */
public final class IdpUserInfo {

    private final String subject;
    private final Set<String> roles;
    private final Map<String, Object> attributes;

    /**
     * Crée une instance avec sujet et rôles.
     *
     * @param subject identifiant unique (non nul ni vide)
     * @param roles   rôles associés (peut être nul)
     */
    public IdpUserInfo(final String subject, final Set<String> roles) {
        if (subject == null || subject.isBlank()) {
            throw new IllegalArgumentException("Subject must not be null or blank");
        }
        this.subject = subject;
        this.roles = roles == null ? Collections.emptySet() : Collections.unmodifiableSet(new HashSet<>(roles));
        this.attributes = new HashMap<>();
    }

    /**
     * @return identifiant principal
     */
    public String getSubject() {
        return subject;
    }

    /**
     * @return rôles applicatifs (jamais nul)
     */
    public Set<String> getRoles() {
        return roles;
    }

    /**
     * Attributs supplémentaires issus de l'IdP.
     *
     * @return map mutable pour enrichissement contrôlé
     */
    public Map<String, Object> getAttributes() {
        return attributes;
    }
}
