package cm.twentysix.user.controller.dto;

import jakarta.validation.constraints.Pattern;

public record LogInForm(
        @Pattern(regexp = "^[a-zA-Z0-9+-\\_.]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$", message = "이메일 형식이 아닙니다.")
        String email,
        @Pattern(regexp = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$ %^&*-]).{8,}$", message = "비밀번호는 8자리 이상의 소문자, 대문자, 숫자, 특수문자를 포함해야 합니다.")
        String password
) {
}
