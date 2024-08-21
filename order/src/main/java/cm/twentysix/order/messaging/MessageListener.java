package cm.twentysix.order.messaging;

import cm.twentysix.order.dto.PaymentFinalizedEvent;
import cm.twentysix.order.dto.StockCheckFailedEvent;
import cm.twentysix.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
@Slf4j
public class MessageListener {
    private final OrderService orderService;

    @Bean(name = "stock-check-failed")
    public Consumer<StockCheckFailedEvent> stockCheckFailedEventConsumer() {
        return orderService::handleStockCheckFailedEvent;
    }

    @Bean(name = "payment-finalized")
    public Consumer<PaymentFinalizedEvent> paymentFinalizedEventConsumer() {
        return orderService::handlePaymentFinalizedEvent;
    }
}
