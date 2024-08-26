package cm.twentysix.payment.dto;

import cm.twentysix.payment.domain.model.Payment;
import lombok.Builder;

import java.util.Map;

@Builder
public record OrderFailedEvent(
        String orderId,
        Map<String, Integer> productQuantity
) {
    public static OrderFailedEvent from(Payment payment) {
        return OrderFailedEvent.builder()
                .productQuantity(payment.getProductQuantity())
                .orderId(payment.getOrderId())
                .build();
    }
}
