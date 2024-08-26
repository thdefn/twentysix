package cm.twentysix.payment.dto;

import lombok.Builder;

@Builder
public record PaymentConditionFailedEvent(
        String orderId,
        boolean shouldNotifyFailed
) {
    public static PaymentConditionFailedEvent of(String orderId, boolean shouldNotifyFailed) {
        return PaymentConditionFailedEvent.builder()
                .orderId(orderId)
                .shouldNotifyFailed(shouldNotifyFailed)
                .build();
    }
}
