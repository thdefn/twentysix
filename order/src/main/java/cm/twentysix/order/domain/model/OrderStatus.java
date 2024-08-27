package cm.twentysix.order.domain.model;

public enum OrderStatus {
    CHECK_FAIL, PAYMENT_FAIL, PAYMENT_PENDING,
    ORDER_PLACED, IN_TRANSIT, DELIVERED, ORDER_COMPLETED,
    CANCEL, BEING_RETURNED, RETURN_COMPLETED;


    public boolean isPreparationStatus() {
        return IN_TRANSIT.equals(this) || ORDER_PLACED.equals(this);
    }

}
