package cm.twentysix.product.cache.global;

import cm.twentysix.product.client.RedisClient;

import java.time.Duration;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class GlobalCacheRepository<T, V extends T> {
    private final GlobalCacheKey globalCacheKey;
    protected final RedisClient<T> redisClient;

    protected GlobalCacheRepository(GlobalCacheKey globalCacheKey, RedisClient<T> redisClient) {
        this.globalCacheKey = globalCacheKey;
        this.redisClient = redisClient;
    }

    protected String getCacheKey(String id) {
        return globalCacheKey.name() + id;
    }

    protected Duration getCacheDuration() {
        return globalCacheKey.duration;
    }

    protected void put(String key, V value){
        redisClient.addValue(getCacheKey(key), value, getCacheDuration());
    }

    protected void putAll(Map<String, V> keyValueMap){
        Map<String, T> cacheKeyValue = keyValueMap.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> getCacheKey(entry.getKey()), Map.Entry::getValue));
        redisClient.addValues(cacheKeyValue, getCacheDuration());
    }
}
