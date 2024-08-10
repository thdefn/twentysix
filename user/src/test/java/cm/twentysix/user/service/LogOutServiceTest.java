package cm.twentysix.user.service;

import cm.twentysix.user.exception.Error;
import cm.twentysix.user.exception.UserException;
import cm.twentysix.user.util.JwtTokenManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LogOutServiceTest {
    @Mock
    private JwtTokenManager jwtTokenManager;
    @InjectMocks
    private LogOutService logOutService;

    @Test
    @DisplayName("로그아웃 성공-SINGLE 타입일때")
    void logout_success_logoutTypeIsSingle() {
        //given
        Optional<String> maybeToken = Optional.of("anyTokenValue");
        String logoutType = "SINGLE";
        //when
        logOutService.logout(maybeToken, logoutType);
        //then
        verify(jwtTokenManager, times(1)).deleteRefreshToken(anyString());
        verify(jwtTokenManager, times(0)).deleteUsersAllRefreshToken(anyString());
    }

    @Test
    @DisplayName("로그아웃 성공-ALL 타입일때")
    void logout_success_logoutTypeIsAll() {
        //given
        Optional<String> maybeToken = Optional.of("anyTokenValue");
        String logoutType = "ALL";
        //when
        logOutService.logout(maybeToken, logoutType);
        //then
        verify(jwtTokenManager, times(0)).deleteRefreshToken(anyString());
        verify(jwtTokenManager, times(1)).deleteUsersAllRefreshToken(anyString());
    }


    @Test
    @DisplayName("로그아웃 실패-EMPTY_REFRESH_TOKEN")
    void logout_fail_EMPTY_REFRESH_TOKEN() {
        //given
        Optional<String> maybeToken = Optional.empty();
        String logoutType = "ALL";
        //when
        UserException e = assertThrows(UserException.class, () -> logOutService.logout(maybeToken, logoutType));
        //then
        assertEquals(e.getError(), Error.EMPTY_REFRESH_TOKEN);
    }


}