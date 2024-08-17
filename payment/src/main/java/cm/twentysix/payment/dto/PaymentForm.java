package cm.twentysix.payment.dto;

public record PaymentForm(
        String orderId,
        String amount,
        String paymentKey
) {
}
