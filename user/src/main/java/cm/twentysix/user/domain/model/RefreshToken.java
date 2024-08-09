package cm.twentysix.user.domain.model;


import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@Builder
@RedisHash(value = "refresh-tokens")
public class RefreshToken {
    @Id
    private String tokenValue;
    @Indexed
    private Long userId;

    public static RefreshToken of(String tokenValue, Long userId) {
        return RefreshToken.builder()
                .tokenValue(tokenValue)
                .userId(userId)
                .build();
    }

}
