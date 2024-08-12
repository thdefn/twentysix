package cm.twentysix.gateway.security;

import cm.twentysix.gateway.service.JwtTokenManager;
import cm.twentysix.gateway.util.CookieUtil;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Optional;

@RequiredArgsConstructor
@Component
@Slf4j
public class JwtAuthenticationFilter implements WebFilter {
    private static final String TOKEN_PREFIX = "Bearer ";
    private final JwtTokenManager jwtTokenManager;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        Optional<String> maybeToken = getToken(exchange.getRequest());
        if (maybeToken.isPresent()) {
            String accessToken = maybeToken.get();
            try {
                if (StringUtils.hasText(accessToken) && jwtTokenManager.validate(accessToken)) {
                    return authorize(accessToken, exchange, chain);
                }
            } catch (ExpiredJwtException e) {
                return authorizeIfValidRefreshToken(exchange.getRequest(), exchange.getResponse(), exchange, chain);
            }
        }
        return chain.filter(exchange);
    }

    private Optional<String> getToken(ServerHttpRequest request) {
        String rawToken = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (!ObjectUtils.isEmpty(rawToken) && rawToken.startsWith(TOKEN_PREFIX))
            return Optional.of(rawToken.substring(TOKEN_PREFIX.length()));
        return Optional.empty();
    }

    private Mono<Void> authorizeIfValidRefreshToken(ServerHttpRequest request, ServerHttpResponse response, ServerWebExchange exchange, WebFilterChain chain) {
        Optional<String> maybeValidToken = CookieUtil.getCookieValue(request.getCookies(), "refreshToken")
                .stream().findFirst()
                .filter(jwtTokenManager::isValidRefreshToken);
        if (maybeValidToken.isEmpty())
            return chain.filter(exchange);

        String refreshToken = maybeValidToken.get();

        jwtTokenManager.deleteRefreshToken(refreshToken);
        String userId = jwtTokenManager.parseId(refreshToken);
        String userRole = jwtTokenManager.parseType(refreshToken);
        String newAccessToken = jwtTokenManager.makeAccessToken(userId, userRole);
        String newRefreshToken = jwtTokenManager.makeRefreshTokenAndSave(userId, userRole);

        response.getHeaders().set(HttpHeaders.AUTHORIZATION, TOKEN_PREFIX + newAccessToken);
        response.getHeaders().set(HttpHeaders.SET_COOKIE, CookieUtil.makeCookie("refreshToken", newRefreshToken));
        return authorize(newAccessToken, exchange, chain);
    }

    private Mono<Void> authorize(String accessToken, ServerWebExchange exchange, WebFilterChain chain) {
        exchange = addUserIdToHeader(exchange, accessToken);
        String userRole = jwtTokenManager.parseType(accessToken);
        return chain.filter(exchange).contextWrite(ReactiveSecurityContextHolder.withAuthentication(new UsernamePasswordAuthenticationToken("user", null, Collections.singleton(new SimpleGrantedAuthority(userRole)))));
    }

    private ServerWebExchange addUserIdToHeader(ServerWebExchange exchange, String accessToken) {
        String userId = jwtTokenManager.parseId(accessToken);
        ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                .header("X-USER-ID", userId)
                .build();
        return exchange.mutate().request(modifiedRequest).build();
    }


}
