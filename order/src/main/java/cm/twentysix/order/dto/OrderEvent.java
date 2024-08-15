package cm.twentysix.order.dto;

import lombok.Builder;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Builder
public record OrderEvent(
        String orderId,
        Map<String, Integer> productQuantity
) {
    public static OrderEvent of(List<ProductItem> products, String orderId) {
        return OrderEvent.builder()
                .productQuantity(products.stream().collect(Collectors.toMap(
                        ProductItem::id,
                        ProductItem::quantity)))
                .orderId(orderId)
                .build();
    }
}
