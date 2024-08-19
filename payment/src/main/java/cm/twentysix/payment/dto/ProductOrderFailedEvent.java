package cm.twentysix.payment.dto;

import lombok.Builder;

@Builder
public record ProductOrderFailedEvent(
        String orderId
) {

}
