package cm.twentysix.order.exception;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

import static cm.twentysix.order.exception.Error.GRPC_COMMUNICATION_ERROR;
import static cm.twentysix.order.exception.Error.REQUEST_ARGUMENT_NOT_VALID;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Map<Status.Code, HttpStatus> grpcStatusHttpStatusMap = Map.of(Status.Code.NOT_FOUND, HttpStatus.NOT_FOUND, Status.Code.INVALID_ARGUMENT, HttpStatus.BAD_REQUEST, Status.Code.UNAVAILABLE, HttpStatus.SERVICE_UNAVAILABLE);
    private static final String LOG_FORMAT = "Class : {}, Code : {}, Message : {}";

    @ExceptionHandler(StatusRuntimeException.class)
    public ResponseEntity<ExceptionResponse<String>> handleStatusRuntimeException(StatusRuntimeException e) {
        Status status = e.getStatus();
        String errorMessage = e.getMessage();
        HttpStatus httpStatus = grpcStatusHttpStatusMap.getOrDefault(status.getCode(), HttpStatus.INTERNAL_SERVER_ERROR);
        log.info(LOG_FORMAT, e.getClass().getSimpleName(), GRPC_COMMUNICATION_ERROR, errorMessage);
        return ResponseEntity.status(httpStatus)
                .body(new ExceptionResponse<>(GRPC_COMMUNICATION_ERROR.name(), errorMessage));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionResponse<String>> handleConstraintViolationException(ConstraintViolationException e) {
        log.info(LOG_FORMAT, e.getClass().getSimpleName(), REQUEST_ARGUMENT_NOT_VALID, REQUEST_ARGUMENT_NOT_VALID.message);
        return ResponseEntity.status(REQUEST_ARGUMENT_NOT_VALID.httpStatus)
                .body(new ExceptionResponse<>(REQUEST_ARGUMENT_NOT_VALID.name(), e.getMessage()));
    }

    @ExceptionHandler(OrderException.class)
    public ResponseEntity<ExceptionResponse<String>> handleOrderException(OrderException e) {
        log.info(LOG_FORMAT, e.getClass().getSimpleName(), e.getError(), e.getError().message);
        return ResponseEntity.status(e.getError().httpStatus)
                .body(new ExceptionResponse<>(e.getError().name(), e.getError().message));
    }

    @ExceptionHandler(CartException.class)
    public ResponseEntity<ExceptionResponse<String>> handleCartException(CartException e) {
        log.info(LOG_FORMAT, e.getClass().getSimpleName(), e.getError(), e.getError().message);
        return ResponseEntity.status(e.getError().httpStatus)
                .body(new ExceptionResponse<>(e.getError().name(), e.getError().message));
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ExceptionResponse<String>> handleMissingRequestHeaderException(MissingRequestHeaderException e) {
        log.info(LOG_FORMAT, e.getClass().getSimpleName(), HttpStatus.BAD_REQUEST.name(), e.getMessage());
        return ResponseEntity.badRequest()
                .body(new ExceptionResponse<>(HttpStatus.BAD_REQUEST.name(), e.getMessage()));
    }

    @ExceptionHandler(CircuitBreakerException.class)
    public ResponseEntity<ExceptionResponse<String>> handleCircuitBreakerException(CircuitBreakerException e) {
        log.info(LOG_FORMAT, e.getClass().getSimpleName(), HttpStatus.SERVICE_UNAVAILABLE.name(), e.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ExceptionResponse<>(HttpStatus.SERVICE_UNAVAILABLE.name(), e.getMessage()));
    }

    @ExceptionHandler(CallNotPermittedException.class)
    public ResponseEntity<ExceptionResponse<String>> handleCallNotPermittedException(CallNotPermittedException e) {
        log.info(LOG_FORMAT, e.getClass().getSimpleName(), HttpStatus.SERVICE_UNAVAILABLE.name(), e.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ExceptionResponse<>(HttpStatus.SERVICE_UNAVAILABLE.name(), e.getMessage()));
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
        log.error(LOG_FORMAT, e.getClass().getSimpleName(), HttpStatus.INTERNAL_SERVER_ERROR, e.getStackTrace());
        return ResponseEntity.internalServerError()
                .body(new ExceptionResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.name(), e.getMessage()));
    }
}
