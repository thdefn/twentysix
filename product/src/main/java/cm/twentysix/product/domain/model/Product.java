package cm.twentysix.product.domain.model;

import cm.twentysix.product.dto.BrandResponse;
import cm.twentysix.product.dto.CreateProductForm;
import cm.twentysix.product.dto.UpdateProductForm;
import cm.twentysix.product.service.dto.CategoryInfoDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(collection = "products")
public class Product extends BaseTimeDocument {
    @Id
    private String id;
    private String thumbnailPath;
    private String bodyImagePath;
    private Integer price;
    private Integer discount;
    private String name;
    private String information;
    private Integer amount;
    private Integer deliveryFee;
    private LocalDateTime lastModifiedAt;
    private List<CategoryInfo> categories;
    private List<Long> likes;
    private ProductBrand productBrand;
    private Long userId;

    @Builder
    public Product(String thumbnailPath, String bodyImagePath, Integer price, Integer discount, String name, String information, Integer amount, Integer deliveryFee, LocalDateTime lastModifiedAt, List<CategoryInfo> categories, List<Long> likes, ProductBrand productBrand, Long userId) {
        this.thumbnailPath = thumbnailPath;
        this.bodyImagePath = bodyImagePath;
        this.price = price;
        this.discount = discount;
        this.name = name;
        this.information = information;
        this.amount = amount;
        this.deliveryFee = deliveryFee;
        this.lastModifiedAt = lastModifiedAt;
        this.categories = categories;
        this.likes = likes;
        this.productBrand = productBrand;
        this.userId = userId;
    }

    public static Product of(CreateProductForm form, BrandResponse brand, Long userId, List<CategoryInfoDto> categoryInfoDtos) {
        return Product.builder()
                .price(form.price())
                .amount(form.amount())
                .name(form.name())
                .discount(form.discount())
                .information(form.information())
                .deliveryFee(form.deliveryFee())
                .lastModifiedAt(LocalDateTime.now())
                .likes(List.of())
                .productBrand(ProductBrand.from(brand))
                .categories(categoryInfoDtos.stream().map(CategoryInfo::from).collect(Collectors.toList()))
                .userId(userId)
                .build();
    }

    public void update(UpdateProductForm form, BrandResponse brand, Long userId, List<CategoryInfoDto> categoryInfoDtos) {
        this.price = form.price();
        this.discount = form.discount();
        this.name = form.name();
        this.information = form.information();
        this.amount = form.amount();
        this.deliveryFee = form.deliveryFee();
        this.lastModifiedAt = LocalDateTime.now();
        this.categories = categoryInfoDtos.stream().map(CategoryInfo::from).collect(Collectors.toList());
        this.productBrand = ProductBrand.from(brand);
        this.userId = userId;
    }
}
