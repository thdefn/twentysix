package cm.twentysix.payment.client;

import cm.twentysix.payment.config.PaymentClientConfig;
import cm.twentysix.payment.dto.PaymentCancelForm;
import cm.twentysix.payment.dto.PaymentForm;
import cm.twentysix.payment.dto.PaymentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Profile("!test")
@FeignClient(name = "payment", url = "${payment.url}", configuration = PaymentClientConfig.class)
public interface TossPaymentClient extends PaymentClient {
    @PostMapping("/confirm")
    PaymentResponse confirm(@RequestBody PaymentForm form);

    @PostMapping("/{paymentKey}/cancel")
    String cancel(@PathVariable String paymentKey, @RequestBody PaymentCancelForm form);


}
