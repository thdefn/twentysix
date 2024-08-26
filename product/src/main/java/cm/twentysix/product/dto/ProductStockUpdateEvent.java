package cm.twentysix.product.dto;

import lombok.Builder;

import java.util.Set;

@Builder
public record ProductStockUpdateEvent(
        Set<String> productIds
) {
    public static ProductStockUpdateEvent of(Set<String> productIds) {
        return ProductStockUpdateEvent.builder()
                .productIds(productIds)
                .build();
    }
}
