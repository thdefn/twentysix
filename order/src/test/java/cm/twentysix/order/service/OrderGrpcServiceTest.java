package cm.twentysix.order.service;

import cm.twentysix.OrderProto;
import cm.twentysix.OrderProto.ProductQuantity;
import cm.twentysix.order.domain.model.Order;
import cm.twentysix.order.domain.model.OrderProduct;
import cm.twentysix.order.domain.model.OrderReceiver;
import cm.twentysix.order.domain.model.OrderStatus;
import cm.twentysix.order.domain.repository.OrderRepository;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static io.grpc.Status.Code.INVALID_ARGUMENT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderGrpcServiceTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private StreamObserver<OrderProto.OrderInfoResponse> responseObserver;
    @InjectMocks
    private OrderGrpcService orderGrpcService;

    @Test
    void getOrder_success() {
        //given
        OrderProto.OrderInfoRequest request = OrderProto.OrderInfoRequest.newBuilder()
                .setOrderId("2032032003030-afsfdasfdsfdsafdl2")
                .build();

        Order order = Order.builder()
                .orderId("2032032003030-afsfdasfdsfdsafdl2")
                .userId(1L)
                .totalAmount(30000)
                .totalDeliveryFee(3000)
                .products(Map.of(
                        "1234", OrderProduct.builder()
                                .brandId(1L)
                                .name("강아지 나시")
                                .quantity(2).build(),
                        "2345", OrderProduct.builder()
                                .brandId(2L)
                                .name("강아지 물티슈")
                                .quantity(1).build()
                ))
                .deliveryFees(Map.of(
                        1L, 3000, 2L, 0
                ))
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
        orderGrpcService.getOrder(request, responseObserver);
        //then
        ArgumentCaptor<OrderProto.OrderInfoResponse> responseCaptor = ArgumentCaptor.forClass(OrderProto.OrderInfoResponse.class);
        verify(responseObserver).onNext(responseCaptor.capture());
        OrderProto.OrderInfoResponse captured = responseCaptor.getValue();
        assertEquals("2032032003030-afsfdasfdsfdsafdl2", captured.getOrderId());
        assertEquals(1L, captured.getUserId());
        assertEquals(order.getOrderName(), captured.getOrderName());
        assertEquals(2, captured.getProductQuantityList().size());
        List<ProductQuantity> productQuantities = captured.getProductQuantityList().stream().sorted(Comparator.comparingInt(ProductQuantity::getQuantity)).toList();
        assertEquals("1234", productQuantities.getLast().getProductId());
        assertEquals(2, productQuantities.getLast().getQuantity());
        assertEquals("2345", productQuantities.getFirst().getProductId());
        assertEquals(1, productQuantities.getFirst().getQuantity());
    }

    @Test
    void getOrder_fail_INVALID_ARGUMENT() {
        //given
        OrderProto.OrderInfoRequest request = OrderProto.OrderInfoRequest.newBuilder()
                .setOrderId("2032032003030-afsfdasfdsfdsafdl2")
                .build();
        given(orderRepository.findByOrderId(anyString())).willReturn(Optional.empty());
        //when
        orderGrpcService.getOrder(request, responseObserver);
        //then
        ArgumentCaptor<StatusRuntimeException> exceptionArgumentCaptor = ArgumentCaptor.forClass(StatusRuntimeException.class);
        verify(responseObserver).onError(exceptionArgumentCaptor.capture());
        assertEquals(exceptionArgumentCaptor.getValue().getStatus().getCode(), INVALID_ARGUMENT);
    }

    @Test
    void getOrder_fail_INTERNAL() {
        //given
        OrderProto.OrderInfoRequest request = OrderProto.OrderInfoRequest.newBuilder()
                .setOrderId("2032032003030-afsfdasfdsfdsafdl2")
                .build();
        doThrow(new RuntimeException())
                .when(orderRepository).findByOrderId(anyString());
        //when
        orderGrpcService.getOrder(request, responseObserver);
        //then
        ArgumentCaptor<StatusRuntimeException> exceptionArgumentCaptor = ArgumentCaptor.forClass(StatusRuntimeException.class);
        verify(responseObserver).onError(exceptionArgumentCaptor.capture());
        assertEquals(exceptionArgumentCaptor.getValue().getStatus().getCode(), Status.Code.INTERNAL);
    }

}