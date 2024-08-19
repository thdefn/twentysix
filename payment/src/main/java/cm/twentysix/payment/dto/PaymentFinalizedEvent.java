package cm.twentysix.payment.dto;

import lombok.Builder;

@Builder
public record PaymentFinalizedEvent(
        String orderId,
        boolean isSuccess
) {
    public static PaymentFinalizedEvent of(String orderId, boolean isSuccess) {
        return PaymentFinalizedEvent.builder()
                .orderId(orderId)
                .isSuccess(isSuccess)
                .build();
    }

}
