package cm.twentysix.order.service;

import cm.twentysix.BrandProto.BrandInfo;
import cm.twentysix.ProductProto.ProductItemResponse;
import cm.twentysix.order.cache.global.ReservedProductStockGlobalCacheRepository;
import cm.twentysix.order.client.BrandGrpcClient;
import cm.twentysix.order.client.ProductGrpcClient;
import cm.twentysix.order.constant.CircuitBreakerDomain;
import cm.twentysix.order.domain.model.Order;
import cm.twentysix.order.domain.model.OrderProduct;
import cm.twentysix.order.domain.model.OrderStatus;
import cm.twentysix.order.domain.repository.OrderRepository;
import cm.twentysix.order.dto.*;
import cm.twentysix.order.exception.Error;
import cm.twentysix.order.exception.OrderException;
import cm.twentysix.order.messaging.MessageSender;
import cm.twentysix.order.util.IdUtil;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
    private final CircuitBreakerService circuitBreakerService;

    @Transactional
    public ReceiveOrderResponse receiveOrder(CreateOrderForm form, Long userId, LocalDateTime requestedAt) {
        circuitBreakerService.validateServiceAvailability(CircuitBreakerDomain.ORDER, form.getProductIds());

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
                            for (ProductItemResponse product : products)
                                validProductIsAvailableWithCircuitBreaker(product, productIdQuantityMap.get(product.getId()), requestedAt);
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
            CompletableFuture
                    .runAsync(() -> reservedProductStockGlobalCacheRepository.decrementStocks(productIdQuantityMap))
                    .thenRunAsync(() -> cartService.removeOrderedCartItem(form, userId));
            return ReceiveOrderResponse.of(orderId);
        } catch (Exception e) {
            Throwable cause = e.getCause();
            if (cause instanceof OrderException) {
                throw (OrderException) cause;
            } else throw new RuntimeException(e);
        }
    }
    private void validProductIsAvailableWithCircuitBreaker(ProductItemResponse product, Integer quantity, LocalDateTime requestedAt) {
        CircuitBreaker circuitBreaker = circuitBreakerService.getOrCreateCircuitBreaker(CircuitBreakerDomain.ORDER, product.getId());
        try {
            CircuitBreaker.decorateCheckedRunnable(circuitBreaker, () -> {
                validProductIsAvailable(product, quantity, requestedAt);
            }).run();
        } catch (Throwable e) {
            if (e instanceof OrderException)
                throw (OrderException) e;
            throw new RuntimeException(e);
        }
    }

    private void validProductIsAvailable(ProductItemResponse productItem, Integer requestedQuantity, LocalDateTime requestedAt) {
        validProductIsOpen(productItem.getOrderingOpensAt(), requestedAt);
        Integer havingQuantity = reservedProductStockGlobalCacheRepository.getOrFetchIfAbsent(productItem.getId(), productItem.getQuantity());
        if (requestedQuantity > havingQuantity)
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
        else {
            order.paymentFail();
            restoreReservedStockIfPresent(order.getProductIdQuantityMap());
        }
    }

    private void restoreReservedStockIfPresent(Map<String, Integer> productIdRestoredQuantityMap) {
        Map<String, Integer> productIdCachedQuantityMap =
                reservedProductStockGlobalCacheRepository.getAll(productIdRestoredQuantityMap.keySet().stream().toList());
        if (productIdCachedQuantityMap.isEmpty())
            return;
        Map<String, Integer> productIdIncrementQuantityMap = new HashMap<>();
        for (String productId : productIdCachedQuantityMap.keySet()) {
            int incrementQuantity = productIdRestoredQuantityMap.get(productId);
            productIdIncrementQuantityMap.put(productId, incrementQuantity);
        }
        reservedProductStockGlobalCacheRepository.incrementStocks(productIdIncrementQuantityMap);
    }


    @Transactional
    public void cancelOrder(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .stream().findFirst()
                .filter(o -> o.getStatus().isPreparationStatus())
                .orElseThrow(() -> new OrderException(Error.ORDER_IN_PREPARATION_NOT_FOUND));

        if (!order.getUserId().equals(userId))
            throw new OrderException(Error.NOT_USERS_ORDER);

        order.cancel();
        restoreReservedStockIfPresent(order.getProductIdQuantityMap());
        messageSender.sendOrderCancelledEvent(OrderCancelledEvent.from(order));
    }

    @Transactional
    public void returnOrder(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .stream().findFirst()
                .filter(Order::isReturnAllowed)
                .orElseThrow(() -> new OrderException(Error.ORDER_RETURN_NOT_ALLOWED));

        if (!order.getUserId().equals(userId))
            throw new OrderException(Error.NOT_USERS_ORDER);

        order.acceptReturn();
        restoreReservedStockIfPresent(order.getProductIdQuantityMap());
    }

    public Slice<OrderItem> retrieveMyOrder(int page, int size, Long userId) {
        return orderRepository.findByUserIdOrderByIdDesc(userId, PageRequest.of(page, size))
                .map(order -> OrderItem.from(order, getOrderBrandItems(order.getProducts(), order.getDeliveryFees())));
    }

    private List<OrderBrandItem> getOrderBrandItems(Map<String, OrderProduct> products, Map<Long, Integer> brands) {
        Map<Long, OrderBrandItem> orderBrandItemMap = new LinkedHashMap<>();
        for (String productId : products.keySet()) {
            OrderProduct orderProduct = products.get(productId);
            Long brandId = orderProduct.getBrandId();
            if (!orderBrandItemMap.containsKey(brandId))
                orderBrandItemMap.put(brandId, OrderBrandItem.of(brandId, brands.get(brandId)));
            OrderBrandItem orderBrandItem = orderBrandItemMap.get(brandId);
            orderBrandItem.addProduct(productId, orderProduct);
        }
        return orderBrandItemMap.values().stream().toList();
    }


}
