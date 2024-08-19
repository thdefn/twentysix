package cm.twentysix.payment.constant;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum CancelReason {
    STOCK_SHORTAGE("재고 부족으로 인한 취소"),
    CUSTOMER_DECISION("고객 변심으로 인한 취소");
    public final String message;
}
