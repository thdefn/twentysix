package cm.twentysix.product.messaging;

import cm.twentysix.product.dto.OrderEvent;
import cm.twentysix.product.dto.OrderFailedEvent;
import cm.twentysix.product.service.ProductStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class MessageListener {
    private final ProductStockService productOrderService;

    @Bean(name = "order")
    public Consumer<OrderEvent> orderEventConsumer() {
        return orderEvent -> productOrderService.checkProductStock(orderEvent.productQuantity(), orderEvent.orderId());
    }

    @Bean(name = "order-failed")
    public Consumer<OrderFailedEvent> orderFailedEventConsumer() {
        return orderFailedEvent -> productOrderService.restoreProductStock(orderFailedEvent.productQuantity());
    }


}
