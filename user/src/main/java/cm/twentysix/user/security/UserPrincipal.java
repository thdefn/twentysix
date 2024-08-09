package cm.twentysix.user.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

@Getter
public class UserPrincipal implements UserDetails {
    private final Long userId;
    private final List<GrantedAuthority> authorities;

    public UserPrincipal(Long userId) {
        this.userId = userId;
        authorities = List.of(new SimpleGrantedAuthority("USER"));
    }

    @Override
    public String getPassword() {
        return String.valueOf(userId);
    }

    @Override
    public String getUsername() {
        return String.valueOf(userId);
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
