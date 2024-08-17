package cm.twentysix.payment.client;

import cm.twentysix.OrderProto.OrderInfoResponse;
import cm.twentysix.OrderServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;

import static cm.twentysix.OrderProto.OrderInfoRequest;


@Component
@Slf4j
public class OrderGrpcClient {
    private final OrderServiceGrpc.OrderServiceBlockingStub orderServiceBlockingStub;
    private final ManagedChannel managedChannel;

    public OrderGrpcClient(@Value("${grpc.client.order.host}") String host, @Value("${grpc.client.order.port}") int port) {
        managedChannel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        orderServiceBlockingStub = OrderServiceGrpc.newBlockingStub(managedChannel);
    }

    public OrderInfoResponse getOrderInfo(String orderId) {
        return orderServiceBlockingStub.getOrder(OrderInfoRequest
                .newBuilder()
                .setOrderId(orderId).build());
    }

    @PreDestroy
    public void shutdown() {
        if (managedChannel != null && !managedChannel.isShutdown()) {
            managedChannel.shutdown();
            try {
                if (!managedChannel.awaitTermination(5, TimeUnit.SECONDS)) {
                    managedChannel.shutdownNow();
                }
            } catch (InterruptedException e) {
                managedChannel.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

}
