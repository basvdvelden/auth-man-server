package nl.management.finance.server.user.principles;

import nl.management.finance.server.security.ERole;
import nl.management.finance.server.user.models.NativeUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;

public class CustomUserPrincipal implements UserDetails {
    private NativeUser nativeUser;

    public CustomUserPrincipal(NativeUser nativeUser) {
        this.nativeUser = nativeUser;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Set.of(ERole.TRIAL);
    }

    @Override
    public String getPassword() {
        return nativeUser.getPassword();
    }

    @Override
    public String getUsername() {
        return nativeUser.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
