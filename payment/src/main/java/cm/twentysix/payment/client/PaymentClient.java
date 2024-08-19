package cm.twentysix.payment.client;

import cm.twentysix.payment.config.PaymentConfig;
import cm.twentysix.payment.dto.PaymentCancelForm;
import cm.twentysix.payment.dto.PaymentResponse;
import cm.twentysix.payment.dto.PaymentForm;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment", url = "${payment.url}", configuration = PaymentConfig.class)
public interface PaymentClient {
    @PostMapping("/confirm")
    PaymentResponse confirm(@RequestBody PaymentForm form);

    @PostMapping("/{paymentKey}/cancel")
    String cancel(@PathVariable String paymentKey, @RequestBody PaymentCancelForm form);


}
