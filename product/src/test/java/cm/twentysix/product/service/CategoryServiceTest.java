package cm.twentysix.product.service;

import cm.twentysix.product.domain.repository.CategoryAggregationRepository;
import cm.twentysix.product.domain.repository.vo.CategoryVo;
import cm.twentysix.product.dto.CategoryInfoDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {
    @Mock
    private CategoryAggregationRepository categoryAggregationRepository;
    @InjectMocks
    private CategoryService categoryService;

    @Test
    void retrieveBelongingCategories_success() {
        //given
        given(categoryAggregationRepository.findParentCategories(anyString()))
                .willReturn(List.of(
                        new CategoryVo("123", "로고 티셔츠", 3, List.of()),
                        new CategoryVo("456", "티셔츠", 2, List.of()),
                        new CategoryVo("789", "상의", 1, List.of()),
                        new CategoryVo("101", "여성", 0, List.of())));
        //when
        List<CategoryInfoDto> results = categoryService.retrieveBelongingCategories("123");
        //then
        assertEquals(results.size(), 4);
        CategoryInfoDto categoryInfoDto = results.get(0);
        assertEquals("123", categoryInfoDto.categoryId());
        assertEquals("로고 티셔츠", categoryInfoDto.name());
        categoryInfoDto = results.get(1);
        assertEquals("456", categoryInfoDto.categoryId());
        assertEquals("티셔츠", categoryInfoDto.name());
        categoryInfoDto = results.get(2);
        assertEquals("789", categoryInfoDto.categoryId());
        assertEquals("상의", categoryInfoDto.name());
        categoryInfoDto = results.get(3);
        assertEquals("101", categoryInfoDto.categoryId());
        assertEquals("여성", categoryInfoDto.name());

    }

}