package cm.twentysix.order.dto;

import lombok.Builder;

@Builder
public record ReceiveOrderResponse(
        String orderId
) {
    public static ReceiveOrderResponse of(String orderId) {
        return ReceiveOrderResponse.builder()
                .orderId(orderId).build();
    }

}
