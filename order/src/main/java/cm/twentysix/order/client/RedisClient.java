package cm.twentysix.order.client;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class RedisClient<T> {
    private final RedisTemplate<String, T> redisTemplate;

    public List<T> getValues(List<String> keys) {
        List<T> values = redisTemplate.opsForValue().multiGet(keys);
        return values.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    public T getValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }
}
