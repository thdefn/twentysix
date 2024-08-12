package cm.twentysix.user.dto;

import jakarta.validation.constraints.Pattern;

public record SendAuthEmailForm(
        @Pattern(regexp = "^[a-zA-Z0-9+-\\_.]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$", message = "이메일 형식이 아닙니다.")
        String email
) {
}
