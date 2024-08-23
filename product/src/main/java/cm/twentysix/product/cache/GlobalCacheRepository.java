package cm.twentysix.product.cache;

import java.time.Duration;

public abstract class GlobalCacheRepository {
    private final GlobalCacheKey globalCacheKey;

    protected GlobalCacheRepository(GlobalCacheKey globalCacheKey) {
        this.globalCacheKey = globalCacheKey;
    }

    String getCacheKey(String id) {
        return globalCacheKey.name() + id;
    }

    Duration getCacheDuration() {
        return globalCacheKey.duration;
    }
}
