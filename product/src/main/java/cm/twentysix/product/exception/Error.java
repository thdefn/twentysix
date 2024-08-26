package cm.twentysix.product.exception;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum Error {
    STOCK_SHORTAGE(HttpStatus.CONFLICT, "재고 부족으로 주문에 실패했습니다."),
    MESSAGE_SEND_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "메세지 퍼블리싱에 문제가 발생했습니다. "),
    NOT_BRAND_ADMIN(HttpStatus.NOT_FOUND, "브랜드 관리자가 아닙니다."),
    GRPC_COMMUNICATION_ERROR(HttpStatus.BAD_REQUEST, "grpc 통신 중 문제가 발생했습니다."),
    FILE_UPLOAD_FAILED(HttpStatus.BAD_REQUEST, "파일 업로드에 실패했습니다."),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "일치하는 상품이 없습니다."),
    ALREADY_DELETED_PRODUCT(HttpStatus.NOT_FOUND, "이미 삭제된 상품입니다."),
    NOT_PRODUCT_ADMIN(HttpStatus.NOT_FOUND, "상품 게시자가 아닙니다."),
    REQUEST_ARGUMENT_NOT_VALID(HttpStatus.BAD_REQUEST, "요청 데이터의 형식을 확인해주세요.");
    public final HttpStatus httpStatus;
    public final String message;
}
