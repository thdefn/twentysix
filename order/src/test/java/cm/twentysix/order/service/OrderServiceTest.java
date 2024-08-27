package cm.twentysix.order.service;

import cm.twentysix.BrandProto;
import cm.twentysix.ProductProto;
import cm.twentysix.order.cache.global.ReservedProductStockGlobalCacheRepository;
import cm.twentysix.order.client.BrandGrpcClient;
import cm.twentysix.order.client.ProductGrpcClient;
import cm.twentysix.order.domain.model.Order;
import cm.twentysix.order.domain.model.OrderProduct;
import cm.twentysix.order.domain.model.OrderReceiver;
import cm.twentysix.order.domain.model.OrderStatus;
import cm.twentysix.order.domain.repository.OrderRepository;
import cm.twentysix.order.dto.*;
import cm.twentysix.order.exception.Error;
import cm.twentysix.order.exception.OrderException;
import cm.twentysix.order.messaging.MessageSender;
import cm.twentysix.order.util.IdUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ApplicationEventPublisher applicationEventPublisher;
    @Mock
    private BrandGrpcClient brandGrpcClient;
    @Mock
    private ProductGrpcClient productGrpcClient;
    @Mock
    private CartService cartService;
    @Mock
    private MessageSender messageSender;
    @Mock
    private ReservedProductStockGlobalCacheRepository reservedProductStockGlobalCacheRepository;
    @InjectMocks
    private OrderService orderService;

    static MockedStatic<IdUtil> mockIdUtil;

    private LocalDateTime now = LocalDateTime.now();

    private Map<String, Integer> reservedProductStockCacheData = new HashMap<>();

    private Order mockOrderA;

    @BeforeEach
    void init() {
        mockIdUtil = mockStatic(IdUtil.class);
        when(IdUtil.generate()).thenReturn("2032032003030-afsfdasfdsfdsafdl2");
        reservedProductStockCacheData.put("123456", 1);
        mockOrderA = mock(Order.class);
    }

    @AfterEach
    void tearDown() {
        mockIdUtil.close();
    }

    @Test
    void receiveOrder_success() {
        //given
        List<OrderProductItemForm> items = List.of(
                new OrderProductItemForm("123456", 1)
        );
        CreateOrderForm.ReceiverForm receiver = new CreateOrderForm.ReceiverForm(true, "송송이", "서울특별시 성북구 보문로", "11112", "010-2222-2222");
        CreateOrderForm form = new CreateOrderForm(items, true, true, receiver);
        given(productGrpcClient.findProductItems(anyList())).willReturn(
                List.of(
                        ProductProto.ProductItemResponse.newBuilder()
                                .setId("123456")
                                .setDiscountedPrice(27000)
                                .setDiscount(10)
                                .setThumbnail("so-cute.jpg")
                                .setPrice(30000)
                                .setName("모달 잠옷 여성")
                                .setBrandName("JAJU")
                                .setQuantity(1)
                                .setBrandId(1L)
                                .setOrderingOpensAt(LocalDateTime.MIN.toString())
                                .build()
                )
        );
        given(brandGrpcClient.findBrandInfo(anyList())).willReturn(Map.of(1L, BrandProto.BrandInfo.newBuilder()
                .setName("JAJU").setDeliveryFee(3000).setId(1L).setFreeDeliveryInfimum(500000).build()));
        given(reservedProductStockGlobalCacheRepository.getOrFetchIfAbsent(anyList(), anyMap())).willReturn(reservedProductStockCacheData);
        //when
        ReceiveOrderResponse response = orderService.receiveOrder(form, 1L, now);
        //then
        ArgumentCaptor<AddressSaveEvent> addressSaveEventCaptor = ArgumentCaptor.forClass(AddressSaveEvent.class);
        verify(messageSender, times(1)).sendAddressSaveEvent(addressSaveEventCaptor.capture());
        AddressSaveEvent event = addressSaveEventCaptor.getValue();
        assertTrue(event.isDefault());
        assertEquals(event.name(), receiver.name());
        assertEquals(event.address(), receiver.address());
        assertEquals(event.phone(), receiver.phone());
        assertEquals(event.userId(), 1L);
        assertEquals(event.zipCode(), receiver.zipCode());

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository, times(1)).save(orderCaptor.capture());
        Order saved = orderCaptor.getValue();
        assertEquals(saved.getOrderId(), "2032032003030-afsfdasfdsfdsafdl2");
        assertEquals(saved.getReceiver().getName(), receiver.name());
        assertEquals(saved.getReceiver().getPhone(), receiver.phone());
        assertEquals(saved.getReceiver().getZipCode(), receiver.zipCode());
        assertEquals(saved.getReceiver().getAddress(), receiver.address());

        OrderProduct savedItem = saved.getProducts().get("123456");
        assertEquals(savedItem.getAmount(), 27000);
        assertEquals(savedItem.getQuantity(), 1);
        assertEquals(savedItem.getBrandId(), 1L);
        assertEquals(savedItem.getBrandName(), "JAJU");
        assertEquals(savedItem.getThumbnail(), "so-cute.jpg");
        assertEquals(savedItem.getName(), "모달 잠옷 여성");
        assertEquals(saved.getProducts().get("123456").getBrandId(), 1L);

        assertEquals(saved.getUserId(), 1L);
        assertEquals(saved.getStatus(), OrderStatus.PAYMENT_PENDING);
        assertEquals(saved.getTotalAmount(), 27000);
        assertEquals(saved.getTotalDeliveryFee(), 3000);
        assertEquals(saved.getPaymentAmount(), 30000);
        assertEquals(response.orderId(), "2032032003030-afsfdasfdsfdsafdl2");
    }

    @Test
    void receiveOrder_success_whenShouldSaveNewAddressIsFalse() {
        //given
        List<OrderProductItemForm> items = List.of(
                new OrderProductItemForm("123456", 1)
        );
        CreateOrderForm.ReceiverForm receiver = new CreateOrderForm.ReceiverForm(true, "송송이", "서울특별시 성북구 보문로", "11112", "010-2222-2222");
        CreateOrderForm form = new CreateOrderForm(items, false, true, receiver);
        given(productGrpcClient.findProductItems(anyList())).willReturn(
                List.of(
                        ProductProto.ProductItemResponse.newBuilder()
                                .setId("123456")
                                .setDiscountedPrice(27000)
                                .setDiscount(10)
                                .setThumbnail("so-cute.jpg")
                                .setPrice(30000)
                                .setName("모달 잠옷 여성")
                                .setBrandName("JAJU")
                                .setQuantity(1)
                                .setOrderingOpensAt(LocalDateTime.MIN.toString())
                                .setBrandId(1L)
                                .build()
                )
        );
        given(brandGrpcClient.findBrandInfo(anyList())).willReturn(Map.of(1L, BrandProto.BrandInfo.newBuilder()
                .setName("JAJU").setDeliveryFee(3000).setId(1L).setFreeDeliveryInfimum(500000).build()));
        given(reservedProductStockGlobalCacheRepository.getOrFetchIfAbsent(anyList(), anyMap())).willReturn(reservedProductStockCacheData);
        //when
        ReceiveOrderResponse response = orderService.receiveOrder(form, 1L, now);
        //then
        verify(messageSender, times(0)).sendAddressSaveEvent(any());

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository, times(1)).save(orderCaptor.capture());
        Order saved = orderCaptor.getValue();
        assertEquals(saved.getOrderId(), "2032032003030-afsfdasfdsfdsafdl2");
        assertEquals(saved.getReceiver().getName(), receiver.name());
        assertEquals(saved.getReceiver().getPhone(), receiver.phone());
        assertEquals(saved.getReceiver().getZipCode(), receiver.zipCode());
        assertEquals(saved.getReceiver().getAddress(), receiver.address());
        assertEquals(saved.getUserId(), 1L);
        assertEquals(saved.getStatus(), OrderStatus.PAYMENT_PENDING);
        assertEquals(response.orderId(), "2032032003030-afsfdasfdsfdsafdl2");
    }

    @Test
    void receiveOrder_fail_STOCK_SHORTAGE() {
        //given
        List<OrderProductItemForm> items = List.of(
                new OrderProductItemForm("123456", 2)
        );
        CreateOrderForm.ReceiverForm receiver = new CreateOrderForm.ReceiverForm(true, "송송이", "서울특별시 성북구 보문로", "11112", "010-2222-2222");
        CreateOrderForm form = new CreateOrderForm(items, true, true, receiver);
        given(productGrpcClient.findProductItems(anyList())).willReturn(
                List.of(
                        ProductProto.ProductItemResponse.newBuilder()
                                .setId("123456")
                                .setDiscountedPrice(27000)
                                .setDiscount(10)
                                .setThumbnail("so-cute.jpg")
                                .setPrice(30000)
                                .setName("모달 잠옷 여성")
                                .setQuantity(2)
                                .setBrandName("JAJU")
                                .setBrandId(1L)
                                .setOrderingOpensAt(LocalDateTime.MIN.toString())
                                .build()
                )
        );
        given(reservedProductStockGlobalCacheRepository.getOrFetchIfAbsent(anyList(), anyMap())).willReturn(reservedProductStockCacheData);
        //when
        OrderException e = assertThrows(OrderException.class, () -> orderService.receiveOrder(form, 1L, now));
        //then
        assertEquals(e.getError(), Error.STOCK_SHORTAGE);
    }

    @Test
    void receiveOrder_fail_ORDER_CONTAIN_CLOSING_PRODUCT() {
        //given
        List<OrderProductItemForm> items = List.of(
                new OrderProductItemForm("123456", 1)
        );
        CreateOrderForm.ReceiverForm receiver = new CreateOrderForm.ReceiverForm(true, "송송이", "서울특별시 성북구 보문로", "11112", "010-2222-2222");
        CreateOrderForm form = new CreateOrderForm(items, true, true, receiver);
        given(productGrpcClient.findProductItems(anyList())).willReturn(
                List.of(
                        ProductProto.ProductItemResponse.newBuilder()
                                .setId("123456")
                                .setDiscountedPrice(27000)
                                .setDiscount(10)
                                .setThumbnail("so-cute.jpg")
                                .setPrice(30000)
                                .setName("모달 잠옷 여성")
                                .setQuantity(1)
                                .setBrandName("JAJU")
                                .setBrandId(1L)
                                .setOrderingOpensAt(LocalDateTime.MAX.toString())
                                .build()
                )
        );
        //when
        OrderException e = assertThrows(OrderException.class, () -> orderService.receiveOrder(form, 1L, now));
        //then
        assertEquals(e.getError(), Error.ORDER_CONTAIN_CLOSING_PRODUCT);
    }

    @Test
    void handleStockCheckFailedEvent_success() {
        //given
        StockCheckFailedEvent event = new StockCheckFailedEvent("2032032003030-afsfdasfdsfdsafdl2");
        Order order = Order.builder()
                .orderId("2032032003030-afsfdasfdsfdsafdl2")
                .userId(1L)
                .products(new HashMap<>())
                .deliveryFees(new HashMap<>())
                .receiver(OrderReceiver.builder()
                        .name("송송이")
                        .address("서울 특별시 성북구 보문로")
                        .zipCode("11112")
                        .phone("010-1111-1111")
                        .build())
                .status(OrderStatus.PAYMENT_PENDING)
                .build();
        given(orderRepository.findByOrderId(anyString())).willReturn(Optional.of(order));
        //when
        orderService.handleStockCheckFailedEvent(event);
        //then
        assertEquals(order.getStatus(), OrderStatus.CHECK_FAIL);
    }

    @Test
    void handleStockCheckFailedEvent_fail_PROCESSING_ORDER_NOT_FOUND() {
        //given
        StockCheckFailedEvent event = new StockCheckFailedEvent("2032032003030-afsfdasfdsfdsafdl2");
        Order order = Order.builder()
                .orderId("2032032003030-afsfdasfdsfdsafdl2")
                .userId(1L)
                .products(new HashMap<>())
                .deliveryFees(new HashMap<>())
                .receiver(OrderReceiver.builder()
                        .name("송송이")
                        .address("서울 특별시 성북구 보문로")
                        .zipCode("11112")
                        .phone("010-1111-1111")
                        .build())
                .status(OrderStatus.PAYMENT_FAIL)
                .build();
        given(orderRepository.findByOrderId(anyString())).willReturn(Optional.of(order));
        //when
        OrderException e = assertThrows(OrderException.class, () -> orderService.handleStockCheckFailedEvent(event));
        //then
        assertEquals(e.getError(), Error.PROCESSING_ORDER_NOT_FOUND);
    }


    @Test
    void handlePaymentFinalizedEvent_success() {
        //given
        PaymentFinalizedEvent event = new PaymentFinalizedEvent("2032032003030-afsfdasfdsfdsafdl2", true);
        Order order = Order.builder()
                .orderId("2032032003030-afsfdasfdsfdsafdl2")
                .userId(1L)
                .products(Map.of(
                        "1234", OrderProduct.builder().quantity(2).build(),
                        "2345", OrderProduct.builder().quantity(1).build(),
                        "3456", OrderProduct.builder().quantity(1).build()
                ))
                .deliveryFees(new HashMap<>())
                .receiver(OrderReceiver.builder()
                        .name("송송이")
                        .address("서울 특별시 성북구 보문로")
                        .zipCode("11112")
                        .phone("010-1111-1111")
                        .build())
                .status(OrderStatus.PAYMENT_PENDING)
                .build();
        given(orderRepository.findByOrderId(anyString())).willReturn(Optional.of(order));
        //when
        orderService.handlePaymentFinalizedEvent(event);
        //then
        assertEquals(order.getStatus(), OrderStatus.ORDER_PLACED);
    }

    @Test
    void handlePaymentFinalizedEvent_success_WhenPaymentIsFail() {
        //given
        PaymentFinalizedEvent event = new PaymentFinalizedEvent("2032032003030-afsfdasfdsfdsafdl2", false);
        Order order = Order.builder()
                .orderId("2032032003030-afsfdasfdsfdsafdl2")
                .userId(1L)
                .products(new HashMap<>())
                .deliveryFees(new HashMap<>())
                .receiver(OrderReceiver.builder()
                        .name("송송이")
                        .address("서울 특별시 성북구 보문로")
                        .zipCode("11112")
                        .phone("010-1111-1111")
                        .build())
                .status(OrderStatus.PAYMENT_PENDING)
                .build();
        given(orderRepository.findByOrderId(anyString())).willReturn(Optional.of(order));
        //when
        orderService.handlePaymentFinalizedEvent(event);
        //then
        assertEquals(order.getStatus(), OrderStatus.PAYMENT_FAIL);
    }

    @Test
    void handlePaymentFinalizedEvent_fail_PROCESSING_ORDER_NOT_FOUND() {
        //given
        PaymentFinalizedEvent event = new PaymentFinalizedEvent("2032032003030-afsfdasfdsfdsafdl2", true);
        Order order = Order.builder()
                .orderId("2032032003030-afsfdasfdsfdsafdl2")
                .userId(1L)
                .products(new HashMap<>())
                .deliveryFees(new HashMap<>())
                .receiver(OrderReceiver.builder()
                        .name("송송이")
                        .address("서울 특별시 성북구 보문로")
                        .zipCode("11112")
                        .phone("010-1111-1111")
                        .build())
                .status(OrderStatus.CHECK_FAIL)
                .build();
        given(orderRepository.findByOrderId(anyString())).willReturn(Optional.of(order));
        //when
        OrderException e = assertThrows(OrderException.class, () -> orderService.handlePaymentFinalizedEvent(event));
        //then
        assertEquals(e.getError(), Error.PROCESSING_ORDER_NOT_FOUND);
    }

    @Test
    void cancelOrder_success() {
        //given
        Order order = Order.builder()
                .orderId("2032032003030-afsfdasfdsfdsafdl2")
                .userId(1L)
                .products(Map.of(
                        "1234", OrderProduct.builder().quantity(2).build(),
                        "2345", OrderProduct.builder().quantity(1).build(),
                        "3456", OrderProduct.builder().quantity(1).build()
                ))
                .deliveryFees(new HashMap<>())
                .receiver(OrderReceiver.builder()
                        .name("송송이")
                        .address("서울 특별시 성북구 보문로")
                        .zipCode("11112")
                        .phone("010-1111-1111")
                        .build())
                .status(OrderStatus.ORDER_PLACED)
                .build();
        given(orderRepository.findById(anyLong())).willReturn(Optional.of(order));
        //when
        orderService.cancelOrder(1L, 1L);
        //then
        ArgumentCaptor<OrderCancelledEvent> orderCancelledEventCaptor = ArgumentCaptor.forClass(OrderCancelledEvent.class);
        verify(messageSender, times(1)).sendOrderCancelledEvent(orderCancelledEventCaptor.capture());
        OrderCancelledEvent orderCancelledEvent = orderCancelledEventCaptor.getValue();
        Map<String, Integer> productQuantityMap = orderCancelledEvent.productQuantity();
        assertEquals(productQuantityMap.get("1234"), 2);
        assertEquals(productQuantityMap.get("2345"), 1);
        assertEquals(productQuantityMap.get("3456"), 1);
        assertEquals(order.getStatus(), OrderStatus.CANCEL);
    }

    @Test
    void cancelOrder_fail_ORDER_IN_PREPARATION_NOT_FOUND() {
        //given
        Order order = Order.builder()
                .orderId("2032032003030-afsfdasfdsfdsafdl2")
                .userId(1L)
                .products(Map.of(
                        "1234", OrderProduct.builder().quantity(2).build(),
                        "2345", OrderProduct.builder().quantity(1).build(),
                        "3456", OrderProduct.builder().quantity(1).build()
                ))
                .deliveryFees(new HashMap<>())
                .receiver(OrderReceiver.builder()
                        .name("송송이")
                        .address("서울 특별시 성북구 보문로")
                        .zipCode("11112")
                        .phone("010-1111-1111")
                        .build())
                .status(OrderStatus.PAYMENT_FAIL)
                .build();
        given(orderRepository.findById(anyLong())).willReturn(Optional.of(order));
        //when
        OrderException e = assertThrows(OrderException.class, () -> orderService.cancelOrder(1L, 1L));
        //then
        assertEquals(e.getError(), Error.ORDER_IN_PREPARATION_NOT_FOUND);
    }

    @Test
    void cancelOrder_fail_NOT_USERS_ORDER() {
        //given
        Order order = Order.builder()
                .orderId("2032032003030-afsfdasfdsfdsafdl2")
                .userId(2L)
                .products(Map.of(
                        "1234", OrderProduct.builder().quantity(2).build(),
                        "2345", OrderProduct.builder().quantity(1).build(),
                        "3456", OrderProduct.builder().quantity(1).build()
                ))
                .deliveryFees(new HashMap<>())
                .receiver(OrderReceiver.builder()
                        .name("송송이")
                        .address("서울 특별시 성북구 보문로")
                        .zipCode("11112")
                        .phone("010-1111-1111")
                        .build())
                .status(OrderStatus.ORDER_PLACED)
                .build();
        given(orderRepository.findById(anyLong())).willReturn(Optional.of(order));
        //when
        OrderException e = assertThrows(OrderException.class, () -> orderService.cancelOrder(1L, 1L));
        //then
        assertEquals(e.getError(), Error.NOT_USERS_ORDER);
    }

    @Test
    void returnOrder_success() {
        //given
        given(mockOrderA.getUserId()).willReturn(1L);
        given(mockOrderA.isReturnAllowed()).willReturn(true);
        given(orderRepository.findById(anyLong())).willReturn(Optional.of(mockOrderA));
        //when
        orderService.returnOrder(1L, 1L);
        //then
        verify(mockOrderA, times(1)).acceptReturn();
    }

    @Test
    void returnOrder_fail_ORDER_RETURN_NOT_ALLOWED() {
        //given
        given(mockOrderA.isReturnAllowed()).willReturn(false);
        given(orderRepository.findById(anyLong())).willReturn(Optional.of(mockOrderA));
        //when
        OrderException e = assertThrows(OrderException.class, () -> orderService.returnOrder(1L, 1L));
        //then
        assertEquals(e.getError(), Error.ORDER_RETURN_NOT_ALLOWED);
    }


    @Test
    void returnOrder_fail_NOT_USERS_ORDER() {
        //given
        given(mockOrderA.getUserId()).willReturn(2L);
        given(mockOrderA.isReturnAllowed()).willReturn(true);
        given(orderRepository.findById(anyLong())).willReturn(Optional.of(mockOrderA));
        //when
        OrderException e = assertThrows(OrderException.class, () -> orderService.returnOrder(1L, 1L));
        //then
        assertEquals(e.getError(), Error.NOT_USERS_ORDER);
    }

    @Test
    void retrieveMyOrder_success() {
        //given
        List<Order> orders = List.of(
                Order.builder()
                        .orderId("2032032003030-afsfdasfdsfdsafdl2")
                        .userId(2L)
                        .products(Map.of(
                                "1234", OrderProduct.builder()
                                        .quantity(2)
                                        .brandId(1L)
                                        .brandName("마리떼 키즈")
                                        .thumbnail("/2024/08/22/301a8a541cf14f81a3ba11c3cdd07843_20240822194539.jpg?width=700")
                                        .name("ENFANT CLASSIC LOGO COLLAR KNIT PULLOVER camel")
                                        .amount(53900)
                                        .build()
                        ))
                        .deliveryFees(Map.of(1L, 2500))
                        .receiver(OrderReceiver.builder()
                                .name("송송이")
                                .address("서울 특별시 성북구 보문로")
                                .zipCode("11112")
                                .phone("010-1111-1111")
                                .build())
                        .status(OrderStatus.ORDER_COMPLETED)
                        .build(),
                Order.builder()
                        .orderId("2032032003030-afsfdasfdsfdsafdl3")
                        .userId(2L)
                        .products(Map.of(
                                "4321", OrderProduct.builder()
                                        .quantity(1)
                                        .brandId(2L)
                                        .brandName("메종드알로하")
                                        .thumbnail("/2022/05/27/161c46b9cde74272b2d9616859f820fa_20220527185027.jpg?width=700")
                                        .name("Throw Me! 던져서 뿌리는 종이릴 꽃가루")
                                        .amount(10000)
                                        .build()
                        ))
                        .deliveryFees(Map.of(2L, 3000))
                        .receiver(OrderReceiver.builder()
                                .name("송송이")
                                .address("서울 특별시 성북구 보문로")
                                .zipCode("11112")
                                .phone("010-1111-1111")
                                .build())
                        .status(OrderStatus.ORDER_PLACED)
                        .build()
        );
        given(orderRepository.findByUserIdOrderByIdDesc(anyLong(), any()))
                .willReturn(new SliceImpl<>(orders));
        //when
        Slice<OrderItem> response = orderService.retrieveMyOrder(0, 10, 2L);
        //then
        assertEquals(response.getContent().size(), 2);

        OrderItem item = response.getContent().getFirst();
        assertEquals(item.orderNumber(), "2032032003030-afsfdasfdsfdsafdl2");
        assertEquals(item.brands().size(), 1);
        assertEquals(item.status(), OrderStatus.ORDER_COMPLETED);

        OrderBrandItem orderBrandItem = item.brands().getFirst();
        assertEquals(orderBrandItem.brandId(), 1L);
        assertEquals(orderBrandItem.deliveryFee(), 2500);

        BrandProductItem brandProductItem = orderBrandItem.products().getFirst();
        assertEquals(brandProductItem.brandId(), 1L);
        assertEquals(brandProductItem.brandName(), "마리떼 키즈");
        assertEquals(brandProductItem.name(), "ENFANT CLASSIC LOGO COLLAR KNIT PULLOVER camel");
        assertEquals(brandProductItem.quantity(), 2);
        assertEquals(brandProductItem.amount(), 53900);
        assertEquals(brandProductItem.thumbnail(), "/2024/08/22/301a8a541cf14f81a3ba11c3cdd07843_20240822194539.jpg?width=700");

        item = response.getContent().getLast();
        assertEquals(item.orderNumber(), "2032032003030-afsfdasfdsfdsafdl3");
        assertEquals(item.brands().size(), 1);
        assertEquals(item.status(), OrderStatus.ORDER_PLACED);

        orderBrandItem = item.brands().getFirst();
        assertEquals(orderBrandItem.brandId(), 2L);
        assertEquals(orderBrandItem.deliveryFee(), 3000);

        brandProductItem = orderBrandItem.products().getFirst();
        assertEquals(brandProductItem.brandId(), 2L);
        assertEquals(brandProductItem.brandName(), "메종드알로하");
        assertEquals(brandProductItem.name(), "Throw Me! 던져서 뿌리는 종이릴 꽃가루");
        assertEquals(brandProductItem.quantity(), 1);
        assertEquals(brandProductItem.amount(), 10000);
        assertEquals(brandProductItem.thumbnail(), "/2022/05/27/161c46b9cde74272b2d9616859f820fa_20220527185027.jpg?width=700");
    }


}