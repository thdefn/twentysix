package cm.twentysix.order.cache.global;

import java.time.Duration;

public abstract class GlobalCacheRepository {
    private final GlobalCacheKey globalCacheKey;

    protected GlobalCacheRepository(GlobalCacheKey globalCacheKey) {
        this.globalCacheKey = globalCacheKey;
    }

    protected String getCacheKey(String id) {
        return globalCacheKey.name() + id;
    }

    protected Duration getCacheDuration() {
        return globalCacheKey.duration;
    }
}
