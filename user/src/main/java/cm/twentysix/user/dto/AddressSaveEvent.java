package cm.twentysix.user.dto;

import lombok.Builder;

@Builder
public record AddressSaveEvent(
        Long userId,
        boolean isDefault,
        String name,
        String address,
        String zipCode,
        String phone
) {
    public static AddressSaveEvent from(SignUpForm form, Long userId) {
        return AddressSaveEvent.builder()
                .name(form.name())
                .address(form.address())
                .zipCode(form.zipCode())
                .isDefault(true)
                .phone(form.phone())
                .userId(userId)
                .build();
    }
}
