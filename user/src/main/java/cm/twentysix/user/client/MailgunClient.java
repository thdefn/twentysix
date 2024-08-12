package cm.twentysix.user.client;

import cm.twentysix.user.dto.SendMailForm;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "mailgun", url = "${mailgun.url}")
@Qualifier(value = "mailgun")
public interface MailgunClient {
    @PostMapping("/messages")
    String sendEmail(@SpringQueryMap SendMailForm form);
}
