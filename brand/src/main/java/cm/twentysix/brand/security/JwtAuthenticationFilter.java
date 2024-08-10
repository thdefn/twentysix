package cm.twentysix.brand.security;

import cm.twentysix.brand.util.JwtTokenParser;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final String TOKEN_PREFIX = "Bearer ";
    private final JwtTokenParser jwtTokenParser;
    private final UserDetailsService userDetailsService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Optional<String> maybeToken = getToken(request);
        if (maybeToken.isPresent()) {
            String accessToken = maybeToken.get();
            try {
                if (StringUtils.hasText(accessToken) && jwtTokenParser.validate(accessToken))
                    authorize(accessToken);
            } catch (ExpiredJwtException e) {
                // TODO : 토큰 만료 핸들링
            }
        }
        filterChain.doFilter(request, response);
    }

    private Optional<String> getToken(HttpServletRequest request) {
        String rawToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (!ObjectUtils.isEmpty(rawToken) && rawToken.startsWith(TOKEN_PREFIX))
            return Optional.of(rawToken.substring(TOKEN_PREFIX.length()));
        return Optional.empty();
    }

    public void authorize(String accessToken) {
        Long userId = jwtTokenParser.parseId(accessToken);
        String userRole = jwtTokenParser.parseType(accessToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(String.valueOf(userId));
        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(userDetails, null, List.of(new SimpleGrantedAuthority(userRole))));
    }
}
