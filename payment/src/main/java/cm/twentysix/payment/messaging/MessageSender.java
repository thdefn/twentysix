package cm.twentysix.payment.messaging;

import cm.twentysix.payment.dto.PaymentFinalizedEvent;
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

    public boolean sendPaymentFinalizedEvent(PaymentFinalizedEvent event) {
        return streamBridge.send("payment-finalized-out-0", MessageBuilder.withPayload(event).build());
    }

}
