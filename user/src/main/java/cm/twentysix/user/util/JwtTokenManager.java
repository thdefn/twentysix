package cm.twentysix.user.util;

import cm.twentysix.user.client.RedisClient;
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

    public String makeAccessToken(Long userId) {
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .expiration(new Date(System.currentTimeMillis() + ACCESS_ALLOWANCE_TIME))
                .signWith(secretKey)
                .compact();
    }

    public String makeRefreshTokenAndSave(Long userId) {
        String refreshToken = Jwts.builder()
                .subject(String.valueOf(userId))
                .expiration(new Date(System.currentTimeMillis() + REFRESH_ALLOWANCE_TIME))
                .signWith(secretKey)
                .compact();
        redisClient.addValueToSet(String.valueOf(userId), refreshToken);
        redisClient.addValue(refreshToken, String.valueOf(userId), Duration.ofMillis(REFRESH_ALLOWANCE_TIME));
        return refreshToken;
    }

    public boolean isValidRefreshToken(String refreshToken) {
        if (!validate(refreshToken))
            return false;
        return redisClient.isKeyExist(refreshToken);
    }

    public void deleteRefreshToken(String refreshToken) {
        Long userId = parseId(refreshToken);
        redisClient.deleteKey(refreshToken);
        redisClient.deleteValueToSet(String.valueOf(userId), refreshToken);
    }

    public void deleteUsersAllRefreshToken(String refreshToken) {
        Long userId = parseId(refreshToken);
        Set<String> refreshTokens = redisClient.getSet(String.valueOf(userId));
        redisClient.deleteAllKey(refreshTokens);
        redisClient.deleteKey(String.valueOf(userId));
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

    public Long parseId(String token) {
        return Long.parseLong(getClaims(token).getSubject());
    }
}
