package cm.twentysix.product.dto;

import lombok.Builder;

@Builder
public record StockCheckFailedEvent(
        String orderId
) {

    public static StockCheckFailedEvent of(String orderId) {
        return StockCheckFailedEvent.builder()
                .orderId(orderId)
                .build();
    }

}
