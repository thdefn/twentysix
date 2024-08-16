package cm.twentysix.order.dto;


import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record DeleteCartItemForm(
        @NotEmpty(message = "아이디를 입력해주세요.")
        List<String> productIds
) {
}
