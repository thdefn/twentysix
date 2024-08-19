package cm.twentysix.payment.domain.model;

import cm.twentysix.OrderProto.OrderInfoResponse;
import cm.twentysix.payment.dto.PaymentResponse;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Payment extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 100)
    private String orderId;

    @Column(unique = true, length = 200)
    private String paymentKey;

    @Column
    private String orderName;

    @Column
    private Integer amount;

    @Column
    @Enumerated(EnumType.STRING)
    private PaymentMethod method;

    @Column
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Column
    private String methodDetail;

    @Column
    private LocalDateTime requestedAt;

    @Column
    private LocalDateTime approvedAt;

    @Column
    private Long userId;

    // TODO: cancel 관련


    @Builder
    public Payment(String orderId, String paymentKey, String orderName, Integer amount, PaymentMethod method, String methodDetail, LocalDateTime requestedAt, LocalDateTime approvedAt, Long userId, PaymentStatus status) {
        this.orderId = orderId;
        this.paymentKey = paymentKey;
        this.orderName = orderName;
        this.amount = amount;
        this.method = method;
        this.methodDetail = methodDetail;
        this.requestedAt = requestedAt;
        this.approvedAt = approvedAt;
        this.userId = userId;
        this.status = status;
    }

    public void updateOrderInfo(OrderInfoResponse orderInfo) {
        this.userId = orderInfo.getUserId();
        this.orderName = orderInfo.getOrderName();
        this.amount = orderInfo.getPaymentAmount();
    }

    public static Payment of(String orderId, PaymentStatus status) {
        return Payment.builder()
                .orderId(orderId)
                .status(status)
                .build();
    }

    public void block() {
        status = PaymentStatus.BLOCK;
    }

    public void cancel() {
        status = PaymentStatus.CANCEL;
    }

    public void confirmPayment(PaymentResponse response) {
        paymentKey = response.paymentKey();
        amount = response.totalAmount();
        requestedAt = response.getRequestedAt();
    }

    public void complete(PaymentResponse response) {
        method = PaymentMethod.valueOf(response.method());
        methodDetail = method.getMethodDetail(response);
        approvedAt = response.getApprovedAt();
        status = PaymentStatus.COMPLETE;
    }

}
