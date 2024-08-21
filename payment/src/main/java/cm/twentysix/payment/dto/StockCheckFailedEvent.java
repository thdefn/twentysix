package cm.twentysix.payment.dto;

import lombok.Builder;

@Builder
public record StockCheckFailedEvent(
        String orderId
) {

}
