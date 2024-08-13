package cm.twentysix.product.domain.repository;

import cm.twentysix.product.domain.repository.vo.CategoryVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GraphLookupOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
@RequiredArgsConstructor
public class CategoryAggregationRepository {
    private final MongoTemplate mongoTemplate;

    public List<CategoryVo> findParentCategories(String categoryId) {
        GraphLookupOperation graphLookupOperation = GraphLookupOperation.builder()
                .from("categories")
                .startWith("$parentId")
                .connectFrom("parentId")
                .connectTo("_id")
                .depthField("depth")
                .as("parentCategories");

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("_id").is(categoryId)), graphLookupOperation);
        AggregationResults<CategoryVo> results = mongoTemplate.aggregate(aggregation, "categories", CategoryVo.class);
        if (!results.getMappedResults().isEmpty()) {
            List<CategoryVo> belongings = new ArrayList<>(results.getMappedResults().get(0).parentCategories());
            belongings.addFirst(results.getMappedResults().get(0));
            return belongings;
        }
        return results.getMappedResults();
    }
}
