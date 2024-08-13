package cm.twentysix.product.domain.repository.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record CategoryVo(
        @JsonProperty("_id")
        String id,
        String name,
        Integer depth,
        List<CategoryVo> parentCategories
) {
}
