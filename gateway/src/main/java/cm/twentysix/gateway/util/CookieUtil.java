package cm.twentysix.gateway.util;

import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseCookie;
import org.springframework.util.MultiValueMap;

import java.time.Duration;
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

    public static Optional<String> getCookieValue(MultiValueMap<String, HttpCookie> cookies, String key) {
        if (cookies.containsKey(key))
            return Optional.of(cookies.getFirst(key).getValue());
        return Optional.empty();
    }
}
