package cm.twentysix.payment.messaging;

import cm.twentysix.payment.dto.ProductOrderFailedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
@Slf4j
public class MessageListener {

    @Bean(name = "product-order-failed")
    public Consumer<ProductOrderFailedEvent> productOrderFailedEventConsumer() {
        return null;
    }
}
