package cm.twentysix.user.service;

import cm.twentysix.user.domain.model.Address;
import cm.twentysix.user.domain.repository.AddressRepository;
import cm.twentysix.user.dto.AddressItem;
import cm.twentysix.user.dto.AddressSaveEvent;
import cm.twentysix.user.exception.AddressException;
import cm.twentysix.user.exception.Error;
import cm.twentysix.user.util.CipherManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class AddressService {
    private final AddressRepository addressRepository;
    private final CipherManager cipherManager;

    @Transactional
    public void saveAddress(AddressSaveEvent form) {
        String encryptedAddress = cipherManager.encrypt(form.address());
        String encryptedName = cipherManager.encrypt(form.name());
        String encryptedPhone = cipherManager.encrypt(form.phone());
        if (form.isDefault()) {
            addressRepository.findByUserIdAndIsDefaultTrue(form.userId())
                    .ifPresent(Address::turnOffDefault);
        }
        addressRepository.save(Address.of(form.isDefault(), encryptedName, form.zipCode(), encryptedAddress, form.userId(), encryptedPhone));
    }

    public AddressItem retrieveDefaultAddress(Long userId) {
        Address address = addressRepository.findByUserIdAndIsDefaultTrue(userId)
                .orElseThrow(() -> new AddressException(Error.ADDRESS_NOT_FOUND));

        String decryptedAddress = cipherManager.decrypt(address.getAddress());
        String decryptedName = cipherManager.decrypt(address.getReceiverName());
        String decryptedPhone = cipherManager.decrypt(address.getPhone());

        return AddressItem.of(address, decryptedName, decryptedAddress, decryptedPhone);
    }

    public List<AddressItem> retrieveAllAddress(Long userId) {
        return addressRepository.findByUserIdOrderByIsDefaultDesc(userId)
                .stream().map(address -> {
                    String decryptedAddress = cipherManager.decrypt(address.getAddress());
                    String decryptedName = cipherManager.decrypt(address.getReceiverName());
                    String decryptedPhone = cipherManager.decrypt(address.getPhone());
                    return AddressItem.of(address, decryptedName, decryptedAddress, decryptedPhone);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteAddress(Long id, Long userId) {
        List<Address> addresses = addressRepository.findByUserIdOrderByIsDefaultDesc(userId);
        Address address = addresses.stream()
                .filter(ad -> id.equals(ad.getId()))
                .findFirst()
                .orElseThrow(() -> new AddressException(Error.ADDRESS_NOT_FOUND));
        if (addresses.size() == 1)
            throw new AddressException(Error.ONLY_ADDRESS);

        if (address.isDefault())
            addresses.get(1).turnOnDefault();
        addressRepository.delete(address);
    }

    @Transactional
    public void changeDefaultAddress(Long id, Long userId) {
        List<Address> addresses = addressRepository.findByUserIdOrderByIsDefaultDesc(userId);
        Address address = addresses.stream()
                .filter(ad -> id.equals(ad.getId()))
                .findFirst()
                .orElseThrow(() -> new AddressException(Error.ADDRESS_NOT_FOUND));
        if (address.isDefault())
            throw new AddressException(Error.ALREADY_DEFAULT_ADDRESS);


        addresses.getFirst().turnOffDefault();
        address.turnOnDefault();
    }

}
