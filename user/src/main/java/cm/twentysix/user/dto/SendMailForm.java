package cm.twentysix.user.dto;

import cm.twentysix.user.constant.MailSender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class SendMailForm {
    private String from;
    private String to;
    private String subject;
    private String text;

    public static SendMailForm of(MailSender mailSender, String to, String subject, String text) {
        return SendMailForm.builder()
                .from(mailSender.getEmailFrom())
                .to(to)
                .subject(subject)
                .text(text)
                .build();

    }
}
