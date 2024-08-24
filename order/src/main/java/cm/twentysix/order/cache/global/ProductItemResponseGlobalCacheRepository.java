package cm.twentysix.order.cache.global;

import cm.twentysix.ProductProto.ProductItemResponse;
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
public class ProductItemResponseGlobalCacheRepository extends GlobalCacheRepository {
    private final RedisClient<ProductItemResponse> redisClient;

    protected ProductItemResponseGlobalCacheRepository(RedisClient<ProductItemResponse> redisClient) {
        super(GlobalCacheKey.PRODUCT_ITEM_RESPONSE);
        this.redisClient = redisClient;
    }

    public Map<String, ProductItemResponse> getAll(List<String> productIds) {
        List<String> cacheKeys = productIds.stream().map(this::getCacheKey).toList();
        List<ProductItemResponse> values = redisClient.getValues(cacheKeys);

        if (values.isEmpty())
            return new HashMap<>();
        return values.stream()
                .collect(Collectors.toMap(ProductItemResponse::getId, product -> product));
    }

    public Optional<ProductItemResponse> get(String productId) {
        return Optional.ofNullable(redisClient.getValue(getCacheKey(productId)));
    }

}
