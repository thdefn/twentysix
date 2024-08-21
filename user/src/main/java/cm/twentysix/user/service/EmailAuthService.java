package cm.twentysix.user.service;

import cm.twentysix.user.client.MailgunClient;
import cm.twentysix.user.dto.SendMailForm;
import cm.twentysix.user.constant.MailContent;
import cm.twentysix.user.constant.MailSender;
import cm.twentysix.user.dto.SendAuthEmailForm;
import cm.twentysix.user.dto.SendAuthEmailResponse;
import cm.twentysix.user.domain.model.EmailAuth;
import cm.twentysix.user.domain.repository.EmailAuthRedisRepository;
import cm.twentysix.user.domain.repository.UserRepository;
import cm.twentysix.user.exception.EmailAuthException;
import cm.twentysix.user.exception.UserException;
import cm.twentysix.user.util.CipherManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static cm.twentysix.user.exception.Error.*;

@RequiredArgsConstructor
@Service
public class EmailAuthService {
    private final MailgunClient mailgunClient;
    private final EmailAuthRedisRepository emailAuthRepository;
    private final UserRepository userRepository;
    private final CipherManager cipherManager;

    public SendAuthEmailResponse sendAuthEmail(SendAuthEmailForm form, String serverUrl) {
        String encryptedEmail = cipherManager.encrypt(form.email());
        if (userRepository.existsByEmail(encryptedEmail))
            throw new UserException(ALREADY_REGISTER_EMAIL);

        String code = generateRandomCode();
        String authenticationLink = getAuthLink(form.email(), code, serverUrl);
        String emailBody = getEmailBody(authenticationLink);

        EmailAuth emailAuth = emailAuthRepository.save(EmailAuth.of(form.email(), code));

        mailgunClient.sendEmail(SendMailForm.of(MailSender.AUTH, form.email(), MailContent.EMAIL_VERIFY.title, emailBody));
        return new SendAuthEmailResponse(emailAuth.getSessionId());
    }

    private String getEmailBody(String link) {
        String body = MailContent.EMAIL_VERIFY.body;
        return body.replace("$1", link);
    }

    private String getAuthLink(String email, String code, String serverUrl) {
        StringBuilder sb = new StringBuilder(serverUrl);
        sb.append("/verify")
                .append("?email=").append(email)
                .append("&code=").append(code);
        return sb.toString();
    }

    private String generateRandomCode() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public void verifyEmail(String email, String code) {
        EmailAuth emailAuth = emailAuthRepository.findById(email)
                .orElseThrow(() -> new EmailAuthException(NOT_VALID_EMAIL));
        if (!emailAuth.getCode().equals(code))
            throw new EmailAuthException(EMAIL_VERIFY_CODE_UNMATCHED);
        if (emailAuth.isVerified())
            throw new EmailAuthException(ALREADY_VERIFIED);

        emailAuth.verify();
        emailAuthRepository.save(emailAuth);
    }
}
