package cm.twentysix.product.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

import static cm.twentysix.product.exception.Error.REQUEST_ARGUMENT_NOT_VALID;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final String LOG_FORMAT = "Class : {}, Code : {}, Message : {}";

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionResponse<String>> handleConstraintViolationException(ConstraintViolationException e) {
        log.info(LOG_FORMAT, e.getClass().getSimpleName(), REQUEST_ARGUMENT_NOT_VALID, REQUEST_ARGUMENT_NOT_VALID.message);
        return ResponseEntity.status(REQUEST_ARGUMENT_NOT_VALID.httpStatus)
                .body(new ExceptionResponse<>(REQUEST_ARGUMENT_NOT_VALID.name(), e.getMessage()));
    }

    @ExceptionHandler(ProductException.class)
    public ResponseEntity<ExceptionResponse<String>> handleProductException(ProductException e) {
        log.info(LOG_FORMAT, e.getClass().getSimpleName(), e.getError(), e.getError().message);
        return ResponseEntity.status(e.getError().httpStatus)
                .body(new ExceptionResponse<>(e.getError().name(), e.getError().message));
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse<Map<String, String>>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));

        log.info(LOG_FORMAT, e.getClass().getSimpleName(), REQUEST_ARGUMENT_NOT_VALID, REQUEST_ARGUMENT_NOT_VALID.message);
        return ResponseEntity.status(e.getStatusCode())
                .body(new ExceptionResponse<>(REQUEST_ARGUMENT_NOT_VALID.name(), errors));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse<String>> handleException(Exception e) {
        log.error(LOG_FORMAT, e.getClass().getSimpleName(), HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        return ResponseEntity.internalServerError()
                .body(new ExceptionResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.name(), e.getMessage()));
    }
}
