package cm.twentysix.user.exception;

import lombok.Getter;

@Getter
public class EmailAuthException extends RuntimeException {
    private final Error error;

    public EmailAuthException(Error error) {
        this.error = error;
    }
}
