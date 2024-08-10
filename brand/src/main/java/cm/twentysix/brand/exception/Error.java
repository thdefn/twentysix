package cm.twentysix.brand.exception;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum Error {
    NOT_BRAND_OWNER(HttpStatus.FORBIDDEN, "브랜드 관리자가 아닙니다."),
    BRAND_NOT_FOUND(HttpStatus.NOT_FOUND, "해당하는 브랜드가 존재하지 않습니다."),
    BRAND_LIMIT_OVER(HttpStatus.BAD_REQUEST, "허용 가능한 브랜드 개수를 초과했습니다."),
    REQUEST_ARGUMENT_NOT_VALID(HttpStatus.BAD_REQUEST, "요청 데이터의 형식을 확인해주세요.");
    public final HttpStatus httpStatus;
    public final String message;
}
