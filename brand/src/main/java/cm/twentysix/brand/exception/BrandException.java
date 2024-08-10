package cm.twentysix.brand.exception;

import lombok.Getter;

@Getter
public class BrandException extends RuntimeException {
    private final Error error;

    public BrandException(Error error) {
        this.error = error;
    }
}
