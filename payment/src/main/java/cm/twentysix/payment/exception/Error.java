package cm.twentysix.payment.exception;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum Error {
    GRPC_COMMUNICATION_ERROR(HttpStatus.BAD_REQUEST, "grpc 통신 중 문제가 발생했습니다."),
    MESSAGE_SEND_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "메세지 퍼블리싱에 문제가 발생했습니다."),
    REQUEST_ARGUMENT_NOT_VALID(HttpStatus.BAD_REQUEST, "요청 데이터의 형식을 확인해주세요.");
    public final HttpStatus httpStatus;
    public final String message;
}
