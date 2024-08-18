package cm.twentysix.payment.controller;

import cm.twentysix.payment.dto.PaymentForm;
import cm.twentysix.payment.exception.Error;
import cm.twentysix.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping(value = "/confirm")
    public ResponseEntity<?> confirm(@Valid @RequestBody PaymentForm form) {
        boolean shouldRedirect = !paymentService.confirmOrStop(form);
        if (shouldRedirect)
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .header(HttpHeaders.LOCATION, "/fail?code=" + Error.STOCK_SHORTAGE +
                            "?message=" + Error.STOCK_SHORTAGE.message + "?orderId=" + form.orderId()).build();
        return ResponseEntity.ok().build();

    }
}
