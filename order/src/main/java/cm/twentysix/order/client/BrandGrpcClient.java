package cm.twentysix.order.client;

import cm.twentysix.BrandProto.BrandInfo;
import cm.twentysix.BrandProto.BrandInfosRequest;
import cm.twentysix.BrandProto.BrandInfosResponse;
import cm.twentysix.BrandServiceGrpc;
import cm.twentysix.order.cache.local.BrandInfoLocalCacheRepository;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class BrandGrpcClient {
    private final CacheManager cacheManager;
    private final ManagedChannel managedChannel;
    private final BrandInfoLocalCacheRepository brandInfoLocalCacheRepository;
    private final BrandServiceGrpc.BrandServiceBlockingStub brandServiceBlockingStub;

    public BrandGrpcClient(CacheManager cacheManager, @Value("${grpc.client.brand.host}") String host, @Value("${grpc.client.brand.port}") int port, BrandInfoLocalCacheRepository brandInfoLocalCacheRepository) {
        this.cacheManager = cacheManager;
        this.brandInfoLocalCacheRepository = brandInfoLocalCacheRepository;
        managedChannel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
        this.brandServiceBlockingStub = BrandServiceGrpc.newBlockingStub(managedChannel);
    }

    private BrandInfosResponse getBrandInfos(List<Long> brandIds) {
        BrandInfosRequest request = BrandInfosRequest.newBuilder()
                .addAllIds(brandIds).build();
        return brandServiceBlockingStub.getBrandInfos(request);
    }

    public Map<Long, BrandInfo> findBrandInfo(List<Long> brandIds) {
        Map<Long, BrandInfo> brandIdBrandInfo = new HashMap<>();
        List<Long> idsToFetch = new ArrayList<>();

        for (Long brandId : brandIds) {
            Optional<BrandInfo> maybeBrandInfo = brandInfoLocalCacheRepository.get(brandId);
            if (maybeBrandInfo.isPresent()) {
                BrandInfo brandInfo = maybeBrandInfo.get();
                brandIdBrandInfo.put(brandId, brandInfo);
            } else idsToFetch.add(brandId);
        }

        if (!idsToFetch.isEmpty()) {
            BrandInfosResponse response = getBrandInfos(idsToFetch);
            List<BrandInfo> fetched = response.getBrandsList();
            for (BrandInfo info : fetched) {
                brandIdBrandInfo.put(info.getId(), info);
            }

            CompletableFuture.runAsync(() -> {
                fetched.forEach(brandInfo -> brandInfoLocalCacheRepository.put(brandInfo.getId(), brandInfo));
            });

        }

        return brandIdBrandInfo;
    }

    @PreDestroy
    public void shutdown() {
        if (managedChannel != null && !managedChannel.isShutdown()) {
            managedChannel.shutdown();
            try {
                if (!managedChannel.awaitTermination(5, TimeUnit.SECONDS)) {
                    managedChannel.shutdownNow();
                }
            } catch (InterruptedException e) {
                managedChannel.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

}
