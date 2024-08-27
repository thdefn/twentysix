package cm.twentysix.order.domain.model;

import cm.twentysix.BrandProto.BrandInfo;
import cm.twentysix.ProductProto.ProductItemResponse;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cm.twentysix.order.dto.CreateOrderForm.ReceiverForm;

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


    public static Order of(String orderId, List<ProductItemResponse> containedProducts,
                           Map<String, Integer> productIdQuantityMap, ReceiverForm receiver, Long userId) {
        return Order.builder()
                .orderId(orderId)
                .userId(userId)
                .products(containedProducts.stream()
                        .collect(Collectors.toMap(ProductItemResponse::getId,
                                product -> OrderProduct.of(product, productIdQuantityMap.get(product.getId())))))
                .status(OrderStatus.PAYMENT_PENDING)
                .deliveryFees(new HashMap<>())
                .receiver(OrderReceiver.from(receiver))
                .build();
    }

    public void settlePayment(Map<Long, BrandInfo> containedBrands) {
        totalAmount = calculateTotalAmount();
        totalDeliveryFee = calculateDeliveryFee(containedBrands);
    }

    public int getPaymentAmount() {
        return totalAmount + totalDeliveryFee;
    }

    private int calculateTotalAmount() {
        return products.values().stream().mapToInt(OrderProduct::getAmount).sum();
    }

    private int calculateDeliveryFee(Map<Long, BrandInfo> containedBrands) {
        Map<Long, Integer> brandIdAmountMap = new HashMap<>();
        for (OrderProduct p : products.values()) {
            brandIdAmountMap.put(p.getBrandId(), brandIdAmountMap.getOrDefault(p.getBrandId(), 0) + p.getAmount());
        }

        for (Long brandId : brandIdAmountMap.keySet()) {
            int brandTotalAmount = brandIdAmountMap.get(brandId);
            int brandFreeDeliveryInfimum = containedBrands.get(brandId).getFreeDeliveryInfimum();
            if (brandTotalAmount >= brandFreeDeliveryInfimum)
                deliveryFees.put(brandId, 0);
            else
                deliveryFees.put(brandId, containedBrands.get(brandId).getDeliveryFee());
        }
        return deliveryFees.values().stream().mapToInt(Integer::intValue).sum();
    }

    public String getOrderName() {
        List<OrderProduct> orderProducts = new ArrayList<>(products.values());
        if (orderProducts.size() > 1)
            return orderProducts.getFirst().getName() + " 외" + (products.size() - 1) + "건";
        return orderProducts.getFirst().getName();
    }

    public void checkFail() {
        status = OrderStatus.CHECK_FAIL;
    }

    public void paymentFail() {
        status = OrderStatus.PAYMENT_FAIL;
    }

    public void placed() {
        status = OrderStatus.ORDER_PLACED;
    }

    public void cancel() {
        status = OrderStatus.CANCEL;
    }

    public void acceptReturn() {
        status = OrderStatus.BEING_RETURNED;
    }

    public boolean isReturnAllowed() {
        return OrderStatus.DELIVERED.equals(status) && LocalDateTime.now().isBefore(updatedAt.plusDays(1));
    }
}
