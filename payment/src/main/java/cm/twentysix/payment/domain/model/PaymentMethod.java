package cm.twentysix.payment.domain.model;

public enum PaymentMethod {
    카드("card"),
    가상계좌("virtualAccount"),
    간편결제("transfer"),
    휴대폰("mobilePhone"),
    계좌이체("transfer");

    public final String responseField;

    PaymentMethod(String responseField) {
        this.responseField = responseField;
    }
}
