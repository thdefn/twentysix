package cm.twentysix.payment.service;

import cm.twentysix.payment.client.OrderGrpcClient;
import cm.twentysix.payment.client.PaymentClient;
import cm.twentysix.payment.domain.model.Payment;
import cm.twentysix.payment.domain.model.PaymentStatus;
import cm.twentysix.payment.domain.repository.PaymentRepository;
import cm.twentysix.payment.dto.*;
import cm.twentysix.payment.exception.PaymentException;
import cm.twentysix.payment.messaging.MessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static cm.twentysix.OrderProto.OrderInfoResponse;
import static cm.twentysix.payment.exception.Error.STOCK_SHORTAGE;

@RequiredArgsConstructor
@Service
@Slf4j
public class PaymentService {
    private final OrderGrpcClient orderGrpcClient;
    private final PaymentRepository paymentRepository;
    private final PaymentClient paymentClient;
    private final MessageSender messageSender;

    public RequiredPaymentResponse getRequiredPayment(String orderId) {
        OrderInfoResponse orderInfo = orderGrpcClient.getOrderInfo(orderId);
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseGet(() -> Payment.of(orderId, PaymentStatus.PENDING));
        payment.updateOrderInfo(orderInfo);
        return RequiredPaymentResponse.from(paymentRepository.save(payment));
    }

    @Transactional
    public void confirm(PaymentForm form) {
        Payment payment = paymentRepository.findByOrderId(form.orderId())
                .stream().findFirst()
                .filter(p -> PaymentStatus.PENDING.equals(p.getStatus()))
                .orElseThrow(() -> new PaymentException(STOCK_SHORTAGE));

        PaymentResponse response = paymentClient.confirm(form);
        payment.confirmPayment(response);
        if ("DONE".equals(response.status())) {
            payment.complete(response);
            messageSender.sendPaymentFinalizedEvent(PaymentFinalizedEvent.of(response.orderId(), true));
        } else if ("ABORTED".equals(response.status())) {
            payment.cancel();
            messageSender.sendPaymentFinalizedEvent(PaymentFinalizedEvent.of(response.orderId(), false));
        }
        // TODO : 429 500 토스 측에서 에러 났을 때 핸들링
    }

    @Transactional
    public void handleProductOrderFailedEvent(ProductOrderFailedEvent event) {
        Payment payment = paymentRepository.findByOrderId(event.orderId())
                .orElseGet(() -> Payment.of(event.orderId(), PaymentStatus.BLOCK));

        if (PaymentStatus.COMPLETE.equals(payment.getStatus())) {
            paymentClient.cancel(payment.getPaymentKey(), PaymentCancelForm.of(STOCK_SHORTAGE.message));
            payment.cancel();
        } else payment.block();
        paymentRepository.save(payment);
    }

}
