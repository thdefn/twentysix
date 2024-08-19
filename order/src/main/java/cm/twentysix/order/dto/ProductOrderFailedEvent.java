package cm.twentysix.order.dto;

import lombok.Builder;

@Builder
public record ProductOrderFailedEvent(
        String orderId
) {

}
