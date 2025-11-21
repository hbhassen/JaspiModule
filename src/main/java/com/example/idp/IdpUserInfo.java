package com.example.idp;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Immutable representation of user identity data returned by the IdP.
 * <p>Responsibilities:</p>
 * <ul>
 *     <li>Hold username, roles and arbitrary attributes.</li>
 *     <li>Provide convenient builder for tests and validators.</li>
 * </ul>
 * Interaction: consumed by {@link IdpPrincipal}, persisted by {@link SessionManager}, produced by {@link TokenValidator}.
 */
public final class IdpUserInfo implements Serializable {

    private final String username;
    private final Set<String> roles;
    private final Map<String, Object> attributes;

    private IdpUserInfo(Builder builder) {
        this.username = Objects.requireNonNull(builder.username, "username");
        this.roles = Collections.unmodifiableSet(new HashSet<>(builder.roles));
        this.attributes = Collections.unmodifiableMap(new HashMap<>(builder.attributes));
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getUsername() {
        return username;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof IdpUserInfo)) {
            return false;
        }
        IdpUserInfo other = (IdpUserInfo) obj;
        return username.equals(other.username) && roles.equals(other.roles) && attributes.equals(other.attributes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, roles, attributes);
    }

    @Override
    public String toString() {
        return "IdpUserInfo{" +
                "username='" + username + '\'' +
                ", roles=" + roles +
                ", attributes=" + attributes +
                '}';
    }

    /**
     * Builder to incrementally add user fields.
     */
    public static final class Builder {
        private String username;
        private final Set<String> roles = new HashSet<>();
        private final Map<String, Object> attributes = new HashMap<>();

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder addRole(String role) {
            if (role != null) {
                this.roles.add(role);
            }
            return this;
        }

        public Builder attribute(String key, Object value) {
            if (key != null && value != null) {
                this.attributes.put(key, value);
            }
            return this;
        }

        public IdpUserInfo build() {
            if (username == null) {
                throw new IllegalStateException("username is required");
            }
            return new IdpUserInfo(this);
        }
    }
}
