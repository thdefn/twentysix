package cm.twentysix.user.controller;

import cm.twentysix.user.controller.dto.EmailVerifyForm;
import cm.twentysix.user.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/email")
    public ResponseEntity<?> verifyEmail(@RequestBody @Valid EmailVerifyForm form) {
        authService.verifyEmail(form);
        return ResponseEntity.ok().build();
    }
}
