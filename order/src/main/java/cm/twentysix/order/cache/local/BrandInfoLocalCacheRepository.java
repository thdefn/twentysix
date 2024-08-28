package cm.twentysix.order.cache.local;

import cm.twentysix.BrandProto.BrandInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Slf4j
public class BrandInfoLocalCacheRepository extends LocalCacheRepository<BrandInfo> {
    public BrandInfoLocalCacheRepository(CacheManager cacheManager) {
        super(LocalCacheKey.BRAND_INFO, cacheManager);
    }

    public Optional<BrandInfo> get(Long brandId) {
        return get(String.valueOf(brandId), BrandInfo.class);
    }

    public void put(Long brandId, BrandInfo brandInfo) {
        put(String.valueOf(brandId), brandInfo);
    }

}
