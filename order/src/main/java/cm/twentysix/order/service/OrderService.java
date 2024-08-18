package cm.twentysix.order.service;

import cm.twentysix.BrandProto.BrandInfo;
import cm.twentysix.ProductProto.ProductItemResponse;
import cm.twentysix.order.client.BrandGrpcClient;
import cm.twentysix.order.client.ProductGrpcClient;
import cm.twentysix.order.domain.model.Order;
import cm.twentysix.order.domain.model.OrderStatus;
import cm.twentysix.order.domain.repository.OrderRepository;
import cm.twentysix.order.dto.*;
import cm.twentysix.order.exception.Error;
import cm.twentysix.order.exception.OrderException;
import cm.twentysix.order.messaging.MessageSender;
import cm.twentysix.order.util.IdUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final BrandGrpcClient brandGrpcClient;
    private final ProductGrpcClient productGrpcClient;
    private final CartService cartService;
    private final MessageSender messageSender;

    @Transactional
    public ReceiveOrderResponse receiveOrder(CreateOrderForm form, Long userId) {
        String orderId = IdUtil.generate();
        CompletableFuture.runAsync(() -> {
            if (form.shouldSaveNewAddress()) {
                messageSender.sendAddressSaveEvent(
                        AddressSaveEvent.from(form.receiver(), userId));
            }
        });

        Map<String, Integer> productIdQuantityMap = form.products().stream()
                .collect(Collectors.toMap(OrderProductItemForm::id, OrderProductItemForm::quantity));

        CompletableFuture<List<ProductItemResponse>> productFuture =
                CompletableFuture.supplyAsync(() ->
                        productGrpcClient.findProductItems(productIdQuantityMap.keySet().stream().toList()));


        CompletableFuture<Map<Long, BrandInfo>> brandInfoFuture = productFuture.thenCompose(products -> {
            List<Long> brandIds = products.stream().map(ProductItemResponse::getBrandId).collect(Collectors.toList());
            return CompletableFuture.supplyAsync(() -> brandGrpcClient.findBrandInfo(brandIds));
        });


        CompletableFuture<Order> orderFuture = productFuture.thenCombine(brandInfoFuture, (products, brandInfo) -> {
            Order order = Order.of(orderId, products, productIdQuantityMap, form.receiver(), userId);
            order.settlePayment(brandInfo);
            return order;
        });

        try {
            Order order = orderFuture.get();
            orderRepository.save(order);
            eventPublisher.publishEvent(OrderEvent.of(order));
            return ReceiveOrderResponse.of(orderId);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public void handleProductOrderFailedEvent(ProductOrderFailedEvent event) {
        Order order = orderRepository.findByOrderId(event.orderId())
                .stream().findFirst()
                .filter(o -> OrderStatus.PAYMENT_PENDING.equals(o.getStatus()))
                .orElseThrow(() -> new OrderException(Error.ORDER_NOT_FOUND));


        order.changeStatus(OrderStatus.CHECK_FAIL);
    }


}
