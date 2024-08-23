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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static cm.twentysix.OrderProto.OrderInfoResponse;
import static cm.twentysix.payment.exception.Error.*;

@RequiredArgsConstructor
@Service
@Slf4j
public class PaymentService {
    private final OrderGrpcClient orderGrpcClient;
    private final PaymentRepository paymentRepository;
    private final PaymentClient paymentClient;
    private final MessageSender messageSender;
    private final ApplicationEventPublisher applicationEventPublisher;

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
                .orElseThrow(() -> new PaymentException(NOT_FOUND_PAYMENT));

        validatePendingPayment(payment);

        PaymentResponse response = paymentClient.confirm(form);
        if ("DONE".equals(response.status())) {
            payment.confirmPayment(response);
            payment.complete(response);
            messageSender.sendPaymentFinalizedEvent(PaymentFinalizedEvent.of(response.orderId(), true));
        } else if ("ABORTED".equals(response.status())) {
            applicationEventPublisher.publishEvent(PaymentAbortedEvent.of(response));
            throw new PaymentException(PAYMENT_FAILED);
        }
        // TODO : 429 500 토스 측에서 에러 났을 때 핸들링
    }

    private static void validatePendingPayment(Payment payment) {
        if (PaymentStatus.BLOCK.equals(payment.getStatus()))
            throw new PaymentException(STOCK_SHORTAGE);

        if (PaymentStatus.COMPLETE.equals(payment.getStatus()))
            throw new PaymentException(ALREADY_PAID_ORDER);

        if (PaymentStatus.CANCEL.equals(payment.getStatus()))
            throw new PaymentException(CANCELLED_ORDER);
    }

    @Transactional
    public void cancelOrBlockPayment(String orderId, String cancelReason) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseGet(() -> Payment.of(orderId, PaymentStatus.BLOCK));

        if (PaymentStatus.COMPLETE.equals(payment.getStatus())) {
            paymentClient.cancel(payment.getPaymentKey(), PaymentCancelForm.of(cancelReason));
            payment.cancel();
        } else payment.block();
        paymentRepository.save(payment);
    }

}
