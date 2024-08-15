package cm.twentysix.order.exception;

import lombok.Getter;

@Getter
public class OrderException extends RuntimeException {
    private final Error error;

    public OrderException(Error error) {
        this.error = error;
    }
}
