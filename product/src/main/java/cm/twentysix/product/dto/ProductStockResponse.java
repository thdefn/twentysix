package cm.twentysix.product.dto;

import cm.twentysix.product.domain.model.Product;
import lombok.Builder;

@Builder
public record ProductStockResponse(
        String id,
        String name,
        Integer quantity
) {
    public static ProductStockResponse from(Product product) {
        return ProductStockResponse.builder()
                .quantity(product.getQuantity())
                .name(product.getName())
                .id(product.getId()).build();
    }
}
