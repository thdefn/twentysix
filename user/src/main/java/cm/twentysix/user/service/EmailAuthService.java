package cm.twentysix.user.service;

import cm.twentysix.user.client.MailgunClient;
import cm.twentysix.user.client.dto.SendMailForm;
import cm.twentysix.user.constant.MailContent;
import cm.twentysix.user.constant.MailSender;
import cm.twentysix.user.controller.dto.SendAuthEmailForm;
import cm.twentysix.user.domain.model.EmailAuth;
import cm.twentysix.user.domain.repository.EmailAuthRedisRepository;
import cm.twentysix.user.exception.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static cm.twentysix.user.exception.Error.*;

@RequiredArgsConstructor
@Service
public class EmailAuthService {
    @Value("${server.url}")
    private String serverUrl;
    private final MailgunClient mailgunClient;
    private final EmailAuthRedisRepository emailAuthRepository;

    public void sendAuthEmail(SendAuthEmailForm form) {
        // TODO: 해당 이메일이 이미 가입되었는지 체크
        String code = generateRandomCode();
        String authenticationLink = getAuthLink(form.email(), code);
        String emailBody = getEmailBody(authenticationLink);

        emailAuthRepository.save(EmailAuth.of(form.email(), code));

        mailgunClient.sendEmail(SendMailForm.of(MailSender.AUTH, form.email(), MailContent.EMAIL_VERIFY.title, emailBody));
    }

    private String getEmailBody(String link) {
        String body = MailContent.EMAIL_VERIFY.body;
        return body.replace("$1", link);
    }

    private String getAuthLink(String email, String code) {
        StringBuilder sb = new StringBuilder(serverUrl);
        sb.append("/email-auths/verify")
                .append("?email=").append(email)
                .append("&code=").append(code);
        return sb.toString();
    }

    private String generateRandomCode() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public void verifyEmail(String email, String code) {
        EmailAuth emailAuth = emailAuthRepository.findById(email)
                .orElseThrow(() -> new AuthException(NOT_VALID_EMAIL));
        if (!emailAuth.getCode().equals(code))
            throw new AuthException(EMAIL_VERIFY_CODE_UNMATCHED);
        if (emailAuth.isVerified())
            throw new AuthException(ALREADY_VERIFIED);

        emailAuth.verify();
        emailAuthRepository.save(emailAuth);
    }
}
