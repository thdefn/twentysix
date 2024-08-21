package cm.twentysix.product.messaging;

import cm.twentysix.product.dto.StockCheckFailedEvent;
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

    public boolean sendProductOrderFailedEvent(StockCheckFailedEvent event) {
        return streamBridge.send("stock-check-failed-out-0", MessageBuilder.withPayload(event).build());
    }
}
