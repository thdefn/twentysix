package cm.twentysix.order.dto;

import lombok.Builder;

import java.util.Map;

@Builder
public record OrderReplyEvent(
        String orderId,
        boolean isSuccess,
        Map<String, ProductOrderItem> orderedItem
) {
}
