package cm.twentysix.user.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenManager {
    private static final long ACCESS_ALLOWANCE_TIME = 1000 * 60 * 60 * 24;
    private static final long REFRESH_ALLOWANCE_TIME = 1000 * 60 * 60 * 24 * 14;
    private static SecretKey secretKey;

    public JwtTokenManager(@Value("${jwt.secret}") String key) {
        secretKey = Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));
    }

    public String makeAccessToken(Long userId) {
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .expiration(new Date(System.currentTimeMillis() + ACCESS_ALLOWANCE_TIME))
                .signWith(secretKey)
                .compact();
    }

    public String makeRefreshToken(Long userId) {
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .expiration(new Date(System.currentTimeMillis() + REFRESH_ALLOWANCE_TIME))
                .signWith(secretKey)
                .compact();
    }

    public static boolean validate(String token) {
        try {
            return getClaims(token).getExpiration().after(new Date());
        } catch (ExpiredJwtException e) {
            throw e;
        } catch (JwtException e) {
            return false;
        }
    }

    private static Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token).getPayload();
    }

    public static Long parseId(String token) {
        return Long.parseLong(getClaims(token).getSubject());
    }
}
