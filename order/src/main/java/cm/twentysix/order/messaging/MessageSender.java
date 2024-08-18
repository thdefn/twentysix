package cm.twentysix.order.messaging;

import cm.twentysix.order.dto.AddressSaveEvent;
import cm.twentysix.order.dto.OrderEvent;
import cm.twentysix.order.dto.OrderFailedEvent;
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

    public boolean sendOrderEvent(OrderEvent event) {
        return streamBridge.send("order-out-0", MessageBuilder.withPayload(event).build());
    }

    public boolean sendAddressSaveEvent(AddressSaveEvent event) {
        return streamBridge.send("address-out-0", MessageBuilder.withPayload(event).build());
    }

    public boolean sendOrderFailedEvent(OrderFailedEvent event) {
        return streamBridge.send("order-failed-out-0", MessageBuilder.withPayload(event).build());
    }

}
