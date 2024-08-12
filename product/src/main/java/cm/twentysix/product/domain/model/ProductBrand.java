package cm.twentysix.product.domain.model;

import cm.twentysix.product.dto.BrandResponse;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ProductBrand {
    private Long id;
    private String name;
    private Integer freeDeliveryInfimum;

    @Builder
    public ProductBrand(Long id, String name, Integer freeDeliveryInfimum) {
        this.id = id;
        this.name = name;
        this.freeDeliveryInfimum = freeDeliveryInfimum;
    }

    public static ProductBrand from(BrandResponse brand) {
        return ProductBrand.builder()
                .id(brand.id())
                .name(brand.name())
                .freeDeliveryInfimum(brand.freeDeliveryInfimum())
                .build();
    }
}
