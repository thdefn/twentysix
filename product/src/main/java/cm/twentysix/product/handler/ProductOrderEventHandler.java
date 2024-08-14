package cm.twentysix.product.handler;

import cm.twentysix.product.dto.OrderReplyEvent;
import cm.twentysix.product.messaging.MessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import static cm.twentysix.product.exception.Error.MESSAGE_SEND_ERROR;

@Component
@RequiredArgsConstructor
public class ProductOrderEventHandler {
    private final MessageSender messageSender;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void handleProductStockRollBackEvent(OrderReplyEvent event) {
        if (!messageSender.sendOrderReplyEvent(event))
            throw new RuntimeException(MESSAGE_SEND_ERROR.message);
    }
}
