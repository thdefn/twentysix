package cm.twentysix.order.messaging;

import cm.twentysix.order.dto.OrderReplyEvent;
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
    @Bean(name = "order-reply")
    public Consumer<OrderReplyEvent> orderReplyEventConsumer() {
        return orderReplyEvent -> orderService.approveOrDenyOrder(orderReplyEvent);
    }
}
