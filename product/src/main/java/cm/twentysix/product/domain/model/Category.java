package cm.twentysix.product.domain.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(collection = "categories")
public class Category {
    @Id
    private String id;
    private String name;
    private List<CategoryInfo> children;
    private String parentId;
}
