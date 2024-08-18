package cm.twentysix.payment.service;

import cm.twentysix.payment.client.OrderGrpcClient;
import cm.twentysix.payment.client.PaymentClient;
import cm.twentysix.payment.domain.model.Payment;
import cm.twentysix.payment.domain.model.PaymentStatus;
import cm.twentysix.payment.domain.repository.PaymentRepository;
import cm.twentysix.payment.dto.*;
import cm.twentysix.payment.exception.Error;
import cm.twentysix.payment.exception.PaymentException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static cm.twentysix.OrderProto.OrderInfoResponse;

@RequiredArgsConstructor
@Service
@Slf4j
public class PaymentService {
    private final OrderGrpcClient orderGrpcClient;
    private final PaymentRepository paymentRepository;
    private final PaymentClient paymentClient;

    public RequiredPaymentResponse getRequiredPayment(String orderId) {
        OrderInfoResponse orderInfo = orderGrpcClient.getOrderInfo(orderId);
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseGet(() -> Payment.of(orderId, PaymentStatus.PENDING));
        payment.updateOrderInfo(orderInfo);
        return RequiredPaymentResponse.from(paymentRepository.save(payment));
    }

    public boolean confirmOrStop(PaymentForm form) {
        Optional<Payment> maybePayment = paymentRepository.findByOrderId(form.orderId())
                .stream().findFirst()
                .filter(p -> PaymentStatus.PENDING.equals(p.getStatus()));
        if (maybePayment.isEmpty())
            return false;

        paymentClient.confirm(form);
        return true;
    }

    @Transactional
    public void handleProductOrderFailedEvent(ProductOrderFailedEvent event) {
        Payment payment = paymentRepository.findByOrderId(event.orderId())
                .orElseGet(() -> Payment.of(event.orderId(), PaymentStatus.BLOCK));

        if (PaymentStatus.COMPLETE.equals(payment.getStatus())) {
            paymentClient.cancel(payment.getPaymentKey(), PaymentCancelForm.of(Error.STOCK_SHORTAGE.message));
            payment.cancel();
        } else payment.block();
        paymentRepository.save(payment);
    }

    @Transactional
    public boolean success(String paymentKey, String orderId, Integer amount) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new PaymentException(Error.NOT_FOUND_PAYMENT));

        if (PaymentStatus.BLOCK.equals(payment.getStatus())) {
            paymentClient.cancel(paymentKey, PaymentCancelForm.of(Error.STOCK_SHORTAGE.message));
            payment.cancel();
            return false;
        }
        payment.complete(paymentKey, amount);
        return true;
    }

}
