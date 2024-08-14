package cm.twentysix.product.dto;

import cm.twentysix.product.domain.model.Product;
import lombok.Builder;

@Builder
public record ProductOrderItem(
        String name,
        String thumbnail,
        Integer quantity,
        Integer amount,
        Long brandId,
        String brandName,
        Integer deliveryFee

) {
    public static ProductOrderItem from(Product product, int quantity) {
        return ProductOrderItem.builder()
                .name(product.getName())
                .thumbnail(product.getThumbnailPath())
                .quantity(quantity)
                .brandId(product.getProductBrand().getId())
                .brandName(product.getProductBrand().getName())
                .amount(product.getDiscountedPrice() * quantity)
                .deliveryFee(product.getDeliveryFee())
                .build();
    }


}
