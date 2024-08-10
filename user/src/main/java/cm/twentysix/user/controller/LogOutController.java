package cm.twentysix.user.controller;

import cm.twentysix.user.controller.dto.LogOutType;
import cm.twentysix.user.service.LogOutService;
import cm.twentysix.user.util.validator.EnumValue;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/users/logout")
public class LogOutController {
    private final LogOutService logOutService;

    @GetMapping
    public ResponseEntity<Void> logout(@RequestParam(value = "type", defaultValue = "SINGLE") @EnumValue(enumClass = LogOutType.class) String logoutType,
                                       @CookieValue(value = "refreshToken") Cookie cookie) {
        logOutService.logout(Optional.ofNullable(cookie.getValue()), logoutType);
        return ResponseEntity.ok().build();
    }

}
