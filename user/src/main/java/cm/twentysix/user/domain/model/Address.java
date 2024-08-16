package cm.twentysix.user.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "addresses",
        indexes = {@Index(name = "idx_user_id_is_default", columnList = "user_id, is_default desc")})
public class Address extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 130)
    private String receiverName;

    @Column(columnDefinition = "bit(1) default 0")
    private boolean isDefault;

    @Column(length = 5, nullable = false)
    private String zipCode;

    @Column(length = 130, nullable = false)
    private String address;

    @Column(nullable = false)
    private Long userId;

    @Builder
    public Address(String receiverName, boolean isDefault, String zipCode, String address, Long userId) {
        this.receiverName = receiverName;
        this.isDefault = isDefault;
        this.zipCode = zipCode;
        this.address = address;
        this.userId = userId;
    }

    public static Address of(boolean isDefault, String receiverName, String zipCode, String address, Long userId) {
        return Address.builder()
                .isDefault(isDefault)
                .receiverName(receiverName)
                .zipCode(zipCode)
                .address(address)
                .userId(userId)
                .build();
    }

    public void turnOffDefault(){
        isDefault = false;
    }

    public void turnOnDefault(){
        isDefault = true;
    }
}
