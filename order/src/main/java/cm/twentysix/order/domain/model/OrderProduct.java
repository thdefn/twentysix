package cm.twentysix.order.domain.model;

import cm.twentysix.ProductProto.ProductItemResponse;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class OrderProduct implements Serializable {
    private String name;
    private Integer quantity;
    private String thumbnail;
    private Long brandId;
    private String brandName;
    private Integer amount;
    private OrderProductStatus status;

    @Builder
    public OrderProduct(String name, Integer quantity, String thumbnail, Long brandId, String brandName, Integer amount, OrderProductStatus status) {
        this.name = name;
        this.quantity = quantity;
        this.thumbnail = thumbnail;
        this.brandId = brandId;
        this.brandName = brandName;
        this.amount = amount;
        this.status = status;
    }

    public static OrderProduct of(ProductItemResponse product, int quantity) {
        return OrderProduct.builder()
                .name(product.getId())
                .quantity(quantity)
                .thumbnail(product.getThumbnail())
                .brandId(product.getBrandId())
                .brandName(product.getBrandName())
                .amount(product.getDiscountedPrice() * quantity)
                .status(OrderProductStatus.ORDER_PLACED)
                .build();
    }
}
