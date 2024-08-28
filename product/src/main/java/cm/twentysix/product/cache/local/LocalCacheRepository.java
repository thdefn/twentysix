package cm.twentysix.product.cache.local;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.Optional;

public abstract class LocalCacheRepository<T> {
    private final LocalCacheKey localCacheKey;
    private final Cache cache;

    protected LocalCacheRepository(LocalCacheKey localCacheKey, CacheManager cacheManager) {
        this.localCacheKey = localCacheKey;
        this.cache = cacheManager.getCache(localCacheKey.name());
    }

    private String getCacheKey(String key) {
        return localCacheKey.name() + key;
    }

    protected void put(String key, T value) {
        cache.put(getCacheKey(key), value);
    }

    protected Optional<T> get(String key, Class<T> clazz) {
        Cache.ValueWrapper wrapper = cache.get(getCacheKey(key));
        if (wrapper != null) {
            Object value = wrapper.get();
            return Optional.ofNullable(clazz.cast(value));
        }
        return Optional.empty();
    }
}
