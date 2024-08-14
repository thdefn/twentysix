package cm.twentysix.order.handler;

import cm.twentysix.order.dto.OrderEvent;
import cm.twentysix.order.exception.Error;
import cm.twentysix.order.messaging.MessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class OrderEventHandler {
    private final MessageSender messageSender;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleReceiveOrderEvent(OrderEvent orderEvent) {
        if (!messageSender.sendOrderEvent(orderEvent))
            throw new RuntimeException(Error.MESSAGE_SEND_ERROR.message);
    }
}
