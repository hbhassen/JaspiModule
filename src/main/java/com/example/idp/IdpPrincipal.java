package com.example.idp;

import java.io.Serial;
import java.io.Serializable;
import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * <p>IdpPrincipal represents the authenticated user propagated to the WildFly security context.</p>
 * <p>Responsibilities:</p>
 * <ul>
 *     <li>Expose the user name and a read-only view of roles.</li>
 *     <li>Store additional attributes retrieved from the Identity Provider.</li>
 *     <li>Provide convenient helpers used by JASPIC callbacks and application code.</li>
 * </ul>
 * <p>Usage example:</p>
 * <pre>{@code
 * IdpPrincipal principal = new IdpPrincipal("alice", Set.of("admin"));
 * principal.getAttributes().put("email", "alice@example.com");
 * String name = principal.getName();
 * boolean isAdmin = principal.getRoles().contains("admin");
 * }</pre>
 * <p>Interaction diagram:</p>
 * <ol>
 *     <li>{@link IdpServerAuthModule} builds the principal after validating the IdP response.</li>
 *     <li>{@link SessionManager} persists it in the HTTP session to avoid re-authentication.</li>
 *     <li>Application code reads roles and attributes for authorization decisions.</li>
 * </ol>
 */
public class IdpPrincipal implements Principal, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final String name;
    private final Set<String> roles;
    private final Map<String, String> attributes = new HashMap<>();

    /**
     * Creates a principal using the provided user name and roles.
     *
     * @param name  non-null user name supplied by the IdP
     * @param roles optional set of roles; may be empty but never null
     * @throws NullPointerException when {@code name} or {@code roles} is null
     */
    public IdpPrincipal(String name, Set<String> roles) {
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.roles = Collections.unmodifiableSet(new HashSet<>(Objects.requireNonNull(roles, "roles must not be null")));
    }

    /**
     * Returns the unique name of the principal.
     *
     * @return user name returned by the IdP
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Provides an immutable set of roles granted to the user.
     *
     * @return roles propagated to WildFly
     */
    public Set<String> getRoles() {
        return roles;
    }

    /**
     * Returns a mutable map of additional attributes. This map is intentionally mutable to allow the
     * authentication layer to enrich the principal before it is injected into the {@link jakarta.security.auth.message.callback.CallerPrincipalCallback}.
     *
     * @return attribute map keyed by attribute name
     */
    public Map<String, String> getAttributes() {
        return attributes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof IdpPrincipal other)) {
            return false;
        }
        return Objects.equals(name, other.name) && Objects.equals(roles, other.roles) && Objects.equals(attributes, other.attributes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, roles, attributes);
    }

    @Override
    public String toString() {
        return "IdpPrincipal{" + "name='" + name + '\'' + ", roles=" + roles + ", attributes=" + attributes + '}';
    }
}
