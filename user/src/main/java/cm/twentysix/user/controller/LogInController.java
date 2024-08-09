package cm.twentysix.user.controller;

import cm.twentysix.user.controller.dto.LogInForm;
import cm.twentysix.user.controller.dto.TokenResponse;
import cm.twentysix.user.service.LogInService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/login")
public class LogInController {
    private final LogInService logInService;

    @PostMapping
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LogInForm form) {
        TokenResponse tokenResponse = logInService.login(form);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, tokenResponse.getRefreshTokenCookie())
                .body(tokenResponse);
    }

}
