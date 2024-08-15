package cm.twentysix.order.dto;


import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;

public record CreateOrderForm(
        @NotEmpty(message = "상품은 비어있을 수 없습니다.")
        @Valid
        List<ProductItem> products,
        boolean shouldSaveNewAddress,
        @Valid
        @NotNull(message = "수신지는 비어있을 수 없습니다.")
        Receiver receiver
) {
    public record Receiver(
            @Size(min = 2, max = 30, message = "이름의 형식이 아닙니다.")
            @NotBlank(message = "이름은 비어있을 수 없습니다.")
            String name,
            @Size(min = 8, max = 100, message = "주소의 형식이 아닙니다.")
            @NotBlank(message = "주소는 비어있을 수 없습니다.")
            String address,
            @Pattern(regexp = "^[0-9]{5}$", message = "우편번호 형식이 아닙니다.")
            @NotBlank(message = "우편번호는 비어있을 수 없습니다.")
            String zipCode,
            @Pattern(regexp = "^0\\d{1,2}-\\d{3,4}-\\d{4}$", message = "전화 번호 형식이 아닙니다.")
            @NotBlank(message = "전화 번호는 비어있을 수 없습니다.")
            String phone
    ) {

    }
}
