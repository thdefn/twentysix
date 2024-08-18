package cm.twentysix.payment.messaging;

import cm.twentysix.payment.dto.ProductOrderFailedEvent;
import cm.twentysix.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
@Slf4j
public class MessageListener {
    private final PaymentService paymentService;

    @Bean(name = "product-order-failed")
    public Consumer<ProductOrderFailedEvent> productOrderFailedEventConsumer() {
        log.error("productOrderFailedEventConsumer");
        return productOrderFailedEvent -> paymentService.handleProductOrderFailedEvent(productOrderFailedEvent);
    }
}
