package cm.twentysix.product.domain.model;

import cm.twentysix.BrandProto;
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
import java.util.ArrayList;
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
    private ProductInfo productInfo;
    private Integer amount;
    private Integer deliveryFee;
    private LocalDateTime lastModifiedAt;
    private List<CategoryInfo> categories;
    private List<Long> likes;
    private ProductBrand productBrand;
    private Long userId;
    private boolean isDeleted;

    @Builder
    public Product(String thumbnailPath, String bodyImagePath, Integer price, Integer discount, String name, String information, Integer amount, Integer deliveryFee, LocalDateTime lastModifiedAt, List<CategoryInfo> categories, List<Long> likes, ProductBrand productBrand, Long userId, boolean isDeleted, ProductInfo productInfo) {
        this.thumbnailPath = thumbnailPath;
        this.bodyImagePath = bodyImagePath;
        this.price = price;
        this.discount = discount;
        this.name = name;
        this.productInfo = productInfo;
        this.amount = amount;
        this.deliveryFee = deliveryFee;
        this.lastModifiedAt = lastModifiedAt;
        this.categories = categories;
        this.likes = likes;
        this.productBrand = productBrand;
        this.userId = userId;
        this.isDeleted = isDeleted;
    }

    public static Product of(CreateProductForm form, BrandProto.BrandResponse brand, Long userId, List<CategoryInfoDto> categoryInfoDtos, String thumbnailPath, String bodyImagePath) {
        return Product.builder()
                .price(form.price())
                .amount(form.amount())
                .name(form.name())
                .discount(form.discount())
                .productInfo(ProductInfo.from(form.manufacturer(), form.countryOfManufacture(), form.contact()))
                .deliveryFee(form.deliveryFee())
                .lastModifiedAt(LocalDateTime.now())
                .likes(List.of())
                .isDeleted(false)
                .productBrand(ProductBrand.from(brand))
                .categories(categoryInfoDtos.stream().map(CategoryInfo::from).collect(Collectors.toList()))
                .userId(userId)
                .thumbnailPath(thumbnailPath)
                .bodyImagePath(bodyImagePath)
                .build();
    }

    public void update(UpdateProductForm form, BrandProto.BrandResponse brand, Long userId, List<CategoryInfoDto> categoryInfoDtos, String thumbnailPath, String bodyImagePath) {
        this.price = form.price();
        this.discount = form.discount();
        this.name = form.name();
        this.productInfo = ProductInfo.from(form.manufacturer(), form.countryOfManufacture(), form.contact());
        this.amount = form.amount();
        this.deliveryFee = form.deliveryFee();
        this.lastModifiedAt = LocalDateTime.now();
        this.categories = categoryInfoDtos.stream().map(CategoryInfo::from).collect(Collectors.toList());
        this.productBrand = ProductBrand.from(brand);
        this.userId = userId;
        this.thumbnailPath = thumbnailPath;
        this.bodyImagePath = bodyImagePath;
    }

    public void delete() {
        isDeleted = true;
        lastModifiedAt = LocalDateTime.now();
    }

    public List<String> getFilePaths() {
        List<String> filePaths = new ArrayList<>();
        filePaths.add(thumbnailPath);
        filePaths.add(bodyImagePath);
        return filePaths;
    }
}
