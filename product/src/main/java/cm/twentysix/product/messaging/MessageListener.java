package cm.twentysix.product.messaging;

import cm.twentysix.product.dto.OrderCancelledEvent;
import cm.twentysix.product.dto.OrderEvent;
import cm.twentysix.product.dto.OrderFailedEvent;
import cm.twentysix.product.service.ProductStockFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class MessageListener {
    private final ProductStockFacade inventoryFacade;

    @Bean(name = "order")
    public Consumer<OrderEvent> orderEventConsumer() {
        return orderEvent -> inventoryFacade.handleOrder(orderEvent.productQuantity(), orderEvent.orderId());
    }

    @Bean(name = "order-failed")
    public Consumer<OrderFailedEvent> orderFailedEventConsumer() {
        return orderFailedEvent -> inventoryFacade.rollbackOrder(orderFailedEvent.productQuantity());
    }

    @Bean(name = "order-cancelled")
    public Consumer<OrderCancelledEvent> orderCancelledEventConsumer() {
        return orderCancelledEvent -> inventoryFacade.rollbackOrder(orderCancelledEvent.productQuantity());
    }

}
