package cm.twentysix.payment.dto;

import lombok.Builder;

import java.util.Map;

@Builder
public record OrderCancelledEvent(
        String orderId,
        Map<String, Integer> productQuantity
) {
}
