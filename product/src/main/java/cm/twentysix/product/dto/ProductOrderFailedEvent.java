package cm.twentysix.product.dto;

import lombok.Builder;

@Builder
public record ProductOrderFailedEvent(
        String orderId
) {

    public static ProductOrderFailedEvent of(String orderId) {
        return ProductOrderFailedEvent.builder()
                .orderId(orderId)
                .build();
    }

}
