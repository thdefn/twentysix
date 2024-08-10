package cm.twentysix.user.security;

import cm.twentysix.user.util.CookieUtil;
import cm.twentysix.user.util.JwtTokenManager;
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
    private final JwtTokenManager jwtTokenManager;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Optional<String> maybeToken = getToken(request);
        if (maybeToken.isPresent()) {
            String accessToken = maybeToken.get();
            try {
                if (StringUtils.hasText(accessToken) && jwtTokenManager.validate(accessToken))
                    authorize(accessToken);
            } catch (ExpiredJwtException e) {
                authorizeIfValidRefreshToken(request, response);
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

    public void authorizeIfValidRefreshToken(HttpServletRequest request, HttpServletResponse response) {
        Optional<String> maybeValidToken = CookieUtil.getCookieValue(request.getCookies(), "refreshToken")
                .stream().findFirst()
                .filter(jwtTokenManager::isValidRefreshToken);
        if (maybeValidToken.isEmpty())
            return;

        String refreshToken = maybeValidToken.get();

        jwtTokenManager.deleteRefreshToken(refreshToken);
        Long userId = jwtTokenManager.parseId(refreshToken);
        String userRole = jwtTokenManager.parseType(refreshToken);
        String newAccessToken = jwtTokenManager.makeAccessToken(userId, userRole);
        String newRefreshToken = jwtTokenManager.makeRefreshTokenAndSave(userId, userRole);

        response.setHeader(HttpHeaders.AUTHORIZATION, TOKEN_PREFIX + newAccessToken);
        response.setHeader(HttpHeaders.SET_COOKIE, CookieUtil.makeCookie("refreshToken", newRefreshToken));
        authorize(newAccessToken);
    }

    public void authorize(String accessToken) {
        Long userId = jwtTokenManager.parseId(accessToken);
        String userRole = jwtTokenManager.parseType(accessToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(String.valueOf(userId));
        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(userDetails, null, List.of(new SimpleGrantedAuthority(userRole))));
    }
}
