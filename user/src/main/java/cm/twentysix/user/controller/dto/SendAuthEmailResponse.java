package cm.twentysix.user.controller.dto;

import lombok.Builder;

@Builder
public record SendAuthEmailResponse(
        String sessionId
) {
}
