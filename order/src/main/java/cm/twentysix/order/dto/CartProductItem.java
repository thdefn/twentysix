package cm.twentysix.order.dto;

import cm.twentysix.ProductProto.ProductItemResponse;
import lombok.Builder;

@Builder
public record CartProductItem(
        String productId,
        String name,
        Integer quantity,
        Integer price,
        Integer discount,
        Integer discountedPrice,
        Integer totalPrice
) {
    public static CartProductItem from(ProductItemResponse response, Integer quantity) {
        return CartProductItem.builder()
                .name(response.getName())
                .productId(response.getId())
                .discount(response.getDiscount())
                .price(response.getPrice())
                .quantity(quantity)
                .discountedPrice(response.getDiscountedPrice())
                .totalPrice(response.getDiscountedPrice() * quantity)
                .build();
    }
}
