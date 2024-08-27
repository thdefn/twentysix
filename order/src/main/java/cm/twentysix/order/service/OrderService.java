package cm.twentysix.order.service;

import cm.twentysix.BrandProto.BrandInfo;
import cm.twentysix.ProductProto.ProductItemResponse;
import cm.twentysix.order.cache.global.ReservedProductStockGlobalCacheRepository;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static cm.twentysix.order.exception.Error.ORDER_CONTAIN_CLOSING_PRODUCT;
import static cm.twentysix.order.exception.Error.STOCK_SHORTAGE;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final BrandGrpcClient brandGrpcClient;
    private final ProductGrpcClient productGrpcClient;
    private final CartService cartService;
    private final MessageSender messageSender;
    private final ReservedProductStockGlobalCacheRepository reservedProductStockGlobalCacheRepository;

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
                            Map<String, Integer> maybeFetchedQuantityMap = new HashMap<>();
                            for (ProductItemResponse product : products) {
                                validProductIsOpen(product.getOrderingOpensAt(), requestedAt);
                                maybeFetchedQuantityMap.put(product.getId(), product.getQuantity());
                            }
                            checkProductStockAndUpdate(productIdQuantityMap, maybeFetchedQuantityMap);
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
        } catch (Exception e) {
            Throwable cause = e.getCause();
            if (cause instanceof OrderException) {
                throw (OrderException) cause;
            } else throw new RuntimeException(e);
        }
    }

    private void checkProductStockAndUpdate(Map<String, Integer> productIdRequestedQuantityMap, Map<String, Integer> maybeFetchedQuantityMap) {
        Map<String, Integer> productIdCachedQuantityMap =
                reservedProductStockGlobalCacheRepository.getOrFetchIfAbsent(
                        productIdRequestedQuantityMap.keySet().stream().toList(), maybeFetchedQuantityMap);

        for (String productId : productIdCachedQuantityMap.keySet()) {
            int havingQuantity = productIdCachedQuantityMap.get(productId);
            int requestedQuantity = productIdRequestedQuantityMap.get(productId);
            if (requestedQuantity > havingQuantity)
                throw new OrderException(STOCK_SHORTAGE);
            productIdCachedQuantityMap.put(productId, havingQuantity - requestedQuantity);
        }
        reservedProductStockGlobalCacheRepository.putAll(productIdCachedQuantityMap);
    }

    private void validProductIsOpen(String orderingOpensAt, LocalDateTime requestedAt) {
        if (LocalDateTime.parse(orderingOpensAt).isAfter(requestedAt))
            throw new OrderException(ORDER_CONTAIN_CLOSING_PRODUCT);
    }

    @Transactional
    public void handleStockCheckFailedEvent(StockCheckFailedEvent event) {
        log.error(event.orderId());
        Order order = orderRepository.findByOrderId(event.orderId())
                .stream().findFirst()
                .filter(o -> OrderStatus.PAYMENT_PENDING.equals(o.getStatus()))
                .orElseThrow(() -> new OrderException(Error.PROCESSING_ORDER_NOT_FOUND));

        order.checkFail();
    }

    @Transactional
    public void handlePaymentFinalizedEvent(PaymentFinalizedEvent event) {
        Order order = orderRepository.findByOrderId(event.orderId())
                .stream().findFirst()
                .filter(o -> OrderStatus.PAYMENT_PENDING.equals(o.getStatus()))
                .orElseThrow(() -> new OrderException(Error.PROCESSING_ORDER_NOT_FOUND));

        if (event.isSuccess()) order.placed();
        else order.paymentFail();
    }


    @Transactional
    public void cancelOrder(String orderId, Long userId) {
        Order order = orderRepository.findByOrderId(orderId)
                .stream().findFirst()
                .filter(o -> o.getStatus().isPreparationStatus())
                .orElseThrow(() -> new OrderException(Error.ORDER_IN_PREPARATION_NOT_FOUND));

        if (!order.getUserId().equals(userId))
            throw new OrderException(Error.NOT_USERS_ORDER);

        order.cancel();
        messageSender.sendOrderCancelledEvent(OrderCancelledEvent.from(order));
    }
}
