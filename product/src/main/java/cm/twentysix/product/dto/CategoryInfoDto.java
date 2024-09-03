package cm.twentysix.product.dto;

import cm.twentysix.product.domain.repository.vo.CategoryVo;
import lombok.Builder;

@Builder
public record CategoryInfoDto(
        String categoryId,
        String name

) {
    public static CategoryInfoDto from(CategoryVo category) {
        return CategoryInfoDto.builder()
                .categoryId(category.id())
                .name(category.name())
                .build();
    }
}
