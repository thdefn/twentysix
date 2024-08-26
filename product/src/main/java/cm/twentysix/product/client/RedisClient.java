package cm.twentysix.product.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.util.Map;

@Slf4j
public class RedisClient<T> {
    private final RedisTemplate<String, T> redisTemplate;

    public RedisClient(RedisTemplate<String, T> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void addValues(Map<String, T> keyValueMap, Duration duration) {
        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            keyValueMap.forEach((k, v) -> redisTemplate.opsForValue().set(k, v, duration));
            return null;
        });
    }

    public void addValue(String key, T value, Duration duration) {
        redisTemplate.opsForValue().set(key, value, duration);
    }

}
