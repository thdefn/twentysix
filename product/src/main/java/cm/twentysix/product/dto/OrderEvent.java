package cm.twentysix.product.dto;

import java.util.Map;


public record OrderEvent(
        String orderId,
        Map<String, Integer> productQuantity
) {
}
