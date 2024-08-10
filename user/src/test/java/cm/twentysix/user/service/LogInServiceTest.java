package cm.twentysix.user.service;

import cm.twentysix.user.controller.dto.LogInForm;
import cm.twentysix.user.controller.dto.TokenResponse;
import cm.twentysix.user.domain.model.User;
import cm.twentysix.user.domain.model.UserType;
import cm.twentysix.user.domain.repository.UserRepository;
import cm.twentysix.user.exception.Error;
import cm.twentysix.user.exception.UserException;
import cm.twentysix.user.util.CipherManager;
import cm.twentysix.user.util.JwtTokenManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class LogInServiceTest {
    @Mock
    private JwtTokenManager jwtTokenManager;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CipherManager cipherManager;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private LogInService logInService;

    static User mockUser = mock(User.class);

    @BeforeAll
    static void init() {
        given(mockUser.getId()).willReturn(1L);
        given(mockUser.getType()).willReturn(UserType.CUSTOMER);
        given(mockUser.getPassword()).willReturn("password");
    }

    @Test
    @DisplayName("로그인 성공")
    void login_success() {
        //given
        LogInForm form = new LogInForm("abcde@gmail.com", "Qwerty!@1");
        given(cipherManager.encrypt(anyString())).willReturn("encrypted");
        given(userRepository.findByEmail(anyString()))
                .willReturn(Optional.of(mockUser));
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);
        given(jwtTokenManager.makeAccessToken(anyLong(), anyString())).willReturn("accesstoken");
        given(jwtTokenManager.makeRefreshTokenAndSave(anyLong(), anyString())).willReturn("refreshtoken");
        //when
        TokenResponse tokenResponse = logInService.login(form);
        //then
        assertEquals(tokenResponse.refreshToken(), "refreshtoken");
        assertEquals(tokenResponse.accessToken(), "accesstoken");
    }

    @Test
    @DisplayName("로그인 실패_EMAIL_NOT_FOUND")
    void login_fail_EMAIL_NOT_FOUND() {
        //given
        LogInForm form = new LogInForm("abcde@gmail.com", "Qwerty!@1");
        given(cipherManager.encrypt(anyString())).willReturn("encrypted");
        given(userRepository.findByEmail(anyString()))
                .willReturn(Optional.empty());
        //when
        UserException e = assertThrows(UserException.class, () -> logInService.login(form));
        //then
        assertEquals(Error.EMAIL_NOT_FOUND, e.getError());
    }

    @Test
    @DisplayName("로그인 실패_WRONG_PASSWORD")
    void login_fail_WRONG_PASSWORD() {
        //given
        LogInForm form = new LogInForm("abcde@gmail.com", "Qwerty!@1");
        given(cipherManager.encrypt(anyString())).willReturn("encrypted");
        given(userRepository.findByEmail(anyString()))
                .willReturn(Optional.of(mockUser));
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(false);
        //when
        UserException e = assertThrows(UserException.class, () -> logInService.login(form));
        //then
        assertEquals(Error.WRONG_PASSWORD, e.getError());
    }

}