package cm.twentysix.user.service;

import cm.twentysix.user.controller.dto.AddressSaveForm;
import cm.twentysix.user.controller.dto.SignUpForm;
import cm.twentysix.user.domain.model.EmailAuth;
import cm.twentysix.user.domain.model.User;
import cm.twentysix.user.domain.repository.EmailAuthRedisRepository;
import cm.twentysix.user.domain.repository.UserRepository;
import cm.twentysix.user.exception.EmailAuthException;
import cm.twentysix.user.exception.Error;
import cm.twentysix.user.exception.UserException;
import cm.twentysix.user.util.CipherManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SignUpServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private CipherManager cipherManager;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private EmailAuthRedisRepository emailAuthRedisRepository;
    @Mock
    private AddressService addressService;
    @InjectMocks
    private SignUpService signUpService;

    static User mockUser = mock(User.class);

    @BeforeAll
    static void init() {
        given(mockUser.getId()).willReturn(1L);
    }

    @Test
    @DisplayName("회원가입 성공")
    void signUp_success() {
        //given
        SignUpForm form = new SignUpForm("abcdegmail.com", "Qwerty!@", "01011111111", "송", "서울 특별시", "1234");
        given(cipherManager.encrypt(anyString())).willReturn("cipherManagerEncrypt");
        given(userRepository.existsByEmail(anyString())).willReturn(false);
        given(emailAuthRedisRepository.findById(anyString()))
                .willReturn(Optional.of(EmailAuth.builder()
                        .email("abcde@gmail.com")
                        .sessionId("anysessionId")
                        .code("longlongcode").isVerified(true).build()));
        given(passwordEncoder.encode(anyString())).willReturn("passwordEncoderEncrypt");
        given(userRepository.save(any())).willReturn(mockUser);
        //when
        signUpService.signUp(form, Optional.of("anysessionId"));
        //then
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertEquals(savedUser.getEmail(), "cipherManagerEncrypt");
        assertEquals(savedUser.getPhone(), "cipherManagerEncrypt");
        assertEquals(savedUser.getName(), "cipherManagerEncrypt");
        assertEquals(savedUser.getPassword(), "passwordEncoderEncrypt");

        ArgumentCaptor<AddressSaveForm> formCaptor = ArgumentCaptor.forClass(AddressSaveForm.class);
        verify(addressService, times(1)).saveAddress(anyLong(), formCaptor.capture());
        AddressSaveForm addressSaveForm = formCaptor.getValue();
        assertEquals(form.address(), addressSaveForm.address());
        assertEquals(form.name(), addressSaveForm.name());
        assertEquals(form.zipCode(), addressSaveForm.zipCode());
    }

    @Test
    @DisplayName("회원가입 실패_ALREADY_REGISTER_EMAIL")
    void signUp_fail_ALREADY_REGISTER_EMAIL() {
        //given
        SignUpForm form = new SignUpForm("abcdegmail.com", "Qwerty!@", "01011111111", "송", "서울 특별시", "1234");
        given(cipherManager.encrypt(anyString())).willReturn("cipherManagerEncrypt");
        given(userRepository.existsByEmail(anyString())).willReturn(true);
        //when
        UserException e = assertThrows(UserException.class, () -> signUpService.signUp(form, Optional.of("anysessionId")));
        //then
        assertEquals(Error.ALREADY_REGISTER_EMAIL, e.getError());
    }

    @Test
    @DisplayName("회원가입 실패_NOT_VERIFIED_EMAIL")
    void signUp_fail_NOT_VERIFIED_EMAIL() {
        //given
        SignUpForm form = new SignUpForm("abcdegmail.com", "Qwerty!@", "01011111111", "송", "서울 특별시", "1234");
        given(cipherManager.encrypt(anyString())).willReturn("cipherManagerEncrypt");
        given(userRepository.existsByEmail(anyString())).willReturn(false);
        given(emailAuthRedisRepository.findById(anyString()))
                .willReturn(Optional.of(EmailAuth.builder()
                        .email("abcde@gmail.com")
                        .sessionId("anysessionId")
                        .code("longlongcode").isVerified(false).build()));
        //when
        EmailAuthException e = assertThrows(EmailAuthException.class, () -> signUpService.signUp(form, Optional.of("anysessionId")));
        //then
        assertEquals(Error.NOT_VERIFIED_EMAIL, e.getError());
    }

    @Test
    @DisplayName("회원가입 실패_NOT_VERIFIED_EMAIL_notFound")
    void signUp_fail_NOT_VERIFIED_EMAIL_notFound() {
        //given
        SignUpForm form = new SignUpForm("abcdegmail.com", "Qwerty!@", "01011111111", "송", "서울 특별시", "1234");
        given(cipherManager.encrypt(anyString())).willReturn("cipherManagerEncrypt");
        given(userRepository.existsByEmail(anyString())).willReturn(false);
        given(emailAuthRedisRepository.findById(anyString()))
                .willReturn(Optional.empty());
        //when
        EmailAuthException e = assertThrows(EmailAuthException.class, () -> signUpService.signUp(form, Optional.of("anysessionId")));
        //then
        assertEquals(Error.NOT_VERIFIED_EMAIL, e.getError());
    }


}