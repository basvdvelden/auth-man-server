package nl.management.auth.server.user.models.entities;

import org.springframework.security.core.GrantedAuthority;

public enum ERole implements GrantedAuthority {
    TRIAL;

    @Override
    public String getAuthority() {
        return this.name().toLowerCase();
    }
}
