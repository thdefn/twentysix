package cm.twentysix.user.service;

import cm.twentysix.user.controller.dto.LogInForm;
import cm.twentysix.user.controller.dto.TokenResponse;
import cm.twentysix.user.domain.model.User;
import cm.twentysix.user.domain.repository.UserRepository;
import cm.twentysix.user.exception.UserException;
import cm.twentysix.user.util.CipherManager;
import cm.twentysix.user.util.JwtTokenManager;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static cm.twentysix.user.exception.Error.EMAIL_NOT_FOUND;
import static cm.twentysix.user.exception.Error.WRONG_PASSWORD;

@Service
@RequiredArgsConstructor
public class LogInService {
    private final JwtTokenManager jwtTokenManager;
    private final UserRepository userRepository;
    private final CipherManager cipherManager;
    private final PasswordEncoder passwordEncoder;

    public TokenResponse login(LogInForm form) {
        String encryptedEmail = cipherManager.encrypt(form.email());
        User user = userRepository.findByEmail(encryptedEmail)
                .orElseThrow(() -> new UserException(EMAIL_NOT_FOUND));
        if (!passwordEncoder.matches(form.password(), user.getPassword()))
            throw new UserException(WRONG_PASSWORD);

        String accessToken = jwtTokenManager.makeAccessToken(user.getId());
        String refreshToken = jwtTokenManager.makeRefreshTokenAndSave(user.getId());

        return TokenResponse.of(accessToken, refreshToken);
    }
}
