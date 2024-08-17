package cm.twentysix.order.service;

import cm.twentysix.BrandProto;
import cm.twentysix.order.client.BrandGrpcClient;
import cm.twentysix.order.domain.model.*;
import cm.twentysix.order.domain.repository.OrderRepository;
import cm.twentysix.order.dto.*;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
    CartService cartService;
    @Mock
    MessageSender messageSender;
    @InjectMocks
    private OrderService orderService;

    static MockedStatic<IdUtil> mockIdUtil;

    @BeforeEach
    void init() {
        mockIdUtil = mockStatic(IdUtil.class);
        when(IdUtil.generate()).thenReturn("2032032003030-afsfdasfdsfdsafdl2");
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
        //when
        orderService.receiveOrder(form, 1L);
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
        assertEquals(saved.getUserId(), 1L);
        assertEquals(saved.getStatus(), OrderStatus.PAYMENT_PENDING);
    }

    @Test
    void receiveOrder_success_whenShouldSaveNewAddressIsFalse() {
        //given
        List<OrderProductItemForm> items = List.of(
                new OrderProductItemForm("123456", 1)
        );
        CreateOrderForm.ReceiverForm receiver = new CreateOrderForm.ReceiverForm(true, "송송이", "서울특별시 성북구 보문로", "11112", "010-2222-2222");
        CreateOrderForm form = new CreateOrderForm(items, false, true, receiver);
        //when
        orderService.receiveOrder(form, 1L);
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
    }

    @Test
    void approveOrDenyOrder_success_WhenOrderReplyEventIsSuccess() {
        //given
        ProductOrderItem item = new ProductOrderItem("수건", "1234.jpg", 1, 100000, 1L, "JAJU", 0);
        ProductOrderFailedEvent event = ProductOrderFailedEvent.builder()
                .orderId("2032032003030-afsfdasfdsfdsafdl2")
                .build();
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
        given(brandGrpcClient.findBrandInfo(anyList())).willReturn(Map.of(1L, BrandProto.BrandInfo.newBuilder()
                .setName("JAJU").setDeliveryFee(20000).setId(2L).setFreeDeliveryInfimum(500000).build()));
        //when
        orderService.handleProductOrderFailedEvent(event);
        //then
        assertTrue(order.getProducts().containsKey("1234t5gf"));
        OrderProduct product = order.getProducts().get("1234t5gf");
        assertEquals(product.getStatus(), OrderProductStatus.ORDER_PLACED);
        assertEquals(product.getBrandName(), "JAJU");
        assertEquals(product.getBrandId(), 1L);
        assertEquals(product.getThumbnail(), "1234.jpg");
        assertEquals(product.getQuantity(), 1);
        assertEquals(product.getAmount(), 100000);

        assertEquals(order.getOrderId(), "2032032003030-afsfdasfdsfdsafdl2");
        assertEquals(order.getTotalAmount(), 100000);
        assertEquals(order.getReceiver().getName(), "송송이");
        assertEquals(order.getReceiver().getPhone(), "010-1111-1111");
        assertEquals(order.getReceiver().getZipCode(), "11112");
        assertEquals(order.getReceiver().getAddress(), "서울 특별시 성북구 보문로");
        assertEquals(order.getUserId(), 1L);
        assertEquals(order.getStatus(), OrderStatus.PAYMENT_PENDING);
    }

    @Test
    void approveOrDenyOrder_success_WhenOrderReplyEventIsFail() {
        //given
        ProductOrderItem item = new ProductOrderItem("수건", "1234.jpg", 1, 100000, 1L, "JAJU", 0);
        ProductOrderFailedEvent event = ProductOrderFailedEvent.builder()
                .orderId("2032032003030-afsfdasfdsfdsafdl2")
                .build();
        Order order = Order.builder()
                .orderId("2032032003030-afsfdasfdsfdsafdl2")
                .userId(1L)
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
        orderService.handleProductOrderFailedEvent(event);
        //then
        verify(orderRepository, times(1)).delete(any());
    }

}