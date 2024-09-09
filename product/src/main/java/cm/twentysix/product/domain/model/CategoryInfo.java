package cm.twentysix.product.domain.model;

import cm.twentysix.product.dto.CategoryInfoDto;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CategoryInfo {
    private String id;
    private String name;

    @Builder
    public CategoryInfo(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public static CategoryInfo from(CategoryInfoDto dto) {
        return CategoryInfo.builder()
                .id(dto.categoryId())
                .name(dto.name())
                .build();
    }
}
