package cm.twentysix.product.cache.global;

import cm.twentysix.ProductProto.ProductItemResponse;
import cm.twentysix.product.client.RedisClient;
import cm.twentysix.product.domain.model.Product;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class ProductItemResponseGlobalCacheRepository extends GlobalCacheRepository<ProductItemResponse, ProductItemResponse> {

    protected ProductItemResponseGlobalCacheRepository(RedisClient<ProductItemResponse> redisClient) {
        super(GlobalCacheKey.PRODUCT_ITEM_RESPONSE, redisClient);
    }

    public void put(Product product) {
        put(product.getId(), toProductItemResponse(product));
    }

    public void putAll(List<Product> products) {
        Map<String, ProductItemResponse> keyValues =
                products.stream().collect(Collectors.toMap(product -> getCacheKey(product.getId()), this::toProductItemResponse));
        putAll(keyValues);
    }

    private ProductItemResponse toProductItemResponse(Product product) {
        return ProductItemResponse.newBuilder()
                .setId(product.getId())
                .setBrandId(product.getProductBrand().getId())
                .setName(product.getName())
                .setThumbnail(product.getThumbnailPath())
                .setDiscount(product.getDiscount())
                .setPrice(product.getPrice())
                .setDiscountedPrice(product.getDiscountedPrice())
                .setQuantity(product.getQuantity())
                .setBrandName(product.getProductBrand().getName())
                .setOrderingOpensAt(product.getOrderingOpensAt().toString())
                .build();
    }

}
