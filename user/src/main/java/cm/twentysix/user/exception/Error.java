package cm.twentysix.user.exception;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum Error {
    EMAIL_VERIFY_CODE_UNMATCHED(HttpStatus.UNAUTHORIZED, "인증 코드가 다릅니다."),
    NOT_VALID_EMAIL(HttpStatus.BAD_REQUEST, "인증받지 않거나 인증이 만료된 이메일입니다."),
    ALREADY_VERIFIED(HttpStatus.BAD_REQUEST, "이미 인증된 이메일입니다."),
    EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "해당하는 이메일이 존재하지 않습니다."),
    ALREADY_REGISTER_EMAIL(HttpStatus.BAD_REQUEST, "이미 가입한 이메일입니다."),
    REQUEST_ARGUMENT_NOT_VALID(HttpStatus.BAD_REQUEST, "요청 데이터의 형식을 확인해주세요.");
    public final HttpStatus httpStatus;
    public final String message;
}
