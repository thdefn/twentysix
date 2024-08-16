package cm.twentysix.user.service;

import cm.twentysix.user.domain.model.Address;
import cm.twentysix.user.domain.repository.AddressRepository;
import cm.twentysix.user.dto.AddressItem;
import cm.twentysix.user.dto.AddressSaveForm;
import cm.twentysix.user.exception.AddressException;
import cm.twentysix.user.exception.Error;
import cm.twentysix.user.util.CipherManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {
    @Mock
    private AddressRepository addressRepository;
    @Mock
    private CipherManager cipherManager;
    @InjectMocks
    private AddressService addressService;

    static Address mockAddressA = mock(Address.class);
    static Address mockAddressB = mock(Address.class);

    @BeforeAll
    static void init() {
        given(mockAddressA.getId()).willReturn(1L);
        given(mockAddressA.getUserId()).willReturn(1L);
        given(mockAddressA.getAddress()).willReturn("encrypted-addressA");
        given(mockAddressA.getReceiverName()).willReturn("encrypted-nameA");
        given(mockAddressA.getPhone()).willReturn("encrypted-phoneA");
        given(mockAddressA.getZipCode()).willReturn("11111");
        given(mockAddressA.isDefault()).willReturn(true);

        given(mockAddressB.getId()).willReturn(2L);
        given(mockAddressB.getUserId()).willReturn(1L);
        given(mockAddressB.getAddress()).willReturn("encrypted-addressB");
        given(mockAddressB.getReceiverName()).willReturn("encrypted-nameB");
        given(mockAddressB.getPhone()).willReturn("encrypted-phoneB");
        given(mockAddressB.getZipCode()).willReturn("22222");
        given(mockAddressB.isDefault()).willReturn(false);
    }

    @Test
    void saveAddress_success() {
        //given
        AddressSaveForm form = new AddressSaveForm(true, "송송", "서울 특별시 보문로 23", "11111", "010-1111-1111");
        given(cipherManager.encrypt(anyString())).willReturn("cipherManagerEncrypted");
        //when
        addressService.saveAddress(1L, form);
        //then
        ArgumentCaptor<Address> addressCaptor = ArgumentCaptor.forClass(Address.class);
        verify(addressRepository, times(1)).save(addressCaptor.capture());
        Address saved = addressCaptor.getValue();
        assertEquals(saved.getAddress(), "cipherManagerEncrypted");
        assertEquals(saved.getReceiverName(), "cipherManagerEncrypted");
        assertEquals(saved.getPhone(), "cipherManagerEncrypted");
        assertEquals(saved.getZipCode(), form.zipCode());

    }

    @Test
    void retrieveDefaultAddress_success() {
        //given
        given(addressRepository.findByUserIdAndIsDefaultTrue(anyLong()))
                .willReturn(Optional.of(Address.builder()
                        .isDefault(true)
                        .receiverName("encryptedname")
                        .address("encryptedaddress")
                        .userId(1L)
                        .zipCode("11111")
                        .phone("encryptedphone")
                        .build()));
        given(cipherManager.decrypt(anyString())).willReturn("decrypted");
        //when
        AddressItem item = addressService.retrieveDefaultAddress(1L);
        //then
        assertEquals(item.address(), "decrypted");
        assertTrue(item.isDefault());
        assertEquals(item.zipCode(), "11111");
        assertEquals(item.receiverName(), "decrypted");
        assertEquals(item.phone(), "decrypted");
    }

    @Test
    void retrieveDefaultAddress_fail_ADDRESS_NOT_FOUND() {
        //given
        given(addressRepository.findByUserIdAndIsDefaultTrue(anyLong()))
                .willReturn(Optional.empty());
        //when
        AddressException e = assertThrows(AddressException.class, () -> addressService.retrieveDefaultAddress(1L));
        //then
        assertEquals(e.getError(), Error.ADDRESS_NOT_FOUND);
    }

    @Test
    void retrieveAllAddress_success() {
        //given
        List<Address> addresses = List.of(
                Address.builder()
                        .isDefault(true)
                        .receiverName("encryptedname1")
                        .address("encryptedaddress1")
                        .phone("encryptedphone1")
                        .userId(1L)
                        .zipCode("11111")
                        .build(),
                Address.builder()
                        .isDefault(false)
                        .receiverName("encryptedname2")
                        .address("encryptedaddress2")
                        .phone("encryptedphone2")
                        .userId(1L)
                        .zipCode("22222")
                        .build()
        );
        given(addressRepository.findByUserIdOrderByIsDefaultDesc(anyLong()))
                .willReturn(addresses);
        given(cipherManager.decrypt(anyString())).willReturn("decrypted");
        //when
        List<AddressItem> items = addressService.retrieveAllAddress(1L);
        //then
        assertEquals(items.size(), 2);
        AddressItem item = items.getFirst();
        assertEquals(item.address(), "decrypted");
        assertTrue(item.isDefault());
        assertEquals(item.zipCode(), "11111");
        assertEquals(item.receiverName(), "decrypted");
        assertEquals(item.receiverName(), "decrypted");

        AddressItem second = items.getLast();
        assertEquals(second.address(), "decrypted");
        assertFalse(second.isDefault());
        assertEquals(second.zipCode(), "22222");
        assertEquals(second.receiverName(), "decrypted");
        assertEquals(second.receiverName(), "decrypted");
    }

    @Test
    void deleteAddress_success() {
        //given
        Address anotherAddress = Address.builder()
                .isDefault(false)
                .receiverName("encryptedname2")
                .address("encryptedaddress2")
                .phone("encryptedphone2")
                .userId(1L)
                .zipCode("22222")
                .build();
        List<Address> addresses = List.of(mockAddressA, anotherAddress);
        given(addressRepository.findByUserIdOrderByIsDefaultDesc(anyLong()))
                .willReturn(addresses);
        //when
        addressService.deleteAddress(1L, 1L);
        //then
        verify(addressRepository, times(1)).delete(mockAddressA);
        assertTrue(anotherAddress.isDefault());
    }

    @Test
    void deleteAddress_fail_ADDRESS_NOT_FOUND() {
        //given
        List<Address> addresses = List.of(mockAddressA);
        given(addressRepository.findByUserIdOrderByIsDefaultDesc(anyLong()))
                .willReturn(addresses);
        //when
        AddressException e = assertThrows(AddressException.class, () -> addressService.deleteAddress(2L, 1L));
        //then
        assertEquals(Error.ADDRESS_NOT_FOUND, e.getError());
    }

    @Test
    void deleteAddress_fail_ONLY_ADDRESS() {
        //given
        List<Address> addresses = List.of(mockAddressA);
        given(addressRepository.findByUserIdOrderByIsDefaultDesc(anyLong()))
                .willReturn(addresses);
        //when
        AddressException e = assertThrows(AddressException.class, () -> addressService.deleteAddress(1L, 1L));
        //then
        assertEquals(Error.ONLY_ADDRESS, e.getError());
    }

    @Test
    void changeDefaultAddress_success() {
        //given
        Address anotherAddress = Address.builder()
                .isDefault(true)
                .receiverName("encryptedname2")
                .address("encryptedaddress2")
                .userId(1L)
                .phone("encryptedphone2")
                .zipCode("22222")
                .build();
        List<Address> addresses = List.of(anotherAddress, mockAddressB);
        given(addressRepository.findByUserIdOrderByIsDefaultDesc(anyLong()))
                .willReturn(addresses);
        //when
        addressService.changeDefaultAddress(2L, 1L);
        //then
        assertFalse(anotherAddress.isDefault());
        verify(mockAddressB, times(1)).turnOnDefault();
    }

    @Test
    void changeDefaultAddress_fail_ADDRESS_NOT_FOUND() {
        //given
        List<Address> addresses = List.of(mockAddressA);
        given(addressRepository.findByUserIdOrderByIsDefaultDesc(anyLong()))
                .willReturn(addresses);
        //when
        AddressException e = assertThrows(AddressException.class, () -> addressService.changeDefaultAddress(2L, 1L));
        //then
        assertEquals(Error.ADDRESS_NOT_FOUND, e.getError());
    }

    @Test
    void changeDefaultAddress_fail_ALREADY_DEFAULT_ADDRESS() {
        //given
        List<Address> addresses = List.of(mockAddressA);
        given(addressRepository.findByUserIdOrderByIsDefaultDesc(anyLong()))
                .willReturn(addresses);
        //when
        AddressException e = assertThrows(AddressException.class, () -> addressService.changeDefaultAddress(1L, 1L));
        //then
        assertEquals(Error.ALREADY_DEFAULT_ADDRESS, e.getError());
    }


}