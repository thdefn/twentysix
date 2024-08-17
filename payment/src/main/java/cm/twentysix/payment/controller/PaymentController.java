package cm.twentysix.payment.controller;

import cm.twentysix.payment.client.PaymentClient;
import cm.twentysix.payment.dto.PaymentForm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentClient paymentClient;

    @PostMapping(value = "/confirm")
    public void confirm(@Valid @RequestBody PaymentForm form) {
        paymentClient.confirm(form);
    }
}
