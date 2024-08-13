package cm.twentysix.product.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateProductForm(
        @NotBlank(message = "카테고리 id는 비어 있을 수 없습니다.")
        String categoryId,
        @Size(min = 1, max = 50, message = "이름은 50글자 이내, 1글자 이상입니다.")
        String name,
        @Size(min = 1, max = 50, message = "제조자는 50글자 이내, 1글자 이상입니다.")
        String manufacturer,
        @Size(min = 1, max = 20, message = "제조국은 50글자 이내, 1글자 이상입니다.")
        String countryOfManufacture,
        @Size(min = 8, max = 50, message = "A/S 책임자와 전화번호는 50글자 이내, 1글자 이상입니다.")
        String contact,
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
        Integer deliveryFee
) {
}
