package cm.twentysix.payment.config;

import cm.twentysix.payment.client.MockPaymentClient;
import cm.twentysix.payment.client.PaymentClient;
import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Configuration
public class PaymentClientConfig {
    private final String token;

    public PaymentClientConfig(@Value("${payment.secret}") String secret) {
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((secret + ":").getBytes(StandardCharsets.UTF_8));
        token = "Basic " + new String(encodedBytes);
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            requestTemplate.header(HttpHeaders.AUTHORIZATION, token);
        };
    }

    @Bean
    @Profile("test")
    public PaymentClient mockPaymentClient() {
        return new MockPaymentClient();
    }

}
