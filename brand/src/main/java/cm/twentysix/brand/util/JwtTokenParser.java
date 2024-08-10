package cm.twentysix.brand.util;

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
public class JwtTokenParser {
    private final SecretKey secretKey;

    public JwtTokenParser(@Value("${jwt.key}") String key) {
        secretKey = Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));
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

    public String parseType(String token) {
        return getClaims(token).get("role", String.class);
    }
}
