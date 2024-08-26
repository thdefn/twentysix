package cm.twentysix.product.exception;

import lombok.Getter;

@Getter
public class LockAcquisitionException extends RuntimeException {

    public LockAcquisitionException(String message) {
        super(message);
    }
}
