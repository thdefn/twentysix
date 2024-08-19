package cm.twentysix.payment.messaging;

import cm.twentysix.payment.dto.OrderCancelledEvent;
import cm.twentysix.payment.dto.ProductOrderFailedEvent;
import cm.twentysix.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

import static cm.twentysix.payment.constant.CancelReason.CUSTOMER_DECISION;
import static cm.twentysix.payment.constant.CancelReason.STOCK_SHORTAGE;


@Component
@RequiredArgsConstructor
@Slf4j
public class MessageListener {
    private final PaymentService paymentService;

    @Bean(name = "product-order-failed")
    public Consumer<ProductOrderFailedEvent> productOrderFailedEventConsumer() {
        return productOrderFailedEvent -> paymentService.cancelOrBlockPayment(productOrderFailedEvent.orderId(), STOCK_SHORTAGE.message);
    }

    @Bean(name = "order-cancelled")
    public Consumer<OrderCancelledEvent> orderCancelledEventConsumer() {
        return orderCancelledEvent -> paymentService.cancelOrBlockPayment(orderCancelledEvent.orderId(), CUSTOMER_DECISION.message);
    }
}
