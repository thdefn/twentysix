package cm.twentysix.user.dto;

import lombok.Builder;

@Builder
public record SendAuthEmailResponse(
        String sessionId
) {
}
