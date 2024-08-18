package cm.twentysix.payment.controller;

import cm.twentysix.payment.dto.PaymentForm;
import cm.twentysix.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public ResponseEntity<Void> confirm(@Valid @RequestBody PaymentForm form) {
        paymentService.confirm(form);
        return ResponseEntity.ok().build();
    }
}
