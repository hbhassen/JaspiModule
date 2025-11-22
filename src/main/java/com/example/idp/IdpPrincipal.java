package com.example.idp;

import java.io.Serial;
import java.io.Serializable;
import java.security.Principal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * <h2>Rôle de la classe</h2>
 * <p>Représente l'utilisateur authentifié dans le conteneur WildFly. Cette implémentation de {@link Principal}
 * transporte des informations d'identité et les rôles issus de l'Identity Provider.</p>
 *
 * <h3>Responsabilités</h3>
 * <ul>
 *     <li>Stocker l'identifiant principal (subject).</li>
 *     <li>Exposer les rôles applicatifs attribués par l'IdP.</li>
 *     <li>Fournir une structure immuable adaptée à la propagation dans le contexte de sécurité.</li>
 * </ul>
 *
 * <h3>Exemple d'utilisation</h3>
 * <pre>{@code
 * IdpPrincipal principal = new IdpPrincipal("alice", Set.of("admin", "user"));
 * request.login(principal.getName(), "");
 * }</pre>
 *
 * <h3>Schéma d'interaction</h3>
 * <p>Instancié par {@link IdpResponseValidator} après validation de la réponse IdP puis transmis au conteneur via
 * {@link IdpServerAuthModule}.</p>
 */
public final class IdpPrincipal implements Principal, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final String name;
    private final Set<String> roles;

    /**
     * Crée un principal enrichi avec les rôles fournis.
     *
     * @param name  identifiant unique de l'utilisateur (précondition : non nul ni vide)
     * @param roles rôles applicatifs attribués par l'IdP (optionnel, peut être nul)
     * @throws IllegalArgumentException si le nom est nul ou vide
     */
    public IdpPrincipal(final String name, final Set<String> roles) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Principal name must not be null or blank");
        }
        this.name = name;
        this.roles = roles == null ? Collections.emptySet() : Collections.unmodifiableSet(new HashSet<>(roles));
    }

    /**
     * @return identifiant de l'utilisateur (postcondition : non nul ni vide)
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Retourne les rôles applicatifs associés.
     *
     * @return ensemble immuable de rôles (jamais nul)
     */
    public Set<String> getRoles() {
        return roles;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof IdpPrincipal other)) {
            return false;
        }
        return Objects.equals(name, other.name) && Objects.equals(roles, other.roles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, roles);
    }

    @Override
    public String toString() {
        return "IdpPrincipal{" +
                "name='" + name + '\'' +
                ", roles=" + roles +
                '}';
    }
}
