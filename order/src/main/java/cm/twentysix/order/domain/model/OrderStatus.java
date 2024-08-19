package cm.twentysix.order.domain.model;

public enum OrderStatus {
    CHECK_FAIL, PAYMENT_FAIL, PAYMENT_PENDING, ORDER_PLACED, IN_TRANSIT, DELIVERED, ORDER_COMPLETED;


    public boolean isOrderProcessingStatus() {
        return PAYMENT_PENDING.equals(this) || ORDER_PLACED.equals(this);
    }

}
