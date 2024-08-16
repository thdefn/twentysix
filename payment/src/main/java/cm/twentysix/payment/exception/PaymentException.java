package cm.twentysix.payment.exception;

import lombok.Getter;

@Getter
public class PaymentException extends RuntimeException {
    private final Error error;

    public PaymentException(Error error) {
        this.error = error;
    }
}
