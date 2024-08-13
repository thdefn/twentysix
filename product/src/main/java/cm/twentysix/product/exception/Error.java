package cm.twentysix.product.exception;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum Error {
    FILE_UPLOAD_FAILED(HttpStatus.BAD_REQUEST, "파일 업로드에 실패했습니다."),
    PRODUCT_NOT_FOUD(HttpStatus.NOT_FOUND, "일치하는 상품이 없습니다."),
    NOT_PRODUCT_ADMIN(HttpStatus.NOT_FOUND, "상품 게시자가 아닙니다."),
    REQUEST_ARGUMENT_NOT_VALID(HttpStatus.BAD_REQUEST, "요청 데이터의 형식을 확인해주세요.");
    public final HttpStatus httpStatus;
    public final String message;
}
