package cm.twentysix.order.messaging;

import cm.twentysix.order.dto.ProductOrderFailedEvent;
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

    @Bean(name = "product-order-failed")
    public Consumer<ProductOrderFailedEvent> productOrderFailedEventConsumer() {
        log.error("productOrderFailedEventConsumer");
        return orderService::handleProductOrderFailedEvent;
    }
}
