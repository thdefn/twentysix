package cm.twentysix.gateway.service;

import cm.twentysix.gateway.client.RedisClient;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
import java.util.Set;

@Service
public class JwtTokenManager {
    private static final long ACCESS_ALLOWANCE_TIME = 1000 * 60 * 60 * 24;
    private static final long REFRESH_ALLOWANCE_TIME = 1000 * 60 * 60 * 24 * 14;
    private final SecretKey secretKey;
    private final RedisClient redisClient;

    public JwtTokenManager(@Value("${jwt.key}") String key, RedisClient redisClient) {
        secretKey = Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));
        this.redisClient = redisClient;
    }

    public String makeAccessToken(String userId, String type) {
        return Jwts.builder()
                .subject(userId)
                .claim("role", type)
                .expiration(new Date(System.currentTimeMillis() + ACCESS_ALLOWANCE_TIME))
                .signWith(secretKey)
                .compact();
    }

    public String makeRefreshTokenAndSave(String userId, String type) {
        String refreshToken = Jwts.builder()
                .subject(userId)
                .claim("role", type)
                .expiration(new Date(System.currentTimeMillis() + REFRESH_ALLOWANCE_TIME))
                .signWith(secretKey)
                .compact();
        redisClient.addValueToSet(userId, refreshToken);
        redisClient.addValue(refreshToken, userId, Duration.ofMillis(REFRESH_ALLOWANCE_TIME));
        return refreshToken;
    }

    public boolean isValidRefreshToken(String refreshToken) {
        if (!validate(refreshToken))
            return false;
        return redisClient.isKeyExist(refreshToken);
    }

    public void deleteRefreshToken(String refreshToken) {
        String userId = parseId(refreshToken);
        redisClient.deleteKey(refreshToken);
        redisClient.deleteValueToSet(userId, refreshToken);
    }

    public void deleteUsersAllRefreshToken(String refreshToken) {
        String userId = parseId(refreshToken);
        Set<String> refreshTokens = redisClient.getSet(userId);
        redisClient.deleteAllKey(refreshTokens);
        redisClient.deleteKey(userId);
    }

    public boolean validate(String token) {
        try {
            return getClaims(token).getExpiration().after(new Date());
        } catch (ExpiredJwtException e) {
            throw e;
        } catch (JwtException e) {
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token).getPayload();
    }

    public String parseId(String token) {
        return getClaims(token).getSubject();
    }

    public String parseType(String token) {
        return getClaims(token).get("role", String.class);
    }
}
