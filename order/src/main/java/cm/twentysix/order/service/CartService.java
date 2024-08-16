package cm.twentysix.order.service;

import cm.twentysix.ProductProto.ProductItemResponse;
import cm.twentysix.order.client.ProductGrpcClient;
import cm.twentysix.order.domain.model.Cart;
import cm.twentysix.order.domain.repository.CartRepository;
import cm.twentysix.order.dto.AddCartItemForm;
import cm.twentysix.order.dto.ChangeCartItemQuantityForm;
import cm.twentysix.order.dto.DeleteCartItemForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final ProductGrpcClient productGrpcClient;

    public void addCartItem(Long userId, AddCartItemForm form) {
        Cart cart = cartRepository.findById(userId)
                .orElseGet(() -> new Cart(userId));
        ProductItemResponse response = productGrpcClient.findProductItem(form.id());
        cart.addItem(form, response);
        cartRepository.save(cart);
    }

    public void deleteCartItems(Long userId, DeleteCartItemForm form) {
        Cart cart = cartRepository.findById(userId)
                .orElseGet(() -> new Cart(userId));
        cart.deleteItems(form.productIds());
        cartRepository.save(cart);
    }

    public void changeCartItemQuantity(Long userId, ChangeCartItemQuantityForm form) {
        Cart cart = cartRepository.findById(userId)
                .orElseGet(() -> new Cart(userId));
        cart.changeItemQuantity(form.id(), form.quantity());
        cartRepository.save(cart);
    }
}
