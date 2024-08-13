package cm.twentysix.product.dto;

public record ProductResponse(
        String id,
        String thumbnailPath,
        Integer price,
        Integer discount,
        String name,
        boolean isFreeDelivery,
        int countOfLikes,
        boolean isReaderLike
) {
}
