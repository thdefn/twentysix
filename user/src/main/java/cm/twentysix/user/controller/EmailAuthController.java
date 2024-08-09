package cm.twentysix.user.controller;

import cm.twentysix.user.controller.dto.SendAuthEmailForm;
import cm.twentysix.user.service.EmailAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/email-auths")
public class EmailAuthController {
    private final EmailAuthService emailAuthService;

    @PostMapping
    public ResponseEntity<Void> sendAuthEmail(@RequestBody @Valid SendAuthEmailForm form) {
        emailAuthService.sendAuthEmail(form);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/verify")
    public ResponseEntity<Void> verifyEmail(@RequestParam String email, @RequestParam String code) {
        emailAuthService.verifyEmail(email, code);
        return ResponseEntity.ok().build();
    }


}
