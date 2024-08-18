package cm.twentysix.order.dto;

import cm.twentysix.order.domain.model.Order;
import lombok.Builder;

import java.util.Map;
import java.util.stream.Collectors;

@Builder
public record OrderEvent(
        String orderId,
        Map<String, Integer> productQuantity
) {
    public static OrderEvent of(Order order) {
        return OrderEvent.builder()
                .productQuantity(order.getProducts().entrySet().stream().collect(Collectors.toMap(
                        idProductEntry -> idProductEntry.getKey(),
                        idProductEntry -> idProductEntry.getValue().getQuantity()
                )))
                .orderId(order.getOrderId())
                .build();
    }
}
