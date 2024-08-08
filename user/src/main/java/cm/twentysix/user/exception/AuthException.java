package cm.twentysix.user.exception;

import lombok.Getter;

@Getter
public class AuthException extends RuntimeException {
    private final Error error;

    public AuthException(Error error) {
        this.error = error;
    }
}
