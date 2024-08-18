package cm.twentysix.product.messaging;

import cm.twentysix.product.dto.ProductOrderFailedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class MessageSender {

    private final StreamBridge streamBridge;

    public boolean sendProductOrderFailedEvent(ProductOrderFailedEvent event) {
        log.error(event.toString());
        return streamBridge.send("product-order-failed-out-0", MessageBuilder.withPayload(event).build());
    }
}
