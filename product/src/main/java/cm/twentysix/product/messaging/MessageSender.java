package cm.twentysix.product.messaging;

import cm.twentysix.product.dto.OrderReplyEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageSender {

    private final StreamBridge streamBridge;

    public boolean sendOrderReplyEvent(OrderReplyEvent event) {
        return streamBridge.send("order-reply-out-0", MessageBuilder.withPayload(event).build());
    }
}
