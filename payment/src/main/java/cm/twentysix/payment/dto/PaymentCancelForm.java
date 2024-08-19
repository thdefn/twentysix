package cm.twentysix.payment.dto;

import lombok.Builder;

@Builder
public record PaymentCancelForm(
        String cancelReason
) {
    public static PaymentCancelForm of(String cancelReason) {
        return PaymentCancelForm.builder()
                .cancelReason(cancelReason).build();
    }

}
