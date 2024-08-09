package cm.twentysix.user.controller.dto;

import cm.twentysix.user.util.CookieUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record TokenResponse(
        String accessToken,
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) String refreshToken
) {
    public static TokenResponse of(String accessToken, String refreshToken) {
        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public String getRefreshTokenCookie() {
        return CookieUtil.makeCookie("refreshToken", refreshToken);
    }
}
