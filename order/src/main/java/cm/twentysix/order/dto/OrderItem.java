package cm.twentysix.order.dto;

import cm.twentysix.order.domain.model.Order;
import cm.twentysix.order.domain.model.OrderStatus;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record OrderItem(
        Long id,
        String orderNumber,
        LocalDateTime orderingAt,
        OrderStatus status,
        List<OrderBrandItem> brands
) {
    public static OrderItem from(Order order, List<OrderBrandItem> brands) {
        return OrderItem.builder()
                .id(order.getId())
                .orderNumber(order.getOrderId())
                .orderingAt(order.getCreatedAt())
                .status(order.getStatus())
                .brands(brands)
                .build();
    }

}
