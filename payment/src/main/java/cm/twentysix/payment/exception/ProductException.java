package cm.twentysix.payment.exception;

import lombok.Getter;

@Getter
public class ProductException extends RuntimeException {
    private final Error error;

    public ProductException(Error error) {
        this.error = error;
    }
}
