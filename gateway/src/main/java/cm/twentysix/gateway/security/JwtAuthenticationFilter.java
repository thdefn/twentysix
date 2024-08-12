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
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Collections;

@RequiredArgsConstructor
@Component
@Slf4j
public class JwtAuthenticationFilter implements WebFilter {
    private static final String TOKEN_PREFIX = "Bearer ";
    private final JwtTokenManager jwtTokenManager;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return getToken(exchange.getRequest())
                .flatMap(accessToken -> processToken(exchange, chain, accessToken))
                .switchIfEmpty(chain.filter(exchange))
                .then();
    }

    private Mono<Void> processToken(ServerWebExchange exchange, WebFilterChain chain, String accessToken) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        try {
            if (isValidAccessToken(accessToken)) {
                return authorizeAndFilter(chain, exchange, accessToken);
            }
        } catch (ExpiredJwtException e) {
            return authorizeWithRefreshToken(request, response, chain, exchange);
        }

        return chain.filter(exchange).contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(new SecurityContextImpl())));
    }

    private Mono<Void> authorizeAndFilter(WebFilterChain chain, ServerWebExchange exchange, String accessToken) {
        SecurityContext context = authorize(accessToken);
        exchange = addUserIdToHeader(exchange, accessToken);
        return chain.filter(exchange).contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(context)));
    }

    private ServerWebExchange addUserIdToHeader(ServerWebExchange exchange, String accessToken) {
        String userId = jwtTokenManager.parseId(accessToken);
        ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                .header("X-USER-ID", userId)
                .build();
        return exchange.mutate().request(modifiedRequest).build();
    }

    private boolean isValidAccessToken(String accessToken) {
        return StringUtils.hasText(accessToken) && jwtTokenManager.validate(accessToken);
    }

    private Mono<Void> authorizeWithRefreshToken(ServerHttpRequest request, ServerHttpResponse response, WebFilterChain chain, ServerWebExchange exchange) {
        return getValidRefreshToken(request)
                .map(refreshToken -> renewTokensAndAuthorize(response, refreshToken))
                .defaultIfEmpty(new SecurityContextImpl())
                .flatMap(context -> chain.filter(exchange).contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(context))));
    }

    private Mono<String> getToken(ServerHttpRequest request) {
        String rawToken = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (!ObjectUtils.isEmpty(rawToken) && rawToken.startsWith(TOKEN_PREFIX)) {
            return Mono.just(rawToken.substring(TOKEN_PREFIX.length()));
        }
        return Mono.empty();
    }

    private Mono<String> getValidRefreshToken(ServerHttpRequest request) {
        return Mono.justOrEmpty(CookieUtil.getCookieValue(request.getCookies(), "refreshToken")
                .stream()
                .findFirst()
                .filter(jwtTokenManager::isValidRefreshToken));
    }

    private SecurityContext renewTokensAndAuthorize(ServerHttpResponse response, String refreshToken) {
        jwtTokenManager.deleteRefreshToken(refreshToken);

        String userId = jwtTokenManager.parseId(refreshToken);
        String userRole = jwtTokenManager.parseType(refreshToken);
        String newAccessToken = jwtTokenManager.makeAccessToken(userId, userRole);
        String newRefreshToken = jwtTokenManager.makeRefreshTokenAndSave(userId, userRole);

        response.getHeaders().set(HttpHeaders.AUTHORIZATION, TOKEN_PREFIX + newAccessToken);
        response.getHeaders().set(HttpHeaders.SET_COOKIE, CookieUtil.makeCookie("refreshToken", newRefreshToken));

        return authorize(newAccessToken);
    }

    private SecurityContext authorize(String accessToken) {
        String userRole = jwtTokenManager.parseType(accessToken);
        return new SecurityContextImpl(
                new UsernamePasswordAuthenticationToken("user", null, Collections.singleton(new SimpleGrantedAuthority(userRole)))
        );
    }

}
