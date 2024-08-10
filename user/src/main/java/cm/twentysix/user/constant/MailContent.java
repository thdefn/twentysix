package cm.twentysix.user.constant;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum MailContent {
    EMAIL_VERIFY("[26cm] Verify your email", "Hi there,\nPlease click the link below if you agree to join our service. \n\n$1 \n\nIf you didn't expect this email, please unsubscribe. \nThanks, 26cm Team");
    public final String title;
    public final String body;
}
