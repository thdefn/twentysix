package cm.twentysix.order.exception;

import lombok.Getter;

@Getter
public class CartException extends RuntimeException {
    private final Error error;

    public CartException(Error error) {
        this.error = error;
    }
}
