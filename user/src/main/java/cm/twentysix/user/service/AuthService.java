package cm.twentysix.user.service;

import cm.twentysix.user.client.MailgunClient;
import cm.twentysix.user.client.dto.SendMailForm;
import cm.twentysix.user.constant.Sender;
import cm.twentysix.user.controller.dto.EmailVerifyForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthService {
    private final MailgunClient mailgunClient;

    public void verifyEmail(EmailVerifyForm form){
        // TODO: 해당 이메일이 이미 가입되었는지 체크

        mailgunClient.sendEmail(SendMailForm.of(Sender.AUTH, form.email(), "Hello", "Testing some Mailgun awesomeness!"));
    }
}
