package cm.twentysix.product.domain.model;

import cm.twentysix.product.dto.BrandResponse;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ProductBrand {
    private Long id;
    private String name;

    @Builder
    public ProductBrand(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static ProductBrand from(BrandResponse brand) {
        return ProductBrand.builder()
                .id(brand.id())
                .name(brand.name())
                .build();
    }
}
