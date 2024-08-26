package cm.twentysix.order.cache.global;

import cm.twentysix.order.client.RedisClient;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class GlobalCacheRepository<T, V extends T> {
    private final GlobalCacheKey globalCacheKey;
    protected final RedisClient<T> redisClient;

    protected GlobalCacheRepository(GlobalCacheKey globalCacheKey, RedisClient<T> redisClient) {
        this.globalCacheKey = globalCacheKey;
        this.redisClient = redisClient;
    }

    protected String getCacheKey(String key) {
        return globalCacheKey.name() + key;
    }

    protected String parseCacheKey(String key){
        return key.substring(globalCacheKey.name().length());
    }

    protected Duration getCacheDuration() {
        return globalCacheKey.duration;
    }

    protected Optional<V> get(String key, Class<V> clazz) {
        Optional<T> optionalValue = Optional.ofNullable(redisClient.getValue(getCacheKey(key)));
        if (optionalValue.isEmpty())
            return Optional.empty();
        T value = optionalValue.get();
        try {
            return Optional.of(clazz.cast(value));
        } catch (ClassCastException e) {
            return Optional.empty();
        }
    }

    protected Map<String, V> getAll(List<String> keys, Class<V> clazz) {
        List<String> cacheKeys = keys.stream().map(this::getCacheKey).toList();
        return redisClient.getValues(cacheKeys).entrySet().stream()
                .filter(entry -> entry.getValue() != null)
                .collect(Collectors.toMap(entry -> parseCacheKey(entry.getKey()),
                        entry -> clazz.cast(entry.getValue())));
    }

    public void put(String key, V value){
        redisClient.addValue(getCacheKey(key), value, getCacheDuration());
    }

    public void putAll(Map<String, V> keyValueMap){
        Map<String, T> cacheKeyValue = keyValueMap.entrySet().stream()
                        .collect(Collectors.toMap(
                                entry -> getCacheKey(entry.getKey()), Map.Entry::getValue));
        redisClient.addValues(cacheKeyValue, getCacheDuration());
    }
}
