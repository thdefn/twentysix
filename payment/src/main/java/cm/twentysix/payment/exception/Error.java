package cm.twentysix.payment.exception;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum Error {
    NOT_FOUND_PAYMENT(HttpStatus.BAD_REQUEST, "해당하는 결제 건이 없습니다."),
    STOCK_SHORTAGE(HttpStatus.NO_CONTENT, "Order failed due to insufficient stock"),
    GRPC_COMMUNICATION_ERROR(HttpStatus.BAD_REQUEST, "grpc 통신 중 문제가 발생했습니다."),
    MESSAGE_SEND_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "메세지 퍼블리싱에 문제가 발생했습니다."),
    REQUEST_ARGUMENT_NOT_VALID(HttpStatus.BAD_REQUEST, "요청 데이터의 형식을 확인해주세요.");
    public final HttpStatus httpStatus;
    public final String message;
}
