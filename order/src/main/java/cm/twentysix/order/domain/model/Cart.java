package cm.twentysix.order.domain.model;

import cm.twentysix.ProductProto.ProductItemResponse;
import cm.twentysix.order.dto.AddCartItemForm;
import cm.twentysix.order.dto.OrderProductItemForm;
import cm.twentysix.order.exception.CartException;
import cm.twentysix.order.exception.Error;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@RedisHash(value = "carts")
public class Cart implements Serializable {
    @Id
    private Long userId;
    private Map<String, CartProduct> items;

    public Cart(Long userId) {
        this.userId = userId;
        this.items = new HashMap<>();
    }

    @Builder
    public Cart(Long userId, Map<String, CartProduct> items) {
        this.userId = userId;
        this.items = items;
    }

    public void addItem(AddCartItemForm form, ProductItemResponse response) {
        CartProduct product = items.getOrDefault(form.id(), CartProduct.of(response));
        product.addQuantity(form.quantity());
        items.put(form.id(), product);
    }

    public void deleteItems(List<String> productIds) {
        productIds.forEach(this::deleteItem);
    }

    private void deleteItem(String productId) {
        if (!items.containsKey(productId))
            throw new CartException(Error.ITEM_DOES_NOT_EXIST);
        items.remove(productId);
    }


    public void changeItemQuantity(String productId, int quantity) {
        if (!items.containsKey(productId))
            throw new CartException(Error.ITEM_DOES_NOT_EXIST);
        CartProduct product = items.get(productId);
        product.changeQuantity(quantity);

    }

    public List<Long> getBrandIds() {
        return items.values().stream()
                .map(CartProduct::getBrandId)
                .collect(Collectors.toList());
    }

    public List<String> getProductIds() {
        return items.keySet().stream().toList();
    }

    public void removeOrderedItems(List<OrderProductItemForm> orderedItem) {
        orderedItem.forEach(itemToBeRemoved ->
                items.get(itemToBeRemoved.id()).decreaseQuantity(itemToBeRemoved.quantity()));
    }
}
