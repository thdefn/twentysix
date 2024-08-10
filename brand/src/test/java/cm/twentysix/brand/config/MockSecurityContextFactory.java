package cm.twentysix.brand.config;

import cm.twentysix.brand.security.UserPrincipal;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.List;

public class MockSecurityContextFactory implements WithSecurityContextFactory<WithMockUser> {
    private static final UserPrincipal userPrincipal = new UserPrincipal(1L);

    @Override
    public SecurityContext createSecurityContext(WithMockUser annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userPrincipal, null, List.of(new SimpleGrantedAuthority("SELLER")));
        context.setAuthentication(authentication);
        return context;
    }
}
