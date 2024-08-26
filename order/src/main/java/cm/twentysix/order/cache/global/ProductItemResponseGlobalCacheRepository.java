package cm.twentysix.order.cache.global;

import cm.twentysix.ProductProto.ProductItemResponse;
import cm.twentysix.order.client.RedisClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@Slf4j
public class ProductItemResponseGlobalCacheRepository extends GlobalCacheRepository<ProductItemResponse, ProductItemResponse> {

    protected ProductItemResponseGlobalCacheRepository(RedisClient<ProductItemResponse> redisClient) {
        super(GlobalCacheKey.PRODUCT_ITEM_RESPONSE, redisClient);
    }

    public Map<String, ProductItemResponse> getAll(List<String> productIds) {
        return getAll(productIds, ProductItemResponse.class);
    }

    public Optional<ProductItemResponse> get(String productId) {
        return get(productId, ProductItemResponse.class);
    }


}
