package cm.twentysix.user.exception;

import lombok.Getter;

@Getter
public class UserException extends RuntimeException {
    private final Error error;

    public UserException(Error error) {
        this.error = error;
    }
}
