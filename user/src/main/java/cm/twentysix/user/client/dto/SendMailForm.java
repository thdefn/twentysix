package cm.twentysix.user.client.dto;

import cm.twentysix.user.constant.Sender;
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

    public static SendMailForm of(Sender sender, String to, String subject, String text) {
        return SendMailForm.builder()
                .from(sender.getEmailFrom())
                .to(to)
                .subject(subject)
                .text(text)
                .build();

    }
}
