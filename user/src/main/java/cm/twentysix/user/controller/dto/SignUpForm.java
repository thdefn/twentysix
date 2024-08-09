package cm.twentysix.user.controller.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SignUpForm(
        @Pattern(regexp = "^[a-zA-Z0-9+-\\_.]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$", message = "이메일 형식이 아닙니다.")
        String email,
        @Pattern(regexp = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$ %^&*-]).{8,}$", message = "비밀번호는 8자리 이상의 소문자, 대문자, 숫자, 특수문자를 포함해야 합니다.")
        String password,
        @Pattern(regexp = "^0\\d{1,2}-\\d{3,4}-\\d{4}$", message = "전화 번호 형식이 아닙니다.")
        String phone,
        @Size(min = 2, max = 30, message = "이름의 형식이 아닙니다.")
        String name,
        @Size(min = 8, max = 100, message = "주소의 형식이 아닙니다.")
        String address,
        @Pattern(regexp = "^[0-9]{5}$", message = "우편번호 형식이 아닙니다.")
        String zipCode
) {
}
