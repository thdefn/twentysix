package cm.twentysix.order.dto;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record AddCartItemForm(
        @NotBlank(message = "아이디는 비어있을 수 없습니다.")
        String id,
        @Min(value = 1, message = "수량은 1개 이상입니다.")
        @Max(value = 1000, message = "수량은 1000개 이하입니다.")
        Integer quantity
) {
}
