package cm.twentysix.brand.exception;

import lombok.Getter;

@Getter
public class FileException extends RuntimeException {
    private final Error error;

    public FileException(Error error) {
        this.error = error;
    }
}
