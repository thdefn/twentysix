package cm.twentysix.product.dto;

import cm.twentysix.BrandProto;
import lombok.Builder;

@Builder
public record ProductBrandResponse(
        Long brandId,
        String name,
        String legalName,
        String thumbnail,
        String introduction,
        String registrationNumber,
        Integer freeDeliveryInfimum
) {
    public static ProductBrandResponse from(BrandProto.BrandDetailResponse brandDetailResponse) {
        return ProductBrandResponse.builder()
                .brandId(brandDetailResponse.getId())
                .name(brandDetailResponse.getName())
                .legalName(brandDetailResponse.getLegalName())
                .freeDeliveryInfimum(brandDetailResponse.getFreeDeliveryInfimum())
                .thumbnail(brandDetailResponse.getThumbnail())
                .introduction(brandDetailResponse.getIntroduction())
                .registrationNumber(brandDetailResponse.getRegistrationNumber())
                .build();
    }
}
