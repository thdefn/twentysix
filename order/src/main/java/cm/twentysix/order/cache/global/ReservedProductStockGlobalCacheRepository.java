package cm.twentysix.order.cache.global;

import cm.twentysix.order.client.RedisClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class ReservedProductStockGlobalCacheRepository extends GlobalCacheRepository<Object, Integer> {
    protected ReservedProductStockGlobalCacheRepository(RedisClient<Object> redisClient) {
        super(GlobalCacheKey.RESERVED_PRODUCT_STOCK, redisClient);
    }

    public Map<String, Integer> getOrFetchIfAbsent(List<String> keys, Map<String, Integer> maybeFetched) {
        Map<String, Integer> cachedData = getAll(keys);
        for (String key : keys) {
            if (!cachedData.containsKey(key))
                cachedData.put(key, maybeFetched.get(key));
        }
        return cachedData;
    }

    public Map<String, Integer> getAll(List<String> keys) {
        return getAll(keys, Integer.class);
    }
}
