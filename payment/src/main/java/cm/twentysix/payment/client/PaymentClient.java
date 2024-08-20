package cm.twentysix.payment.client;

import cm.twentysix.payment.dto.PaymentCancelForm;
import cm.twentysix.payment.dto.PaymentForm;
import cm.twentysix.payment.dto.PaymentResponse;

public interface PaymentClient {
    PaymentResponse confirm(PaymentForm form);

    String cancel(String paymentKey, PaymentCancelForm form);
}
