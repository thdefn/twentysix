package cm.twentysix.payment.client;

import cm.twentysix.payment.dto.PaymentCancelForm;
import cm.twentysix.payment.dto.PaymentForm;
import cm.twentysix.payment.dto.PaymentResponse;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.ThreadLocalRandom;

public class MockPaymentClient implements PaymentClient {
    private static final double CANCEL_PROBABILITY = 0.2;
    private static final ZoneOffset koreaOffset = ZoneOffset.of("+09:00");

    @Override
    public PaymentResponse confirm(PaymentForm form) {
        String now = OffsetDateTime.now().withOffsetSameLocal(koreaOffset).toString();
        if (ThreadLocalRandom.current().nextDouble() < CANCEL_PROBABILITY)
            return PaymentResponse.builder()
                    .orderId(form.orderId())
                    .totalAmount(Integer.parseInt(form.amount()))
                    .paymentKey(form.orderId())
                    .requestedAt(now)
                    .method("간편결제")
                    .easyPay(new PaymentResponse.EasyPay("카카오페이", Integer.parseInt(form.amount()), 0))
                    .status("ABORTED").build();
        return PaymentResponse.builder()
                .orderId(form.orderId())
                .totalAmount(Integer.parseInt(form.amount()))
                .paymentKey(form.orderId())
                .requestedAt(now)
                .approvedAt(now)
                .method("간편결제")
                .easyPay(new PaymentResponse.EasyPay("카카오페이", Integer.parseInt(form.amount()), 0))
                .status("DONE").build();
    }

    @Override
    public String cancel(String paymentKey, PaymentCancelForm form) {
        return "cancelled";
    }
}
