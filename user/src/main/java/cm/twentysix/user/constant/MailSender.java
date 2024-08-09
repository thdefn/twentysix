package cm.twentysix.user.constant;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum MailSender {
    AUTH("26cm", "twentysix@email.com");
    public final String name;
    public final String email;

    public String getEmailFrom() {
        return this.name + " <" + this.email + ">";
    }
}
