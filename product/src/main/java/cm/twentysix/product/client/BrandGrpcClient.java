package cm.twentysix.product.client;

import cm.twentysix.BrandProto;
import cm.twentysix.BrandServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;

@Component
public class BrandGrpcClient {
    private final BrandServiceGrpc.BrandServiceBlockingStub brandServiceBlockingStub;
    private final ManagedChannel managedChannel;

    public BrandGrpcClient(@Value("${grpc.client.brand.host}") String host, @Value("${grpc.client.brand.port}") int port) {
        managedChannel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
        this.brandServiceBlockingStub = BrandServiceGrpc.newBlockingStub(managedChannel);
    }

    public BrandProto.BrandResponse getBrandInfo(Long brandId) {
        BrandProto.BrandRequest request = BrandProto.BrandRequest.newBuilder()
                .setId(brandId).build();
        return brandServiceBlockingStub.getBrand(request);
    }

    public BrandProto.BrandDetailResponse getBrandDetail(Long brandId) {
        BrandProto.BrandDetailRequest request = BrandProto.BrandDetailRequest.newBuilder()
                .setId(brandId).build();
        return brandServiceBlockingStub.getBrandDetail(request);
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
