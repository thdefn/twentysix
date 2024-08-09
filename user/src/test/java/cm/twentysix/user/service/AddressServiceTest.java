package cm.twentysix.user.service;

import cm.twentysix.user.controller.dto.AddressSaveForm;
import cm.twentysix.user.domain.model.Address;
import cm.twentysix.user.domain.repository.AddressRepository;
import cm.twentysix.user.util.CipherManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {
    @Mock
    private AddressRepository addressRepository;
    @Mock
    private CipherManager cipherManager;
    @InjectMocks
    private AddressService addressService;

    @Test
    @DisplayName("주조 저장 성공")
    void saveAddress_success() {
        //given
        AddressSaveForm form = new AddressSaveForm("강아지", "송송", "서울 특별시 보문로 23", "11111");
        given(cipherManager.encrypt(anyString())).willReturn("cipherManagerEncrypted");
        //when
        addressService.saveAddress(1L, form);
        //then
        ArgumentCaptor<Address> addressCaptor = ArgumentCaptor.forClass(Address.class);
        verify(addressRepository, times(1)).save(addressCaptor.capture());
        Address saved = addressCaptor.getValue();
        assertEquals(saved.getAddress(), "cipherManagerEncrypted");
        assertEquals(saved.getReceiverName(), "cipherManagerEncrypted");
        assertEquals(saved.getAlias(), form.alias());
        assertEquals(saved.getZipCode(), form.zipCode());

    }
}