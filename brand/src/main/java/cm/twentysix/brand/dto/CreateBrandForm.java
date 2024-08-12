package cm.twentysix.brand.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateBrandForm(
        @Size(min = 1, max = 50, message = "이름은 50글자 이내, 1글자 이상입니다.")
        String name,
        @Size(min = 1, max = 50, message = "상호명은 50글자 이내, 1글자 이상입니다.")
        String legalName,
        @Pattern(regexp = "^\\d{3}-\\d{2}-\\d{5}$", message = "사업자등록번호 형식이 아닙니다.")
        String registrationNumber,
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
