package cm.twentysix.product.dto;

import lombok.Builder;

import java.util.Map;

@Builder
public record OrderReplyEvent(
        String orderId,
        boolean isSuccess,
        Map<String, ProductOrderItem> orderedItem
) {
    public static OrderReplyEvent of(String orderId, boolean isSuccess) {
        return OrderReplyEvent.builder()
                .isSuccess(isSuccess)
                .orderId(orderId).build();
    }

    public static OrderReplyEvent of(String orderId, boolean isSuccess, Map<String, ProductOrderItem> orderedItem) {
        return OrderReplyEvent.builder()
                .isSuccess(isSuccess)
                .orderedItem(orderedItem)
                .orderId(orderId).build();
    }
}
