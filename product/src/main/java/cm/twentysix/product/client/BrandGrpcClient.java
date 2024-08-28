package cm.twentysix.product.client;

import cm.twentysix.BrandServiceGrpc;
import cm.twentysix.product.cache.local.BrandDetailLocalCacheRepository;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static cm.twentysix.BrandProto.*;

@Component
public class BrandGrpcClient {
    private final BrandServiceGrpc.BrandServiceBlockingStub brandServiceBlockingStub;
    private final ManagedChannel managedChannel;
    private final BrandDetailLocalCacheRepository brandDetailLocalCacheRepository;

    public BrandGrpcClient(@Value("${grpc.client.brand.host}") String host, @Value("${grpc.client.brand.port}") int port, BrandDetailLocalCacheRepository brandDetailLocalCacheRepository) {
        this.brandDetailLocalCacheRepository = brandDetailLocalCacheRepository;
        managedChannel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
        this.brandServiceBlockingStub = BrandServiceGrpc.newBlockingStub(managedChannel);
    }

    public BrandDetailResponse getBrandDetail(Long brandId) {
        Optional<BrandDetailResponse> maybeBrandInfo = brandDetailLocalCacheRepository.get(brandId);
        if (maybeBrandInfo.isPresent())
            return maybeBrandInfo.get();

        BrandDetailResponse response = brandServiceBlockingStub.getBrandDetail(
                BrandDetailRequest.newBuilder()
                        .setId(brandId).build());
        CompletableFuture.runAsync(() -> brandDetailLocalCacheRepository.put(brandId, response));
        return response;
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
