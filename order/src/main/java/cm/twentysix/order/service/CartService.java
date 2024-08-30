package cm.twentysix.order.service;

import cm.twentysix.BrandProto.BrandInfo;
import cm.twentysix.ProductProto.ProductItemResponse;
import cm.twentysix.order.client.BrandGrpcClient;
import cm.twentysix.order.client.ProductGrpcClient;
import cm.twentysix.order.domain.model.Cart;
import cm.twentysix.order.domain.model.CartProduct;
import cm.twentysix.order.domain.repository.CartRepository;
import cm.twentysix.order.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final ProductGrpcClient productGrpcClient;
    private final BrandGrpcClient brandGrpcClient;

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

    public List<CartItem> retrieveCart(Long userId) {
        Cart cart = cartRepository.findById(userId)
                .orElseGet(() -> new Cart(userId));

        if(cart.getItems().isEmpty())
            return new ArrayList<>();

        List<String> productIds = cart.getProductIds();
        List<Long> brandIds = cart.getBrandIds();

        CompletableFuture<List<ProductItemResponse>> productInfoFuture =
                CompletableFuture.supplyAsync(() -> productGrpcClient.findProductItems(productIds));
        CompletableFuture<Map<Long, BrandInfo>> brandInfoFuture =
                CompletableFuture.supplyAsync(() -> brandGrpcClient.findBrandInfo(brandIds));

        return CompletableFuture.allOf(
                productInfoFuture, brandInfoFuture
        ).thenApply(_ -> {
                    try {
                        List<ProductItemResponse> productInfos = productInfoFuture.get();
                        Map<Long, BrandInfo> brandInfos = brandInfoFuture.get();
                        return toCartItems(productInfos, brandInfos, cart.getItems());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
        ).join();
    }

    private List<CartItem> toCartItems(List<ProductItemResponse> containingProductInfo, Map<Long, BrandInfo> containingBrandInfo, Map<String, CartProduct> cartProducts) {
        Map<Long, CartItem> cartItems = new HashMap<>();
        for (ProductItemResponse productInfo : containingProductInfo) {
            if (!cartItems.containsKey(productInfo.getBrandId())) {
                BrandInfo brandInfo = containingBrandInfo.get(productInfo.getBrandId());
                cartItems.put(productInfo.getBrandId(), CartItem.from(brandInfo));
            }
            CartItem cartItem = cartItems.get(productInfo.getBrandId());
            CartProduct cartProduct = cartProducts.get(productInfo.getId());
            cartItem.addCartProductItem(productInfo, cartProduct.getQuantity());
        }
        return cartItems.values().stream().toList();
    }

    public void removeOrderedCartItem(CreateOrderForm form, Long userId){
        if(!form.shouldDeleteCartItem())
            return;
        Optional<Cart> maybeCart = cartRepository.findById(userId);
        if(maybeCart.isEmpty())
            return;
        Cart cart = maybeCart.get();
        cart.removeOrderedItems(form.products());
        cartRepository.save(cart);
    }

}
