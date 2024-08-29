package cm.twentysix.order.dto;

import cm.twentysix.order.domain.model.Order;
import lombok.Builder;

import java.util.Map;
import java.util.stream.Collectors;

@Builder
public record OrderFailedEvent(
        String orderId,
        Map<String, Integer> productQuantity
) {
    public static OrderFailedEvent from(Order order) {
        return OrderFailedEvent.builder()
                .productQuantity(order.getProductIdQuantityMap())
                .orderId(order.getOrderId())
                .build();
    }
}
