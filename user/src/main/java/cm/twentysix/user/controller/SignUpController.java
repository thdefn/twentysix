package cm.twentysix.user.controller;

import cm.twentysix.user.controller.dto.SignUpForm;
import cm.twentysix.user.service.SignUpService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/signup")
public class SignUpController {
    private final SignUpService signUpService;

    @PostMapping
    public ResponseEntity<Void> signUp(@Valid @RequestBody SignUpForm form, HttpServletRequest request) {
        String sessionId = (String) request.getSession().getAttribute("SESSION_ID");
        signUpService.signUp(form, Optional.ofNullable(sessionId));
        return ResponseEntity.ok().build();
    }

}
