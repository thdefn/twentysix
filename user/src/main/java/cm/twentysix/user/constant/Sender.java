package cm.twentysix.user.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Sender {
    AUTH("twentysix", "twentysix@email.com");
    private final String name;
    private final String email;

    public String getEmailFrom() {
        return this.name + " <" + this.email + ">";
    }
}
