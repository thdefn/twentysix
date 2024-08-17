package cm.twentysix.payment.client;

import cm.twentysix.payment.config.PaymentConfig;
import cm.twentysix.payment.dto.PaymentForm;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment", url = "${payment.url}", configuration = PaymentConfig.class)
public interface PaymentClient {
    @PostMapping("/confirm")
    String confirm(@RequestBody PaymentForm form);
}
