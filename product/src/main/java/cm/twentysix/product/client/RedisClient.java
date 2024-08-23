package cm.twentysix.product.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class RedisClient<T> {
    private final RedisTemplate<String, T> redisTemplate;
    private final RedisSerializer<String> keySerializer;
    private final RedisSerializer valueSerializer;

    public RedisClient(RedisTemplate<String, T> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.keySerializer = new StringRedisSerializer();
        this.valueSerializer = new GenericJackson2JsonRedisSerializer();

    }

    public void addValues(Map<String, T> keyValueMap, Duration duration) {
        try {
            redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
                keyValueMap.forEach((k, v) -> redisTemplate.opsForValue().set(k, v, duration));
                return null;
            });
        } catch (Exception e) {
            log.error(e.toString());
        }
    }

    public void addValue(String key, T value, Duration duration) {
        redisTemplate.opsForValue().set(key, value, duration);
    }

}
