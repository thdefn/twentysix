package cm.twentysix.order.client;

import cm.twentysix.ProductServiceGrpc;
import cm.twentysix.order.cache.ProductItemResponseGlobalCacheRepository;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static cm.twentysix.ProductProto.*;

@Component
@Slf4j
public class ProductGrpcClient {
    private final ProductServiceGrpc.ProductServiceBlockingStub productServiceBlockingStub;
    private final ManagedChannel managedChannel;
    private final ProductItemResponseGlobalCacheRepository productItemResponseGlobalCacheRepository;

    public ProductGrpcClient(ProductItemResponseGlobalCacheRepository productItemResponseGlobalCacheRepository, @Value("${grpc.client.product.host}") String host, @Value("${grpc.client.product.port}") int port) {
        this.productItemResponseGlobalCacheRepository = productItemResponseGlobalCacheRepository;
        managedChannel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        productServiceBlockingStub = ProductServiceGrpc.newBlockingStub(managedChannel);
    }

    public ProductItemResponse findProductItem(String productId) {
        Optional<ProductItemResponse> maybeProductItemResponse = productItemResponseGlobalCacheRepository.get(productId);
        return maybeProductItemResponse.orElseGet(() -> productServiceBlockingStub.getProductItem(
                ProductItemRequest.newBuilder()
                        .setId(productId).build()));
    }

    public List<ProductItemResponse> findProductItems(List<String> productIds) {
        List<ProductItemResponse> response = new ArrayList<>();

        List<String> missingIds = findCachedProductInfosAndIdentifyMissingIds(response, productIds);

        if (!missingIds.isEmpty()) {
            List<ProductItemResponse> fetched = getProductItems(productIds).getProductsList();
            response.addAll(fetched);
        }

        return response;
    }

    private List<String> findCachedProductInfosAndIdentifyMissingIds(List<ProductItemResponse> response, List<String> productIds) {
        List<String> missingIds = new ArrayList<>();

        Map<String, ProductItemResponse> cached = productItemResponseGlobalCacheRepository.getAll(productIds);
        for (String productId : productIds) {
            if (cached.containsKey(productId))
                response.add(cached.get(productId));
            else missingIds.add(productId);
        }

        return missingIds;
    }

    private ProductItemsResponse getProductItems(List<String> productIds) {
        return productServiceBlockingStub.getProductItems(
                ProductItemsRequest.newBuilder()
                        .addAllIds(productIds).build());
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
