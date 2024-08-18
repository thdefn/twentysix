package cm.twentysix.order.dto;

import lombok.Builder;

@Builder
public record PaymentFinalizedEvent(
        String orderId,
        boolean isSuccess
) {

}
