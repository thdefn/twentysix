package cm.twentysix.product.dto;

public record BrandResponse(
        Long id,
        String name,
        Integer freeDeliveryInfimum,
        Long userId
) {
}
