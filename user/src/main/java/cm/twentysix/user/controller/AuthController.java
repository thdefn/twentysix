package cm.twentysix.user.controller;

import cm.twentysix.user.controller.dto.SendAuthEmailForm;
import cm.twentysix.user.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/email")
    public ResponseEntity<Void> sendAuthEmail(@RequestBody @Valid SendAuthEmailForm form) {
        authService.sendAuthEmail(form);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/email/verify")
    public ResponseEntity<Void> verifyEmail(@RequestParam String email, @RequestParam String code) {
        authService.verifyEmail(email, code);
        return ResponseEntity.ok().build();
    }


}
