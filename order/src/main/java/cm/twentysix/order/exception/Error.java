package cm.twentysix.order.exception;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum Error {
    ORDER_RETURN_NOT_ALLOWED(HttpStatus.NOT_FOUND, "반품 가능한 주문이 없습니다."),
    ORDER_IN_PREPARATION_NOT_FOUND(HttpStatus.NOT_FOUND, "준비중이거나 접수된 주문이 없습니다."),
    STOCK_SHORTAGE(HttpStatus.CONFLICT, "재고 부족으로 주문에 실패했습니다."),
    ORDER_CONTAIN_CLOSING_PRODUCT(HttpStatus.BAD_REQUEST, "구매 불가능한 상품이 포함되어 있습니다."),
    NOT_USERS_ORDER(HttpStatus.BAD_REQUEST, "유저의 주문이 아닙니다."),
    PROCESSING_ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "처리 진행 중인 주문이 없습니다."),
    ITEM_DOES_NOT_EXIST(HttpStatus.BAD_REQUEST, "해당 아이템이 없습니다."),
    GRPC_COMMUNICATION_ERROR(HttpStatus.BAD_REQUEST, "grpc 통신 중 문제가 발생했습니다."),
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 주문이 없습니다."),
    MESSAGE_SEND_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "메세지 퍼블리싱에 문제가 발생했습니다."),
    REQUEST_ARGUMENT_NOT_VALID(HttpStatus.BAD_REQUEST, "요청 데이터의 형식을 확인해주세요.");
    public final HttpStatus httpStatus;
    public final String message;
}
