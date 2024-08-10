package cm.twentysix.user.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "users")
public class User extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 130)
    private String email;

    @Column(nullable = false, length = 130)
    private String phone;

    @Column(nullable = false, length = 130)
    private String name;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 8)
    @Enumerated(value = EnumType.STRING)
    private UserType type;

    @Builder
    public User(String email, String phone, String name, String password, UserType type) {
        this.email = email;
        this.phone = phone;
        this.name = name;
        this.password = password;
        this.type = type;
    }

    public static User of(String email, String phone, String name, String password, UserType type) {
        return User.builder()
                .phone(phone)
                .email(email)
                .name(name)
                .password(password)
                .type(type)
                .build();
    }
}
