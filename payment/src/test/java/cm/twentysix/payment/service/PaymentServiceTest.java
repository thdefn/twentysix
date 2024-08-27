package cm.twentysix.payment.service;

import cm.twentysix.OrderProto;
import cm.twentysix.ProductProto;
import cm.twentysix.payment.client.OrderGrpcClient;
import cm.twentysix.payment.client.ProductGrpcClient;
import cm.twentysix.payment.client.TossPaymentClient;
import cm.twentysix.payment.constant.CancelReason;
import cm.twentysix.payment.domain.model.Payment;
import cm.twentysix.payment.domain.model.PaymentMethod;
import cm.twentysix.payment.domain.model.PaymentStatus;
import cm.twentysix.payment.domain.repository.PaymentRepository;
import cm.twentysix.payment.dto.*;
import cm.twentysix.payment.exception.Error;
import cm.twentysix.payment.exception.PaymentException;
import cm.twentysix.payment.exception.ProductException;
import cm.twentysix.payment.messaging.MessageSender;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {
    @Mock
    private OrderGrpcClient orderGrpcClient;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private TossPaymentClient tossPaymentClient;
    @Mock
    private MessageSender messageSender;
    @Mock
    private ApplicationEventPublisher applicationEventPublisher;
    @Mock
    private ProductGrpcClient productGrpcClient;
    @InjectMocks
    private PaymentService paymentService;

    @Test
    void getRequiredPayment_success() {
        //given
        given(orderGrpcClient.getOrderInfo(anyString())).willReturn(
                OrderProto.OrderInfoResponse.newBuilder()
                        .setOrderName("강아지 나시 외 1건")
                        .setPaymentAmount(10000)
                        .setOrderId("20240101033323212-hashhashhash")
                        .addAllProductQuantity(
                                List.of(
                                        OrderProto.ProductQuantity.newBuilder()
                                                .setQuantity(1)
                                                .setProductId("12345")
                                                .build()
                                )
                        )
                        .setUserId(1L)
                        .build());
        given(paymentRepository.findByOrderId(anyString()))
                .willReturn(Optional.empty());
        Payment savedPayment = Payment.builder()
                .amount(10000)
                .orderName("any-order-name")
                .userId(1L)
                .orderId("any-order-id")
                .status(PaymentStatus.PENDING)
                .build();
        given(paymentRepository.save(any())).willReturn(savedPayment);
        //when
        RequiredPaymentResponse response = paymentService.getRequiredPayment("20240101033323212-hashhashhash");
        //then
        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository, times(1)).save(paymentCaptor.capture());
        Payment saved = paymentCaptor.getValue();
        assertEquals(saved.getStatus(), PaymentStatus.PENDING);
        assertEquals(saved.getOrderId(), "20240101033323212-hashhashhash");
        assertEquals(saved.getUserId(), 1L);
        assertEquals(saved.getOrderName(), "강아지 나시 외 1건");
        assertEquals(saved.getAmount(), 10000);


        assertEquals(response.amount(), savedPayment.getAmount());
        assertEquals(response.orderName(), savedPayment.getOrderName());
        assertFalse(response.isBlocked());
        assertEquals(response.userId(), savedPayment.getUserId());
        assertEquals(response.orderId(), savedPayment.getOrderId());
    }

    @Test
    void getRequiredPayment_success_WhenStatusIsBLOCK() {
        //given
        given(orderGrpcClient.getOrderInfo(anyString())).willReturn(
                OrderProto.OrderInfoResponse.newBuilder()
                        .setOrderName("강아지 나시 외 1건")
                        .setPaymentAmount(10000)
                        .setOrderId("20240101033323212-hashhashhash")
                        .setUserId(1L)
                        .addAllProductQuantity(
                                List.of(
                                        OrderProto.ProductQuantity.newBuilder()
                                                .setQuantity(1)
                                                .setProductId("12345")
                                                .build()
                                )
                        )
                        .build());
        Payment payment = Payment.builder()
                .amount(10000)
                .orderName("any-order-name")
                .userId(1L)
                .orderId("any-order-id")
                .status(PaymentStatus.BLOCK)
                .build();
        given(paymentRepository.findByOrderId(anyString()))
                .willReturn(Optional.of(payment));
        given(paymentRepository.save(any())).willReturn(payment);
        //when
        RequiredPaymentResponse response = paymentService.getRequiredPayment("20240101033323212-hashhashhash");
        //then
        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository, times(1)).save(paymentCaptor.capture());
        Payment saved = paymentCaptor.getValue();
        assertEquals(saved.getOrderId(), "any-order-id");
        assertEquals(saved.getUserId(), 1L);
        assertEquals(saved.getStatus(), PaymentStatus.BLOCK);
        assertEquals(saved.getOrderName(), "강아지 나시 외 1건");
        assertEquals(saved.getAmount(), 10000);

        assertEquals(response.amount(), payment.getAmount());
        assertEquals(response.orderName(), payment.getOrderName());
        assertTrue(response.isBlocked());
        assertEquals(response.userId(), payment.getUserId());
        assertEquals(response.orderId(), payment.getOrderId());
    }

    @Test
    void confirm_success() {
        //given
        PaymentForm form = new PaymentForm("any-order-id", "10000", "any-payment-key");
        Payment payment = Payment.builder()
                .amount(10000)
                .orderName("any-order-name")
                .userId(1L)
                .orderId("any-order-id")
                .productQuantity(Map.of("123", 1))
                .status(PaymentStatus.PENDING)
                .build();
        given(paymentRepository.findByOrderId(anyString()))
                .willReturn(Optional.of(payment));
        given(productGrpcClient.checkProductStockRequest(any(), anyString()))
                .willReturn(ProductProto.CheckProductStockResponse.newBuilder()
                                .setIsSuccess(true)
                                .build());
        PaymentResponse response = PaymentResponse.builder()
                .paymentKey("any-payment-key")
                .totalAmount(10000)
                .requestedAt("2022-01-01T00:00:00+09:00")
                .approvedAt("2022-01-01T00:00:00+09:00")
                .orderId("any-order-id")
                .status("DONE")
                .method("간편결제")
                .easyPay(new PaymentResponse.EasyPay("카카오페이", 10000, 0))
                .build();
        given(tossPaymentClient.confirm(any())).willReturn(response);
        //when
        paymentService.confirm(form);
        //then
        ArgumentCaptor<PaymentFinalizedEvent> paymentFinalizedEventCaptor = ArgumentCaptor.forClass(PaymentFinalizedEvent.class);
        verify(messageSender, times(1)).sendPaymentFinalizedEvent(paymentFinalizedEventCaptor.capture());
        assertEquals(paymentFinalizedEventCaptor.getValue().orderId(), "any-order-id");
        assertEquals(payment.getPaymentKey(), "any-payment-key");
        assertEquals(payment.getAmount(), 10000);
        assertEquals(payment.getRequestedAt(), LocalDateTime.of(2022, 1, 1, 0, 0));
        assertEquals(payment.getApprovedAt(), LocalDateTime.of(2022, 1, 1, 0, 0));
        assertEquals(payment.getStatus(), PaymentStatus.COMPLETE);
        assertEquals(payment.getMethod(), PaymentMethod.간편결제);
        assertEquals(payment.getMethodDetail(), response.getEasyPay());
    }

    @Test
    void confirm_fail_STOCK_SHORTAGE() {
        //given
        PaymentForm form = new PaymentForm("any-order-id", "10000", "any-payment-key");
        Payment payment = Payment.builder()
                .amount(10000)
                .orderName("any-order-name")
                .userId(1L)
                .orderId("any-order-id")
                .productQuantity(Map.of("123", 1))
                .status(PaymentStatus.PENDING)
                .build();
        given(paymentRepository.findByOrderId(anyString()))
                .willReturn(Optional.of(payment));
        given(productGrpcClient.checkProductStockRequest(anyMap(), anyString()))
                .willReturn(ProductProto.CheckProductStockResponse.newBuilder()
                        .setIsSuccess(false)
                        .build());
        //when
        ProductException e = assertThrows(ProductException.class, () -> paymentService.confirm(form));
        //then
        assertEquals(e.getError(), Error.STOCK_SHORTAGE);
    }

    @Test
    void confirm_fail_PAYMENT_FAILED() {
        //given
        PaymentForm form = new PaymentForm("any-order-id", "10000", "any-payment-key");
        Payment payment = Payment.builder()
                .amount(10000)
                .orderName("any-order-name")
                .userId(1L)
                .orderId("any-order-id")
                .productQuantity(Map.of("123", 1))
                .status(PaymentStatus.PENDING)
                .build();
        given(paymentRepository.findByOrderId(anyString()))
                .willReturn(Optional.of(payment));
        given(productGrpcClient.checkProductStockRequest(any(), anyString()))
                .willReturn(ProductProto.CheckProductStockResponse.newBuilder()
                        .setIsSuccess(true)
                        .build());
        PaymentResponse response = PaymentResponse.builder()
                .paymentKey("any-payment-key")
                .totalAmount(10000)
                .orderId("any-order-id")
                .requestedAt("2022-01-01T00:00:00+09:00")
                .approvedAt("2022-01-01T00:00:00+09:00")
                .status("ABORTED")
                .method("간편결제")
                .easyPay(new PaymentResponse.EasyPay("카카오페이", 10000, 0))
                .build();
        given(tossPaymentClient.confirm(any())).willReturn(response);
        //when
        PaymentException e = assertThrows(PaymentException.class, () -> paymentService.confirm(form));
        //then
        ArgumentCaptor<PaymentAbortedEvent> paymentAbortedEventCaptor = ArgumentCaptor.forClass(PaymentAbortedEvent.class);
        verify(applicationEventPublisher, times(1)).publishEvent(paymentAbortedEventCaptor.capture());
        PaymentAbortedEvent event = paymentAbortedEventCaptor.getValue();
        assertEquals(event.response(), response);
        assertEquals(e.getError(), Error.PAYMENT_FAILED);
    }


    @Test
    void handleProductOrderFailedEvent_success_WhenStatusPENDING() {
        //given
        OrderCancelledEvent event = new OrderCancelledEvent("any-order-id", new HashMap<>());
        Payment payment = Payment.builder()
                .amount(10000)
                .orderName("any-order-name")
                .userId(1L)
                .orderId("any-order-id")
                .productQuantity(Map.of("123", 1))
                .status(PaymentStatus.PENDING)
                .build();
        given(paymentRepository.findByOrderId(anyString()))
                .willReturn(Optional.of(payment));
        //when
        paymentService.cancelPayment(event.orderId(), CancelReason.STOCK_SHORTAGE.message);
        //then
        assertEquals(payment.getStatus(), PaymentStatus.BLOCK);
    }

    @Test
    void handleProductOrderFailedEvent_success_WhenStatusCOMPLETE() {
        //given
        OrderCancelledEvent event = new OrderCancelledEvent("any-order-id", new HashMap<>());
        Payment payment = Payment.builder()
                .amount(10000)
                .orderName("any-order-name")
                .userId(1L)
                .paymentKey("any-payment-key")
                .orderId("any-order-id")
                .productQuantity(Map.of("123", 1))
                .status(PaymentStatus.COMPLETE)
                .build();
        given(paymentRepository.findByOrderId(anyString()))
                .willReturn(Optional.of(payment));
        //when
        paymentService.cancelPayment(event.orderId(), CancelReason.STOCK_SHORTAGE.message);
        //then
        verify(tossPaymentClient, times(1)).cancel(eq("any-payment-key"), any());
        assertEquals(payment.getStatus(), PaymentStatus.CANCEL);
    }

    @Test
    void handleProductOrderFailedEvent_success() {
        //given
        OrderCancelledEvent event = new OrderCancelledEvent("any-order-id", new HashMap<>());
        given(paymentRepository.findByOrderId(anyString()))
                .willReturn(Optional.empty());
        //when
        paymentService.cancelPayment(event.orderId(), CancelReason.STOCK_SHORTAGE.message);
        //then
        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository, times(1)).save(paymentCaptor.capture());
        Payment saved = paymentCaptor.getValue();
        assertEquals(saved.getStatus(), PaymentStatus.BLOCK);
    }
}