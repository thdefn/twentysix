package cm.twentysix.product.dto;

import cm.twentysix.product.domain.model.ProductInfo;
import lombok.Builder;

@Builder
public record ProductInfoResponse(
        String manufacturer,
        String countryOfManufacture,
        String contact
) {
    public static ProductInfoResponse from(ProductInfo productInfo) {
        return ProductInfoResponse.builder()
                .manufacturer(productInfo.getManufacturer())
                .countryOfManufacture(productInfo.getCountryOfManufacture())
                .contact(productInfo.getContact())
                .build();
    }
}
