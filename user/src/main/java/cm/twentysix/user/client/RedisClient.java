package cm.twentysix.user.client;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RedisClient {
    private final RedisTemplate<String, String> redisTemplate;

    public void setValue(String key, String data, Duration duration) {
        redisTemplate.opsForValue().set(key, data, duration);
    }

    public Optional<String> getValue(String key) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(key));
    }

    public void deleteValue(String key) {
        redisTemplate.delete(key);
    }

}
