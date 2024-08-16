package cm.twentysix.order.service;

import cm.twentysix.BrandProto.BrandInfo;
import cm.twentysix.order.client.BrandGrpcClient;
import cm.twentysix.order.domain.model.Order;
import cm.twentysix.order.domain.model.OrderStatus;
import cm.twentysix.order.domain.repository.OrderRepository;
import cm.twentysix.order.dto.CreateOrderForm;
import cm.twentysix.order.dto.OrderEvent;
import cm.twentysix.order.dto.OrderReplyEvent;
import cm.twentysix.order.dto.ProductOrderItem;
import cm.twentysix.order.exception.Error;
import cm.twentysix.order.exception.OrderException;
import cm.twentysix.order.util.IdUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final BrandGrpcClient brandGrpcClient;

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
                .filter(o -> OrderStatus.CHECK_PENDING.equals(o.getStatus()))
                .orElseThrow(() -> new OrderException(Error.ORDER_NOT_FOUND));
        if (event.isSuccess()) {
            List<Long> brandIds = event.orderedItem().values()
                    .stream().map(ProductOrderItem::brandId).toList();
            Map<Long, BrandInfo> containedBrandInfos = brandGrpcClient.findBrandInfo(brandIds);
            order.approve(event.orderedItem(), containedBrandInfos);
            // TODO: 결제 서버 관련 처리
        } else {
            orderRepository.delete(order);
        }
    }


}
