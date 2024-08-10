package cm.twentysix.brand.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserPrincipalService implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        return new UserPrincipal(Long.parseLong(userId));
    }
}
