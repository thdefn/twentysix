package cm.twentysix.product.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record UpdateProductForm(
        String categoryId,
        @Size(min = 1, max = 50, message = "이름은 50글자 이내, 1글자 이상입니다.")
        String name,
        @Size(min = 1, max = 50, message = "상호명은 50글자 이내, 1글자 이상입니다.")
        String information,
        @Min(value = 0, message = "가격은 0원 이상입니다.")
        @Max(value = 10000000, message = "가격은 1000만원 이하입니다.")
        Integer price,
        @Min(value = 0, message = "재고는 0개 이상입니다.")
        @Max(value = 1000, message = "재고는 1000개 이하입니다.")
        Integer amount,
        @Min(value = 0, message = "할인율은 0 퍼센트 이상입니다.")
        @Max(value = 100, message = "할인율은 100 퍼센트 이하입니다.")
        Integer discount,
        @Min(value = 0, message = "기본 배송비는 0원 이상입니다.")
        @Max(value = 100000, message = "기본 배송비는 10000원 이하입니다.")
        Integer deliveryFee,
        @Size(max = 200, message = "소개는 255글자 이내입니다.")
        String introduction
) {
}
