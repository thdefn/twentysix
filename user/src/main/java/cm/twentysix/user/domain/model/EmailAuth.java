package cm.twentysix.user.domain.model;


import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.UUID;

@Getter
@Builder
@RedisHash(value = "email-auths", timeToLive = 60 * 60 * 30)
public class EmailAuth {
    @Id
    private String email;
    private String code;
    private boolean isVerified;
    private String sessionId;

    public void verify() {
        this.isVerified = true;
    }

    public static EmailAuth of(String email, String code) {
        return EmailAuth.builder()
                .email(email)
                .code(code)
                .isVerified(false)
                .sessionId(UUID.randomUUID().toString())
                .build();
    }

}
