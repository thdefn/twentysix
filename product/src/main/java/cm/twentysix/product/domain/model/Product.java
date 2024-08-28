package cm.twentysix.product.domain.model;

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
import java.util.Set;
import java.util.stream.Collectors;

import static cm.twentysix.BrandProto.BrandDetailResponse;

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
    private Integer quantity;
    private Integer deliveryFee;
    private LocalDateTime lastModifiedAt;
    private List<CategoryInfo> categories;
    private Set<Long> likes;
    private ProductBrand productBrand;
    private Long userId;
    private boolean isDeleted;
    private LocalDateTime orderingOpensAt;

    @Builder
    public Product(String thumbnailPath, String bodyImagePath, Integer price, Integer discount, String name, Integer quantity, Integer deliveryFee, LocalDateTime lastModifiedAt, List<CategoryInfo> categories, Set<Long> likes, ProductBrand productBrand, Long userId, boolean isDeleted, ProductInfo productInfo, LocalDateTime orderingOpensAt) {
        this.thumbnailPath = thumbnailPath;
        this.bodyImagePath = bodyImagePath;
        this.price = price;
        this.discount = discount;
        this.name = name;
        this.productInfo = productInfo;
        this.quantity = quantity;
        this.deliveryFee = deliveryFee;
        this.lastModifiedAt = lastModifiedAt;
        this.categories = categories;
        this.likes = likes;
        this.productBrand = productBrand;
        this.userId = userId;
        this.isDeleted = isDeleted;
        this.orderingOpensAt = orderingOpensAt;
    }

    public static Product of(CreateProductForm form, BrandDetailResponse brand, Long userId, List<CategoryInfoDto> categoryInfoDtos, String thumbnailPath, String bodyImagePath) {
        return Product.builder()
                .price(form.price())
                .quantity(form.quantity())
                .name(form.name())
                .discount(form.discount())
                .productInfo(ProductInfo.from(form.manufacturer(), form.countryOfManufacture(), form.contact()))
                .deliveryFee(form.deliveryFee())
                .lastModifiedAt(LocalDateTime.now())
                .likes(Set.of())
                .isDeleted(false)
                .productBrand(ProductBrand.from(brand))
                .categories(categoryInfoDtos.stream().map(CategoryInfo::from).collect(Collectors.toList()))
                .userId(userId)
                .thumbnailPath(thumbnailPath)
                .bodyImagePath(bodyImagePath)
                .orderingOpensAt(form.parseOrderingOpensAt())
                .build();
    }

    public void update(UpdateProductForm form, BrandDetailResponse brand, Long userId, List<CategoryInfoDto> categoryInfoDtos, String thumbnailPath, String bodyImagePath) {
        this.price = form.price();
        this.discount = form.discount();
        this.name = form.name();
        this.productInfo = ProductInfo.from(form.manufacturer(), form.countryOfManufacture(), form.contact());
        this.quantity = form.quantity();
        this.deliveryFee = form.deliveryFee();
        this.lastModifiedAt = LocalDateTime.now();
        this.categories = categoryInfoDtos.stream().map(CategoryInfo::from).collect(Collectors.toList());
        this.productBrand = ProductBrand.from(brand);
        this.userId = userId;
        this.thumbnailPath = thumbnailPath;
        this.bodyImagePath = bodyImagePath;
        this.orderingOpensAt = form.parseOrderingOpensAt();
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

    public boolean isFreeDelivery() {
        return deliveryFee == 0;
    }

    public int getDiscountedPrice() {
        return price * (100 - discount) / 100;
    }

    public boolean isUserLike(Long userId) {
        return likes.contains(userId);
    }

    public long countOfLikes() {
        return likes.size();
    }

    public void minusQuantity(int requiredQuantity) {
        this.quantity -= requiredQuantity;
    }

    public void addQuantity(int quantityToAdded) {
        this.quantity += quantityToAdded;
    }

    public boolean isOpen() {
        return orderingOpensAt.isBefore(LocalDateTime.now());
    }
}
