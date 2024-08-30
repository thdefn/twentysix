package cm.twentysix.order.cache.global;

import cm.twentysix.order.client.RedisClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class ReservedProductStockGlobalCacheRepository extends GlobalCacheRepository<Object, Integer> {
    protected ReservedProductStockGlobalCacheRepository(RedisClient<Object> redisClient) {
        super(GlobalCacheKey.RESERVED_PRODUCT_STOCK, redisClient);
    }

    public Map<String, Integer> getOrFetchIfAbsent(List<String> keys, Map<String, Integer> maybeFetched) {
        Map<String, Integer> cachedData = getAll(keys);
        Map<String, Integer> fetchedData = new HashMap<>();
        for (String key : keys) {
            if (!cachedData.containsKey(key))
                fetchedData.put(key, maybeFetched.get(key));
        }
        if (!fetchedData.isEmpty())
            putAllIfAbsent(fetchedData);
        cachedData.putAll(fetchedData);
        return cachedData;
    }

    public Integer getOrFetchInfAbsent(String key, Integer fetchedStock) {
        Optional<Integer> maybeInteger = get(key, Integer.class);
        if (maybeInteger.isEmpty()) {
            put(key, fetchedStock);
            return fetchedStock;
        }
        return maybeInteger.get();
    }

    public Map<String, Integer> getAll(List<String> keys) {
        return getAll(keys, Integer.class);
    }

    public void decrementStocks(Map<String, Integer> productIdQuantityMap) {
        Map<String, Integer> cacheKeyQuantityMap = productIdQuantityMap.entrySet().stream().collect(Collectors.toMap(
                entry -> getCacheKey(entry.getKey()),
                Map.Entry::getValue
        ));
        redisClient.decrementValuesBy(cacheKeyQuantityMap);
    }

    public void incrementStocks(Map<String, Integer> productIdQuantityMap) {
        Map<String, Integer> cacheKeyQuantityMap = productIdQuantityMap.entrySet().stream().collect(Collectors.toMap(
                entry -> getCacheKey(entry.getKey()),
                Map.Entry::getValue
        ));
        redisClient.incrementValuesBy(cacheKeyQuantityMap);
    }
}
