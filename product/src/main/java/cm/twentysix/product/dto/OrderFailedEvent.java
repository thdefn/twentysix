package cm.twentysix.product.dto;

import lombok.Builder;

import java.util.Map;

@Builder
public record OrderFailedEvent(
        String orderId,
        Map<String, Integer> productQuantity
) {
}
