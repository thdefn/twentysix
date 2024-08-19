package cm.twentysix.payment.config;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Configuration
public class PaymentConfig {
    private final String token;
    public PaymentConfig(@Value("${payment.secret}") String secret) {
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
}
