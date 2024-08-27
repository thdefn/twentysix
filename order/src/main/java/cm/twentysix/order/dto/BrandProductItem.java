package cm.twentysix.order.dto;

import cm.twentysix.order.domain.model.OrderProduct;
import lombok.Builder;

@Builder
public record BrandProductItem(
        String productId,
        String name,
        String thumbnail,
        Integer quantity,
        Integer amount,
        Long brandId,
        String brandName,
        Integer deliveryFee

) {
    public static BrandProductItem from(String productId, OrderProduct orderProduct) {
        return BrandProductItem.builder()
                .productId(productId)
                .name(orderProduct.getName())
                .thumbnail(orderProduct.getThumbnail())
                .quantity(orderProduct.getQuantity())
                .amount(orderProduct.getAmount())
                .brandName(orderProduct.getBrandName())
                .brandId(orderProduct.getBrandId())
                .build();
    }

}
