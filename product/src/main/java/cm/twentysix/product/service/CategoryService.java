package cm.twentysix.product.service;

import cm.twentysix.product.domain.repository.CategoryAggregationRepository;
import cm.twentysix.product.service.dto.CategoryInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryAggregationRepository categoryAggregationRepository;

    public List<CategoryInfoDto> retrieveBelongingCategories(String categoryId) {
        return categoryAggregationRepository.findParentCategories(categoryId).stream()
                .map(CategoryInfoDto::from).collect(Collectors.toList());
    }

}
