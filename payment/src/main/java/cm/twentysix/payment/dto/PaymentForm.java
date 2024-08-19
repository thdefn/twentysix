package cm.twentysix.payment.dto;

import jakarta.validation.constraints.NotBlank;

public record PaymentForm(
        @NotBlank(message = "주문 아이디는 비어있을 수 없습니다.")
        String orderId,
        @NotBlank(message = "결제 금액은 비어있을 수 없습니다.")
        String amount,
        @NotBlank(message = "payment key 는 비어있을 수 없습니다.")
        String paymentKey
) {
}
