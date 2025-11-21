package com.example.idp;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * <p>IdpUserInfo is a transport object returned by the IdP validation layer.</p>
 * <p>Responsibilities:</p>
 * <ul>
 *     <li>Carry the subject identifier (name).</li>
 *     <li>Expose granted roles.</li>
 *     <li>Provide arbitrary attributes (claims) extracted from the IdP response.</li>
 * </ul>
 * <p>Usage example:</p>
 * <pre>{@code
 * IdpUserInfo info = IdpUserInfo.builder("alice")
 *     .addRole("user")
 *     .addAttribute("email", "alice@example.com")
 *     .build();
 * }</pre>
 * <p>Interaction diagram:</p>
 * <ol>
 *     <li>{@link IdpResponseValidator} produces the object after signature and audience checks.</li>
 *     <li>{@link IdpServerAuthModule} converts it to an {@link IdpPrincipal}.</li>
 *     <li>{@link SessionManager} stores it for re-use.</li>
 * </ol>
 */
public final class IdpUserInfo {

    private final String name;
    private final Set<String> roles;
    private final Map<String, String> attributes;

    private IdpUserInfo(Builder builder) {
        this.name = builder.name;
        this.roles = Collections.unmodifiableSet(new HashSet<>(builder.roles));
        this.attributes = Collections.unmodifiableMap(new HashMap<>(builder.attributes));
    }

    public String getName() {
        return name;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    /**
     * Builder to create instances in a fluent manner.
     */
    public static Builder builder(String name) {
        return new Builder(name);
    }

    /**
     * Mutable builder implementation used during response validation.
     */
    public static final class Builder {
        private final String name;
        private final Set<String> roles = new HashSet<>();
        private final Map<String, String> attributes = new HashMap<>();

        private Builder(String name) {
            this.name = Objects.requireNonNull(name, "name must not be null");
        }

        public Builder addRole(String role) {
            Objects.requireNonNull(role, "role must not be null");
            this.roles.add(role);
            return this;
        }

        public Builder addAttribute(String key, String value) {
            Objects.requireNonNull(key, "key must not be null");
            Objects.requireNonNull(value, "value must not be null");
            this.attributes.put(key, value);
            return this;
        }

        public IdpUserInfo build() {
            return new IdpUserInfo(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof IdpUserInfo that)) {
            return false;
        }
        return Objects.equals(name, that.name) && Objects.equals(roles, that.roles) && Objects.equals(attributes, that.attributes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, roles, attributes);
    }
}
