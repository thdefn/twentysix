package cm.twentysix.order.domain.model;

import cm.twentysix.order.dto.CreateOrderForm;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class OrderReceiver implements Serializable {
    private String name;
    private String address;
    private String zipCode;
    private String phone;

    @Builder
    public OrderReceiver(String name, String address, String zipCode, String phone) {
        this.name = name;
        this.address = address;
        this.zipCode = zipCode;
        this.phone = phone;
    }

    public static OrderReceiver of(CreateOrderForm.ReceiverForm receiver) {
        return OrderReceiver.builder()
                .address(receiver.address())
                .name(receiver.name())
                .zipCode(receiver.zipCode())
                .phone(receiver.phone())
                .build();
    }
}
