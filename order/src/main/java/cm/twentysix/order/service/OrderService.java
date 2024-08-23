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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static cm.twentysix.order.exception.Error.ORDER_CONTAIN_CLOSING_PRODUCT;
import static cm.twentysix.order.exception.Error.STOCK_SHORTAGE;

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
    public ReceiveOrderResponse receiveOrder(CreateOrderForm form, Long userId, LocalDateTime requestedAt) {
        String orderId = IdUtil.generate();
        CompletableFuture.runAsync(() -> {
            if (form.shouldSaveNewAddress())
                messageSender.sendAddressSaveEvent(AddressSaveEvent.from(form.receiver(), userId));
        });

        Map<String, Integer> productIdQuantityMap = form.products().stream()
                .collect(Collectors.toMap(OrderProductItemForm::id, OrderProductItemForm::quantity));

        CompletableFuture<List<ProductItemResponse>> productFuture =
                CompletableFuture.supplyAsync(() -> productGrpcClient.findProductItems(productIdQuantityMap.keySet().stream().toList()))
                        .thenApply(products -> {
                            for (ProductItemResponse product : products) {
                                validAvailableProductQuantity(product.getQuantity(), productIdQuantityMap.get(product.getId()));
                                validProductIsOpen(product.getOrderingOpensAt(), requestedAt);
                            }
                            return products;
                        });

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
            CompletableFuture.runAsync(() -> cartService.removeOrderedCartItem(form, userId));
            return ReceiveOrderResponse.of(orderId);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof OrderException) {
                throw (OrderException) cause;
            } else throw new RuntimeException(e);
        }
    }

    private void validAvailableProductQuantity(int obtainedQuantity, int requestedQuantity) {
        if (requestedQuantity > obtainedQuantity)
            throw new OrderException(STOCK_SHORTAGE);
    }

    private void validProductIsOpen(String orderingOpensAt, LocalDateTime requestedAt) {
        if (LocalDateTime.parse(orderingOpensAt).isAfter(requestedAt))
            throw new OrderException(ORDER_CONTAIN_CLOSING_PRODUCT);
    }

    @Transactional
    public void handleStockCheckFailedEvent(StockCheckFailedEvent event) {
        Order order = orderRepository.findByOrderId(event.orderId())
                .stream().findFirst()
                .filter(o -> o.getStatus().isOrderProcessingStatus())
                .orElseThrow(() -> new OrderException(Error.PROCESSING_ORDER_NOT_FOUND));

        order.checkFail();
    }

    @Transactional
    public void handlePaymentFinalizedEvent(PaymentFinalizedEvent event) {
        Order order = orderRepository.findByOrderId(event.orderId())
                .stream().findFirst()
                .filter(o -> OrderStatus.PAYMENT_PENDING.equals(o.getStatus()))
                .orElseThrow(() -> new OrderException(Error.PROCESSING_ORDER_NOT_FOUND));

        if (event.isSuccess()) {
            order.placed();
            eventPublisher.publishEvent(OrderEvent.of(order));
        } else {
            order.paymentFail();
        }
    }


    @Transactional
    public void cancelOrder(String orderId, Long userId) {
        Order order = orderRepository.findByOrderId(orderId)
                .stream().findFirst()
                .filter(o -> o.getStatus().isOrderProcessingStatus())
                .orElseThrow(() -> new OrderException(Error.PROCESSING_ORDER_NOT_FOUND));

        if (!order.getUserId().equals(userId))
            throw new OrderException(Error.NOT_USERS_ORDER);

        order.cancel();
        messageSender.sendOrderCancelledEvent(OrderCancelledEvent.from(order));
    }
}
