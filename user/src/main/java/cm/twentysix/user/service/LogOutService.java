package cm.twentysix.user.service;

import cm.twentysix.user.controller.dto.LogOutType;
import cm.twentysix.user.exception.UserException;
import cm.twentysix.user.util.JwtTokenManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static cm.twentysix.user.exception.Error.EMPTY_REFRESH_TOKEN;

@Service
@RequiredArgsConstructor
public class LogOutService {
    private final JwtTokenManager jwtTokenManager;

    public void logout(Optional<String> maybeRefreshToken, String logoutType) {
        String refreshToken = maybeRefreshToken
                .orElseThrow(() -> new UserException(EMPTY_REFRESH_TOKEN));
        if (LogOutType.SINGLE.name().equals(logoutType))
            jwtTokenManager.deleteRefreshToken(refreshToken);
        else jwtTokenManager.deleteUsersAllRefreshToken(refreshToken);
    }
}
