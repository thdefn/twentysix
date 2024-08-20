package cm.twentysix.product.dto;

import cm.twentysix.product.domain.model.Product;
import lombok.Builder;

import java.util.Optional;

@Builder
public record ProductItem(
        String id,
        String thumbnailPath,
        Integer price,
        Integer discount,
        String name,
        boolean isFreeDelivery,
        Long countOfLikes,
        boolean isUserLike,
        String brandName,
        Long brandId,
        boolean isOpen

) {
    public static ProductItem from(Product product, Optional<Long> optionalUserId) {
        return ProductItem.builder()
                .id(product.getId())
                .thumbnailPath(product.getThumbnailPath())
                .price(product.getPrice())
                .discount(product.getDiscount())
                .price(product.getDiscountedPrice())
                .name(product.getName())
                .isFreeDelivery(product.isFreeDelivery())
                .discount(product.getDiscount())
                .brandName(product.getProductBrand().getName())
                .brandId(product.getProductBrand().getId())
                .countOfLikes(product.countOfLikes())
                .isUserLike(product.isUserLike(optionalUserId.orElse(Long.MIN_VALUE)))
                .isOpen(product.isOpen())
                .build();
    }


}
