package cm.twentysix.payment.dto;

import cm.twentysix.payment.domain.model.PaymentMethod;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record PaymentConfirmResponse(
        LocalDateTime requestedAt,
        LocalDateTime createdAt,
        String method,
        @JsonProperty("card") String cardJson,
        @JsonProperty("virtualAccount") String virtualAccountJson,
        @JsonProperty("transfer") String transferJson,
        @JsonProperty("mobilePhone") String mobilePhoneJson,
        @JsonProperty("giftCertificate") String giftCertificateJson,
        Integer totalAmount

) {
    public PaymentMethod getMethod() {
        return PaymentMethod.valueOf(method);
    }
}
