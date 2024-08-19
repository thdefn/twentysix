package cm.twentysix.payment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

@Builder
public record PaymentResponse(
        @JsonProperty("mId") String mId,
        @JsonProperty("lastTransactionKey") String lastTransactionKey,
        @JsonProperty("paymentKey") String paymentKey,
        @JsonProperty("orderId") String orderId,
        @JsonProperty("orderName") String orderName,
        @JsonProperty("taxExemptionAmount") int taxExemptionAmount,
        @JsonProperty("status") String status,
        @JsonProperty("requestedAt") String requestedAt,
        @JsonProperty("approvedAt") String approvedAt,
        @JsonProperty("useEscrow") boolean useEscrow,
        @JsonProperty("cultureExpense") boolean cultureExpense,
        @JsonProperty("card") String card,
        @JsonProperty("virtualAccount") String virtualAccount,
        @JsonProperty("transfer") String transfer,
        @JsonProperty("mobilePhone") String mobilePhone,
        @JsonProperty("giftCertificate") String giftCertificate,
        @JsonProperty("cashReceipt") String cashReceipt,
        @JsonProperty("cashReceipts") String cashReceipts,
        @JsonProperty("discount") String discount,
        @JsonProperty("cancels") String cancels,
        @JsonProperty("secret") String secret,
        @JsonProperty("type") String type,
        @JsonProperty("easyPay") EasyPay easyPay,
        @JsonProperty("country") String country,
        @JsonProperty("failure") String failure,
        @JsonProperty("isPartialCancelable") boolean isPartialCancelable,
        @JsonProperty("receipt") Receipt receipt,
        @JsonProperty("checkout") Checkout checkout,
        @JsonProperty("currency") String currency,
        @JsonProperty("totalAmount") int totalAmount,
        @JsonProperty("balanceAmount") int balanceAmount,
        @JsonProperty("suppliedAmount") int suppliedAmount,
        @JsonProperty("vat") int vat,
        @JsonProperty("taxFreeAmount") int taxFreeAmount,
        @JsonProperty("method") String method,
        @JsonProperty("version") String version
) {

    public record EasyPay(
            @JsonProperty("provider") String provider,
            @JsonProperty("amount") int amount,
            @JsonProperty("discountAmount") int discountAmount
    ) {
    }

    public record Receipt(
            String url
    ) {
    }

    public record Checkout(
            String url
    ) {
    }

    public LocalDateTime getRequestedAt() {
        return OffsetDateTime.parse(requestedAt, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toLocalDateTime();
    }

    public LocalDateTime getApprovedAt() {
        return OffsetDateTime.parse(approvedAt, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toLocalDateTime();
    }

    public String getEasyPay() {
        return easyPay.toString();
    }
}

