package cm.twentysix.payment.service;

import cm.twentysix.payment.client.OrderGrpcClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static cm.twentysix.OrderProto.OrderInfoResponse;

@RequiredArgsConstructor
@Service
public class CheckoutService {
    private final OrderGrpcClient orderGrpcClient;

    public OrderInfoResponse getOrderInfo(String orderId) {
        return orderGrpcClient.getOrderInfo(orderId);
    }
}
