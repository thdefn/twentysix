package cm.twentysix.order.messaging;

import cm.twentysix.order.dto.OrderEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageSender {

    private final StreamBridge streamBridge;

    public boolean sendOrderEvent(OrderEvent event) {
        return streamBridge.send("order-out-0", MessageBuilder.withPayload(event).build());
    }

}
