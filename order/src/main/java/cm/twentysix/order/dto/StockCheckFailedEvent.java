package cm.twentysix.order.dto;

import lombok.Builder;

@Builder
public record StockCheckFailedEvent(
        String orderId
) {

}
