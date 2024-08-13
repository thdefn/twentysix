package cm.twentysix.product.dto;

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
        boolean isUserLike

) {
}
