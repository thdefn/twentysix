package cm.twentysix.order.service;

import cm.twentysix.order.domain.model.Order;
import cm.twentysix.order.domain.model.OrderStatus;
import cm.twentysix.order.domain.repository.OrderRepository;
import cm.twentysix.order.dto.CreateOrderForm;
import cm.twentysix.order.dto.OrderEvent;
import cm.twentysix.order.dto.OrderReplyEvent;
import cm.twentysix.order.exception.Error;
import cm.twentysix.order.exception.OrderException;
import cm.twentysix.order.util.IdUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void receiveOrder(CreateOrderForm form, Long userId) {
        String orderId = IdUtil.generate();
        // TODO : 배송지 정보 업데이트 요청
        orderRepository.save(Order.of(orderId, userId, form));

        eventPublisher.publishEvent(OrderEvent.of(form.products(), orderId));
    }

    @Transactional
    public void approveOrDenyOrder(OrderReplyEvent event) {
        // TODO : approve 받았는데 리소스가 없을때 재고 관리
        Order order = orderRepository.findByOrderId(event.orderId())
                .stream().findFirst()
                .filter(o -> OrderStatus.PENDING.equals(o.getStatus()))
                .orElseThrow(() -> new OrderException(Error.ORDER_NOT_FOUND));
        if (event.isSuccess()) {
            // TODO: brand delivery fee 관련 정보
            order.approve(event.orderedItem());
            // TODO: 결제 서버 관련 처리
        } else {
            orderRepository.delete(order);
        }
    }


}
