package com.example.idp;

import java.security.Principal;
import java.util.Objects;
import java.util.Set;

/**
 * {@link Principal} implementation exposing IdP-derived identity and roles to the application layer.
 * <p>Responsibilities:</p>
 * <ul>
 *     <li>Return the canonical username for security checks.</li>
 *     <li>Expose {@link IdpUserInfo} for richer application needs.</li>
 * </ul>
 * Interaction: added to the {@link javax.security.auth.Subject} by {@link IdpServerAuthModule} and stored via {@link SessionManager}.</p>
 */
public final class IdpPrincipal implements Principal {

    private final IdpUserInfo userInfo;

    public IdpPrincipal(IdpUserInfo userInfo) {
        this.userInfo = Objects.requireNonNull(userInfo, "userInfo");
    }

    @Override
    public String getName() {
        return userInfo.getUsername();
    }

    public Set<String> getRoles() {
        return userInfo.getRoles();
    }

    public IdpUserInfo getUserInfo() {
        return userInfo;
    }

    @Override
    public String toString() {
        return "IdpPrincipal{" +
                "userInfo=" + userInfo +
                '}';
    }
}
