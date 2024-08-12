package cm.twentysix.user.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record AddressSaveForm(
        @Size(min = 1, max = 20, message = "별칭은 1자 이상 20자 이내 입니다.")
        String alias,
        @Size(min = 2, max = 30, message = "이름의 형식이 아닙니다.")
        String name,
        @Size(min = 8, max = 100, message = "주소의 형식이 아닙니다.")
        String address,
        @Pattern(regexp = "^[0-9]{5}$", message = "우편번호 형식이 아닙니다.")
        String zipCode
) {
    public static AddressSaveForm from(SignUpForm form) {
        return AddressSaveForm.builder()
                .name(form.name())
                .address(form.address())
                .zipCode(form.zipCode())
                .build();
    }
}
