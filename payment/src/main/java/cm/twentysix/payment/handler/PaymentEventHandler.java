package cm.twentysix.payment.handler;

import cm.twentysix.payment.domain.model.Payment;
import cm.twentysix.payment.domain.model.PaymentStatus;
import cm.twentysix.payment.domain.repository.PaymentRepository;
import cm.twentysix.payment.dto.OrderFailedEvent;
import cm.twentysix.payment.dto.PaymentAbortedEvent;
import cm.twentysix.payment.dto.PaymentFinalizedEvent;
import cm.twentysix.payment.dto.PaymentConditionFailedEvent;
import cm.twentysix.payment.exception.Error;
import cm.twentysix.payment.exception.PaymentException;
import cm.twentysix.payment.messaging.MessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentEventHandler {
    private final PaymentRepository paymentRepository;
    private final MessageSender messageSender;

    @EventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handlePaymentCancelEvent(PaymentAbortedEvent event) {
        Payment payment = paymentRepository.findByOrderId(event.response().orderId())
                .stream().findFirst()
                .filter(p -> PaymentStatus.PENDING.equals(p.getStatus()))
                .orElseThrow(() -> new PaymentException(Error.NOT_FOUND_PAYMENT));

        payment.confirmPayment(event.response());
        payment.cancel();
        messageSender.sendPaymentFinalizedEvent(PaymentFinalizedEvent.of(event.response().orderId(), false));
        messageSender.sendOrderFailedEvent(OrderFailedEvent.from(payment));
    }

    @EventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handlePaymentConditionFailedEvent(PaymentConditionFailedEvent event) {
        Payment payment = paymentRepository.findByOrderId(event.orderId())
                .stream().findFirst()
                .filter(p -> PaymentStatus.PENDING.equals(p.getStatus()))
                .orElseThrow(() -> new PaymentException(Error.NOT_FOUND_PAYMENT));

        payment.block();
    }
}
