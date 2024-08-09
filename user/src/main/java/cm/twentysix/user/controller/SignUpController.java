package cm.twentysix.user.controller;

import cm.twentysix.user.controller.dto.SignUpForm;
import cm.twentysix.user.service.SignUpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/signup")
public class SignUpController {
    private final SignUpService signUpService;

    @PostMapping
    public ResponseEntity<Void> signUp(@Valid @RequestBody SignUpForm form) {
        signUpService.signUp(form);
        return ResponseEntity.ok().build();
    }

}
