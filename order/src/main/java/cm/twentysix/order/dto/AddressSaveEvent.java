package cm.twentysix.order.dto;

import cm.twentysix.order.dto.CreateOrderForm.ReceiverForm;
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
    public static AddressSaveEvent from(ReceiverForm form, Long userId) {
        return AddressSaveEvent.builder()
                .userId(userId)
                .address(form.address())
                .isDefault(form.isDefault())
                .zipCode(form.zipCode())
                .name(form.name())
                .phone(form.phone())
                .build();
    }
}
