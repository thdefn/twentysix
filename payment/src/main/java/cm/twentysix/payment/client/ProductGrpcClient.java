package cm.twentysix.payment.client;

import cm.twentysix.ProductProto;
import cm.twentysix.ProductProto.CheckProductStockResponse;
import cm.twentysix.ProductServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Component
@Slf4j
public class ProductGrpcClient {
    private final ProductServiceGrpc.ProductServiceBlockingStub productServiceBlockingStub;
    private final ManagedChannel managedChannel;

    public ProductGrpcClient(@Value("${grpc.client.product.host}") String host, @Value("${grpc.client.product.port}") int port) {
        managedChannel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        productServiceBlockingStub = ProductServiceGrpc.newBlockingStub(managedChannel);
    }

    public CheckProductStockResponse checkProductStockRequest(Map<String, Integer> productQuantity, String orderId) {
        return productServiceBlockingStub.checkAndUpdateProductStock(
                ProductProto.CheckProductStockRequest.newBuilder()
                        .setOrderId(orderId)
                        .addAllProductQuantity(productQuantity.entrySet()
                                .stream().map(entry ->
                                        ProductProto.ProductIdQuantity.newBuilder()
                                                .setProductId(entry.getKey())
                                                .setQuantity(entry.getValue()).build()
                                ).collect(Collectors.toList())
                        )
                        .build()
        );
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
