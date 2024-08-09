package cm.twentysix.user.service;

import cm.twentysix.user.controller.dto.SignUpForm;
import cm.twentysix.user.domain.model.User;
import cm.twentysix.user.domain.repository.UserRepository;
import cm.twentysix.user.exception.UserException;
import cm.twentysix.user.util.CypherManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static cm.twentysix.user.exception.Error.ALREADY_REGISTER_EMAIL;

@Service
@RequiredArgsConstructor
public class SignUpService {
    private final UserRepository userRepository;
    private final CypherManager cypherManager;

    public void signUp(SignUpForm form) {
        if (userRepository.findByEmail(form.email()))
            throw new UserException(ALREADY_REGISTER_EMAIL);

        String encryptedEmail = cypherManager.encrypt(form.email());
        String encryptedName = cypherManager.encrypt(form.name());
        String encryptedPhone = cypherManager.encrypt(form.phone());
        // TODO : 배송지로 따로 저장
        String encryptedAddress = cypherManager.encrypt(form.address());
        String encryptedZipCode = cypherManager.encrypt(form.zipCode());

        // TODO : 패스워드 단방향 암호화 및 저장
        userRepository.save(User.of(encryptedEmail, encryptedPhone, encryptedName, form.password()));
    }
}
