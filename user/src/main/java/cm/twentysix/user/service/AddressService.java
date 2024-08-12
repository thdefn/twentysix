package cm.twentysix.user.service;

import cm.twentysix.user.dto.AddressSaveForm;
import cm.twentysix.user.domain.model.Address;
import cm.twentysix.user.domain.repository.AddressRepository;
import cm.twentysix.user.util.CipherManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AddressService {
    private final AddressRepository addressRepository;
    private final CipherManager cipherManager;

    @Transactional
    public void saveAddress(Long userId, AddressSaveForm form) {
        String encryptedAddress = cipherManager.encrypt(form.address());
        String encryptedName = cipherManager.encrypt(form.name());
        addressRepository.save(Address.of(form.alias(), encryptedName, form.zipCode(), encryptedAddress, userId));
    }

}
