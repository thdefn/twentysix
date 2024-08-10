package cm.twentysix.brand.controller.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record UpdateBrandForm(
        @Size(min = 1, max = 50, message = "이름은 50글자 이내, 1글자 이상입니다.")
        String name,
        @Size(min = 1, max = 50, message = "상호명은 50글자 이내, 1글자 이상입니다.")
        String legalName,
        @Min(value = 0, message = "기본 배송비는 0원 이상입니다.")
        @Max(value = 100000, message = "기본 배송비는 10000원 이하입니다.")
        Integer deliveryFee,
        @Min(value = 0, message = "무료 배송 하한은 0원 이상입니다.")
        @Max(value = 10000000, message = "무료 배송 하한은 1000만원 이하입니다.")
        Integer freeDeliveryInfimum,
        @Size(max = 200, message = "상호명은 255글자 이내입니다.")
        String introduction

) {
}
