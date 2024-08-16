package cm.twentysix.order.client;

import cm.twentysix.ProductServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static cm.twentysix.ProductProto.*;

@Component
@Slf4j
public class ProductGrpcClient {
    private final CacheManager cacheManager;
    private final ProductServiceGrpc.ProductServiceBlockingStub productServiceBlockingStub;
    private final ManagedChannel managedChannel;

    public ProductGrpcClient(CacheManager cacheManager, @Value("${grpc.client.product.host}") String host, @Value("${grpc.client.product.port}") int port) {
        this.cacheManager = cacheManager;
        managedChannel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        productServiceBlockingStub = ProductServiceGrpc.newBlockingStub(managedChannel);
    }

    public ProductItemResponse findProductItem(String productId) {
        Cache cache = cacheManager.getCache("productInfos");
        if (cache != null) {
            Optional<ProductItemResponse> maybeProductItem = Optional.ofNullable(cache.get(productId, ProductItemResponse.class));
            if (maybeProductItem.isPresent())
                return maybeProductItem.get();
        }
        
        ProductItemResponse response = productServiceBlockingStub.getProductItem(
                ProductItemRequest.newBuilder()
                        .setId(productId).build());
        CompletableFuture.runAsync(() -> cache.put(productId, response));
        return response;
    }

    public Map<String, ProductItemResponse> findProductItems(List<String> productIds) {
        Cache cache = cacheManager.getCache("productInfos");
        List<String> idsToFetch = new ArrayList<>();
        Map<String, ProductItemResponse> idProductInfo = new HashMap<>();

        if (cache != null) {
            for (String productId : productIds) {
                Optional<ProductItemResponse> maybeProductInfo = Optional.ofNullable(cache.get(productId, ProductItemResponse.class));
                if (maybeProductInfo.isPresent()) {
                    ProductItemResponse productInfo = maybeProductInfo.get();
                    idProductInfo.put(productId, productInfo);
                } else idsToFetch.add(productId);
            }
        }

        if (!idsToFetch.isEmpty()) {
            List<ProductItemResponse> fetched = getProductItems(productIds).getProductsList();
            fetched.forEach(item -> idProductInfo.put(item.getId(), item));

            CompletableFuture.runAsync(() -> {
                fetched.forEach(productInfo -> cache.put(productInfo.getId(), productInfo));
            });
        }

        return idProductInfo;
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
