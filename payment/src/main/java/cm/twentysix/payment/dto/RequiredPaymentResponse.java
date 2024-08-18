package cm.twentysix.payment.dto;

import cm.twentysix.payment.domain.model.Payment;
import lombok.Builder;

@Builder
public record RequiredPaymentResponse(
        String orderId,
        String orderName,
        Integer amount,
        Long userId
) {
    public static RequiredPaymentResponse from(Payment payment) {
        return RequiredPaymentResponse.builder()
                .amount(payment.getAmount())
                .orderName(payment.getOrderName())
                .userId(payment.getUserId())
                .orderId(payment.getOrderId())
                .build();
    }
}
