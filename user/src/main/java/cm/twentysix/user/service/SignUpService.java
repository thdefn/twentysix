package cm.twentysix.user.service;

import cm.twentysix.user.dto.AddressSaveForm;
import cm.twentysix.user.dto.SignUpForm;
import cm.twentysix.user.domain.model.EmailAuth;
import cm.twentysix.user.domain.model.User;
import cm.twentysix.user.domain.model.UserType;
import cm.twentysix.user.domain.repository.EmailAuthRedisRepository;
import cm.twentysix.user.domain.repository.UserRepository;
import cm.twentysix.user.exception.EmailAuthException;
import cm.twentysix.user.exception.UserException;
import cm.twentysix.user.util.CipherManager;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static cm.twentysix.user.exception.Error.*;

@Service
@RequiredArgsConstructor
public class SignUpService {
    private final UserRepository userRepository;
    private final CipherManager cipherManager;
    private final PasswordEncoder passwordEncoder;
    private final EmailAuthRedisRepository emailAuthRepository;
    private final AddressService addressService;

    @Transactional
    public void signUp(SignUpForm form, Optional<String> maybeSessionId) {
        if (maybeSessionId.isEmpty())
            throw new EmailAuthException(NOT_VALID_EMAIL);
        String encryptedEmail = cipherManager.encrypt(form.email());
        if (userRepository.existsByEmail(encryptedEmail))
            throw new UserException(ALREADY_REGISTER_EMAIL);
        emailAuthRepository.findById(form.email()).stream()
                .filter(EmailAuth::isVerified)
                .filter(emailAuth -> emailAuth.getSessionId().equals(maybeSessionId.get()))
                .findFirst()
                .orElseThrow(() -> new EmailAuthException(NOT_VERIFIED_EMAIL));

        String encryptedName = cipherManager.encrypt(form.name());
        String encryptedPhone = cipherManager.encrypt(form.phone());
        String encryptedPassword = passwordEncoder.encode(form.password());
        User savedUser = userRepository.save(User.of(encryptedEmail, encryptedPhone, encryptedName, encryptedPassword, UserType.valueOf(form.userType())));

        addressService.saveAddress(savedUser.getId(), AddressSaveForm.from(form));
    }
}
