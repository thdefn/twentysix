package cm.twentysix.order.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
public class RedisClient<T> {
    private final RedisTemplate<String, T> redisTemplate;

    public Map<String, T> getValues(List<String> keys) {
        Map<String, T> keyValueMap = new HashMap<>();
        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            keys.forEach(k -> keyValueMap.put(k, redisTemplate.opsForValue().get(k)));
            return null;
        });
        return keyValueMap;
    }

    public T getValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void addValues(Map<String, T> keyValueMap, Duration duration) {
        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            keyValueMap.forEach((k, v) -> redisTemplate.opsForValue().set(k, v, duration));
            return null;
        });
    }

    public void addValuesIfAbsent(Map<String, T> keyValueMap, Duration duration) {
        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            keyValueMap.forEach((k, v) -> redisTemplate.opsForValue().setIfAbsent(k, v, duration));
            return null;
        });
    }

    public void addValue(String key, T value, Duration duration) {
        redisTemplate.opsForValue().set(key, value, duration);
    }

    public void decrementValuesBy(Map<String, Integer> keyDelta){
        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            keyDelta.forEach((k, v) -> redisTemplate.opsForValue().decrement(k,v));
            return null;
        });
    }

    public void incrementValuesBy(Map<String, Integer> keyDelta){
        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            keyDelta.forEach((k, v) -> redisTemplate.opsForValue().increment(k,v));
            return null;
        });
    }
}
