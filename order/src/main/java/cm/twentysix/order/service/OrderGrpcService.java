package cm.twentysix.order.service;

import cm.twentysix.OrderProto;
import cm.twentysix.OrderProto.OrderInfoResponse;
import cm.twentysix.OrderServiceGrpc;
import cm.twentysix.order.domain.model.Order;
import cm.twentysix.order.domain.repository.OrderRepository;
import cm.twentysix.order.exception.Error;
import cm.twentysix.order.exception.OrderException;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderGrpcService extends OrderServiceGrpc.OrderServiceImplBase {

    private final OrderRepository orderRepository;

    @Override
    public void getOrder(OrderProto.OrderInfoRequest request, StreamObserver<OrderInfoResponse> responseObserver) {
        try {
            Order order = orderRepository.findByOrderId(request.getOrderId())
                    .orElseThrow(() -> new OrderException(Error.ORDER_NOT_FOUND));

            OrderInfoResponse response = OrderInfoResponse.newBuilder()
                    .setOrderId(order.getOrderId())
                    .setPaymentAmount(order.getPaymentAmount())
                    .setUserId(order.getUserId())
                    .setOrderName(order.getOrderName())
                    .addAllProductQuantity(order.getProducts().entrySet().stream()
                            .map(entry -> OrderProto.ProductQuantity.newBuilder()
                                            .setProductId(entry.getKey())
                                            .setQuantity(entry.getValue().getQuantity())
                                            .build())
                            .collect(Collectors.toList()))
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (OrderException e) {
            responseObserver.onError(
                    Status.INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException()
            );
        } catch (Exception e) {
            responseObserver.onError(
                    Status.INTERNAL.withDescription(HttpStatus.INTERNAL_SERVER_ERROR.name()).asRuntimeException()
            );
        }
    }
}
