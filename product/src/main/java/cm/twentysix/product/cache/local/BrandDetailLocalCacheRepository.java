package cm.twentysix.product.cache.local;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static cm.twentysix.BrandProto.*;

@Repository
@Slf4j
public class BrandDetailLocalCacheRepository extends LocalCacheRepository<BrandDetailResponse> {
    public BrandDetailLocalCacheRepository(CacheManager cacheManager) {
        super(LocalCacheKey.BRAND_INFO, cacheManager);
    }

    public Optional<BrandDetailResponse> get(Long brandId) {
        return get(String.valueOf(brandId), BrandDetailResponse.class);
    }

    public void put(Long brandId, BrandDetailResponse brandResponse) {
        put(String.valueOf(brandId), brandResponse);
    }

}
