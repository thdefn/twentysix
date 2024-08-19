package cm.twentysix.payment.domain.model;

import cm.twentysix.payment.dto.PaymentResponse;

public enum PaymentMethod {
    카드 {
        @Override
        public String getMethodDetail(PaymentResponse response) {
            return response.getCard();
        }
    },
    가상계좌 {
        @Override
        public String getMethodDetail(PaymentResponse response) {
            return response.virtualAccount();
        }
    },
    간편결제 {
        @Override
        public String getMethodDetail(PaymentResponse response) {
            return response.getEasyPay();
        }
    },
    휴대폰 {
        @Override
        public String getMethodDetail(PaymentResponse response) {
            return response.mobilePhone();
        }
    },
    계좌이체 {
        @Override
        public String getMethodDetail(PaymentResponse response) {
            return response.transfer();
        }
    };

    public abstract String getMethodDetail(PaymentResponse response);

}
