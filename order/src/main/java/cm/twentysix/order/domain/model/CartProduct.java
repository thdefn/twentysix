package cm.twentysix.order.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

import static cm.twentysix.ProductProto.ProductItemResponse;

@Getter
public class CartProduct implements Serializable {
    private int quantity;
    private Long brandId;

    @Builder
    public CartProduct(int quantity, Long brandId) {
        this.quantity = quantity;
        this.brandId = brandId;
    }

    public static CartProduct of(ProductItemResponse response) {
        return CartProduct.builder()
                .brandId(response.getBrandId())
                .build();
    }

    public void addQuantity(int quantity) {
        this.quantity += quantity;
    }

    public void changeQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void decreaseQuantity(int quantity) {
        this.quantity -= quantity;
    }
}
