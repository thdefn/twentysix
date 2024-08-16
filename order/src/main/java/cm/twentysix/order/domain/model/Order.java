package cm.twentysix.order.domain.model;

import cm.twentysix.BrandProto.BrandInfo;
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

    @Type(JsonType.class)
    @Column(name = "delivery_fees", columnDefinition = "json")
    private Map<Long, Integer> deliveryFees;

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
    public Order(String orderId, Map<String, OrderProduct> products, Integer totalAmount, Integer totalDeliveryFee, OrderStatus status, OrderReceiver receiver, Long userId, Map<Long, Integer> deliveryFees) {
        this.orderId = orderId;
        this.products = products;
        this.totalAmount = totalAmount;
        this.totalDeliveryFee = totalDeliveryFee;
        this.status = status;
        this.receiver = receiver;
        this.userId = userId;
        this.deliveryFees = deliveryFees;
    }

    public static Order of(String orderId, Long userId, CreateOrderForm form) {
        return Order.builder()
                .orderId(orderId)
                .userId(userId)
                .products(new HashMap<>())
                .deliveryFees(new HashMap<>())
                .receiver(OrderReceiver.of(form.receiver()))
                .status(OrderStatus.CHECK_PENDING).build();
    }

    public void approve(Map<String, ProductOrderItem> orderedItem, Map<Long, BrandInfo> containedBrandInfo) {
        saveOrderProducts(orderedItem);
        calculateDeliveryFee(containedBrandInfo);
        this.totalAmount = products.values().stream().mapToInt(OrderProduct::getAmount).sum();
        this.totalDeliveryFee = deliveryFees.values().stream().mapToInt(Integer::intValue).sum();
        status = OrderStatus.PAYMENT_PENDING;
    }

    private void saveOrderProducts(Map<String, ProductOrderItem> orderedItem) {
        for (String productId : orderedItem.keySet()) {
            ProductOrderItem item = orderedItem.get(productId);
            products.put(productId, OrderProduct.from(item));
        }
    }

    private void calculateDeliveryFee(Map<Long, BrandInfo> containedBrandInfo) {
        Map<Long, Integer> brandIdAmountMap = new HashMap<>();
        for (OrderProduct p : products.values()) {
            brandIdAmountMap.put(p.getBrandId(), brandIdAmountMap.getOrDefault(p.getBrandId(), 0) + p.getAmount());
        }

        for (Long brandId : brandIdAmountMap.keySet()) {
            int brandTotalAmount = brandIdAmountMap.get(brandId);
            int brandFreeDeliveryInfimum = containedBrandInfo.get(brandId).getFreeDeliveryInfimum();
            if (brandTotalAmount >= brandFreeDeliveryInfimum)
                deliveryFees.put(brandId, 0);
            else
                deliveryFees.put(brandId, containedBrandInfo.get(brandId).getDeliveryFee());
        }
    }
}
