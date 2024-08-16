package cm.twentysix.order.dto;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record DeleteCartItemForm(
        @NotEmpty(message = "상품은 비어있을 수 없습니다.")
        @Size(min = 1, message = "상품은 한 개 이상입니다.")
        List<String> productIds
) {
}
