package cm.twentysix.payment.dto;

import lombok.Builder;

@Builder
public record PaymentAbortedEvent(
        PaymentResponse response
) {
    public static PaymentAbortedEvent of(PaymentResponse response) {
        return PaymentAbortedEvent.builder()
                .response(response)
                .build();
    }

}
