package cm.twentysix.order.exception;

import lombok.Getter;

@Getter
public class CircuitBreakerException extends RuntimeException {
    public CircuitBreakerException(String message) {
        super(message);
    }
}
