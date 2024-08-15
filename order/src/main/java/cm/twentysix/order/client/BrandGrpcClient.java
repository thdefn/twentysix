package cm.twentysix.order.client;

import cm.twentysix.BrandProto.BrandInfo;
import cm.twentysix.BrandProto.BrandInfosRequest;
import cm.twentysix.BrandProto.BrandInfosResponse;
import cm.twentysix.BrandServiceGrpc;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
@RequiredArgsConstructor
public class BrandGrpcClient {
    private final CacheManager cacheManager;
    @GrpcClient("brand")
    private BrandServiceGrpc.BrandServiceBlockingStub brandServiceBlockingStub;

    public BrandInfosResponse getBrandInfos(List<Long> brandIds) {
        BrandInfosRequest request = BrandInfosRequest.newBuilder()
                .addAllIds(brandIds).build();
        return brandServiceBlockingStub.getBrandInfos(request);
    }

    public Map<Long, BrandInfo> findBrandInfo(List<Long> brandIds) {
        Cache cache = cacheManager.getCache("brandInfos");
        Map<Long, BrandInfo> idBrandInfo = new HashMap<>();
        List<Long> idsToFetch = new ArrayList<>();


        if (cache != null) {
            for (Long brandId : brandIds) {
                Optional<BrandInfo> maybeBrandInfo = Optional.ofNullable(cache.get(brandId, BrandInfo.class));
                if (maybeBrandInfo.isPresent()) {
                    BrandInfo brandInfo = maybeBrandInfo.get();
                    idBrandInfo.put(brandId, brandInfo);
                } else idsToFetch.add(brandId);
            }
        }

        if (!idsToFetch.isEmpty()) {
            BrandInfosResponse response = getBrandInfos(idsToFetch);
            List<BrandInfo> fetched = response.getBrandsList();
            for (BrandInfo info : fetched) {
                idBrandInfo.put(info.getId(), info);
            }

            CompletableFuture.runAsync(() -> {
                fetched.forEach(brandInfo -> cache.put(brandInfo.getId(), brandInfo));
            });

        }

        return idBrandInfo;

    }

}
