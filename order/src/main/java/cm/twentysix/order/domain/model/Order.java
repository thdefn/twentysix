package cm.twentysix.order.domain.model;

import cm.twentysix.order.dto.CreateOrderForm;
import cm.twentysix.order.dto.ProductOrderItem;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import java.util.HashMap;
import java.util.Map;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "orders",
        indexes = {@Index(name = "idx_user_id", columnList = "user_id")})
public class Order extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String orderId;

    @Type(JsonType.class)
    @Column(name = "products", columnDefinition = "json")
    private Map<String, OrderProduct> products;

    @Column
    private Integer totalAmount;

    @Column
    private Integer totalDeliveryFee;

    @Enumerated(EnumType.STRING)
    @Column(length = 15, nullable = false)
    private OrderStatus status;

    @Type(JsonType.class)
    @Column(name = "receiver", columnDefinition = "json")
    private OrderReceiver receiver;

    @Column(nullable = false)
    private Long userId;

    @Builder
    public Order(String orderId, Map<String, OrderProduct> products, Integer totalAmount, Integer totalDeliveryFee, OrderStatus status, OrderReceiver receiver, Long userId) {
        this.orderId = orderId;
        this.products = products;
        this.totalAmount = totalAmount;
        this.totalDeliveryFee = totalDeliveryFee;
        this.status = status;
        this.receiver = receiver;
        this.userId = userId;
    }

    public static Order of(String orderId, Long userId, CreateOrderForm form) {
        return Order.builder()
                .orderId(orderId)
                .userId(userId)
                .receiver(OrderReceiver.of(form.receiver()))
                .status(OrderStatus.PENDING).build();
    }

    public void approve(Map<String, ProductOrderItem> orderedItem) {
        products = new HashMap<>();
        int totalDeliveryFee = 0;
        int totalAmount = 0;
        for (String productId : orderedItem.keySet()) {
            ProductOrderItem item = orderedItem.get(productId);
            products.put(productId, OrderProduct.from(item));
            totalAmount += item.amount();
        }
        this.totalAmount = totalAmount;
    }
}
