package cm.twentysix.brand.exception;

import java.time.LocalDateTime;

public record ExceptionResponse<T>(
        String code,
        T message,
        LocalDateTime timestamp
) {
    public ExceptionResponse(String code, T message) {
        this(code, message, LocalDateTime.now());
    }
}
