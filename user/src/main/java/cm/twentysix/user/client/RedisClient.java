package cm.twentysix.user.client;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class RedisClient {
    private final RedisTemplate<String, String> redisTemplate;

    public void addValue(String key, String value, Duration duration) {
        redisTemplate.opsForValue().set(key, value, duration);
    }

    public void deleteAllKey(Collection<String> key) {
        redisTemplate.delete(key);
    }

    public Optional<String> getValue(String key) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(key));
    }

    public boolean isKeyExist(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public void deleteKey(String key) {
        redisTemplate.delete(key);
    }

    public void deleteValueToSet(String key, String value) {
        redisTemplate.opsForSet().remove(key, value);
    }

    public void addValueToSet(String key, String value) {
        redisTemplate.opsForSet().add(key, value);
    }

    public Set<String> getSet(String key) {
        return redisTemplate.opsForSet().members(key);
    }
}
