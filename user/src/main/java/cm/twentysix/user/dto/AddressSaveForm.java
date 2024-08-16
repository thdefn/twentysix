package cm.twentysix.user.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record AddressSaveForm(
        boolean isDefault,
        @Size(min = 2, max = 30, message = "이름의 형식이 아닙니다.")
        String name,
        @Size(min = 8, max = 100, message = "주소의 형식이 아닙니다.")
        String address,
        @Pattern(regexp = "^[0-9]{5}$", message = "우편번호 형식이 아닙니다.")
        String zipCode,
        @Pattern(regexp = "^0\\d{1,2}-\\d{3,4}-\\d{4}$", message = "전화 번호 형식이 아닙니다.")
        String phone
) {
    public static AddressSaveForm from(SignUpForm form) {
        return AddressSaveForm.builder()
                .name(form.name())
                .address(form.address())
                .zipCode(form.zipCode())
                .isDefault(true)
                .phone(form.phone())
                .build();
    }
}
