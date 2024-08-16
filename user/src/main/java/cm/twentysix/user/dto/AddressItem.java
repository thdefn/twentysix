package cm.twentysix.user.dto;

import cm.twentysix.user.domain.model.Address;
import lombok.Builder;

@Builder
public record AddressItem(
        Long id,
        boolean isDefault,
        String receiverName,
        String address,
        String zipCode
) {
    public static AddressItem of(Address address, String decryptedReceiverName, String decryptedAddress) {
        return AddressItem.builder()
                .id(address.getId())
                .address(decryptedAddress)
                .isDefault(address.isDefault())
                .receiverName(decryptedReceiverName)
                .zipCode(address.getZipCode())
                .build();
    }
}
