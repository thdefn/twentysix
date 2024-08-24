package cm.twentysix.order.cache.local;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Slf4j
public class ReservedProductStockLocalCacheRepository extends LocalCacheRepository<Integer> {
    public ReservedProductStockLocalCacheRepository(CacheManager cacheManager) {
        super(LocalCacheKey.RESERVED_PRODUCT_STOCK, cacheManager);
    }

    private Integer fetchStockIfAbsent(String productId, int obtainedQuantity) {
        Optional<Integer> cachedStock = get(productId, Integer.class);
        if (cachedStock.isPresent())
            return cachedStock.get();
        put(productId, obtainedQuantity);
        return obtainedQuantity;
    }

    public boolean checkAndReserveStock(String productId, int fetchedQuantity, int quantityToReserve) {
        int cachedQuantity = fetchStockIfAbsent(productId, fetchedQuantity);
        if (quantityToReserve > cachedQuantity)
            return false;
        put(productId, cachedQuantity - quantityToReserve);
        return true;
    }

}
