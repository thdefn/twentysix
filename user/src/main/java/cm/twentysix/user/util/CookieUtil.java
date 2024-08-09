package cm.twentysix.user.util;

import jakarta.servlet.http.Cookie;
import org.springframework.http.ResponseCookie;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;

public class CookieUtil {
    public static String makeCookie(String name, String value) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(false)
                .sameSite("Strict")
                .maxAge(Duration.ofDays(30))
                .path("/")
                .build().toString();
    }

    public static Optional<String> getCookieValue(Cookie[] cookies, String key) {
        for (Cookie c : cookies)
            if (Objects.equals(c.getName(), key))
                return Optional.of(c.getValue());
        return Optional.empty();
    }
}
