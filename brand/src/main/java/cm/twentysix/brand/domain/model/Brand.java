package cm.twentysix.brand.domain.model;

import cm.twentysix.brand.dto.CreateBrandForm;
import cm.twentysix.brand.dto.UpdateBrandForm;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "brands",
        indexes = {@Index(name = "idx_user_id", columnList = "user_id")})
public class Brand extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false)
    private String name;

    @Column(length = 50, nullable = false)
    private String legalName;

    @Column
    private String thumbnail;

    @Column
    private String introduction;

    @Column(length = 12)
    private String registrationNumber;

    @Column
    private Integer deliveryFee;

    @Column
    private Integer freeDeliveryInfimum;

    @Column(nullable = false)
    private Long userId;

    @Builder
    public Brand(String name, String legalName, String thumbnail, String introduction, String registrationNumber, Integer deliveryFee, Integer freeDeliveryInfimum, Long userId) {
        this.name = name;
        this.legalName = legalName;
        this.thumbnail = thumbnail;
        this.introduction = introduction;
        this.registrationNumber = registrationNumber;
        this.deliveryFee = deliveryFee;
        this.freeDeliveryInfimum = freeDeliveryInfimum;
        this.userId = userId;
    }

    public static Brand from(CreateBrandForm form, Long userId, String thumbnail) {
        return Brand.builder()
                .name(form.name())
                .legalName(form.legalName())
                .introduction(form.introduction())
                .registrationNumber(form.registrationNumber())
                .deliveryFee(form.deliveryFee())
                .freeDeliveryInfimum(form.freeDeliveryInfimum())
                .thumbnail(thumbnail)
                .userId(userId)
                .build();
    }

    public void update(UpdateBrandForm form, String thumbnail) {
        this.name = form.name();
        this.legalName = form.legalName();
        this.introduction = form.introduction();
        this.deliveryFee = form.deliveryFee();
        this.freeDeliveryInfimum = form.freeDeliveryInfimum();
        this.thumbnail = thumbnail;
    }
}
