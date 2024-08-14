package cm.twentysix.product.dto;

import cm.twentysix.BrandProto;
import cm.twentysix.product.domain.model.CategoryInfo;
import cm.twentysix.product.domain.model.Product;
import lombok.Builder;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Builder
public record ProductResponse(
        String id,
        String thumbnailPath,
        String descriptionImagePath,
        Integer discountedPrice,
        Integer price,
        Integer discount,
        Integer deliveryFee,
        String name,
        boolean isFreeDelivery,
        boolean isUserLike,
        ProductInfoResponse info,
        ProductBrandResponse brand,
        List<String> categories
) {
    public static ProductResponse of(Product product, BrandProto.BrandDetailResponse brandDetailResponse, Optional<Long> optionalUserId) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .thumbnailPath(product.getThumbnailPath())
                .descriptionImagePath(product.getBodyImagePath())
                .discountedPrice(product.getDiscountedPrice())
                .price(product.getPrice())
                .discount(product.getDiscount())
                .deliveryFee(product.getDeliveryFee())
                .isFreeDelivery(product.isFreeDelivery())
                .isUserLike(product.isUserLike(optionalUserId.orElse(Long.MIN_VALUE)))
                .info(ProductInfoResponse.from(product.getProductInfo()))
                .brand(ProductBrandResponse.from(brandDetailResponse))
                .categories(product.getCategories().stream().map(CategoryInfo::getName).collect(Collectors.toList()))
                .build();
    }
}
