package cm.twentysix.user.domain.model;


import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@Builder
@RedisHash(value = "email-auths", timeToLive = 60 * 60 * 3)
public class EmailAuth {
    @Id
    private String email;
    private String code;
    private boolean isVerified;

    public void verify() {
        this.isVerified = true;
    }

    public static EmailAuth of(String email, String code) {
        return EmailAuth.builder()
                .email(email)
                .code(code)
                .isVerified(false)
                .build();
    }

}
