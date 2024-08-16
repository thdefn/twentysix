package cm.twentysix.user.exception;

import lombok.Getter;

@Getter
public class AddressException extends RuntimeException {
    private final Error error;

    public AddressException(Error error) {
        this.error = error;
    }
}
