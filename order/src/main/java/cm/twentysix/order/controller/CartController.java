package cm.twentysix.order.controller;

import cm.twentysix.order.dto.AddCartItemForm;
import cm.twentysix.order.dto.CartItem;
import cm.twentysix.order.dto.ChangeCartItemQuantityForm;
import cm.twentysix.order.dto.DeleteCartItemForm;
import cm.twentysix.order.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/carts")
public class CartController {
    private final CartService cartService;

    @PostMapping
    public ResponseEntity<Void> addCartItem(@Valid @RequestBody AddCartItemForm form,
                                            @RequestHeader(value = "X-USER-ID") Long userId) {
        cartService.addCartItem(userId, form);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteCartItems(@Valid @RequestBody DeleteCartItemForm form,
                                                @RequestHeader(value = "X-USER-ID") Long userId) {
        cartService.deleteCartItems(userId, form);
        return ResponseEntity.ok().build();
    }

    @PutMapping
    public ResponseEntity<Void> changeCartItemQuantity(@Valid @RequestBody ChangeCartItemQuantityForm form,
                                                       @RequestHeader(value = "X-USER-ID") Long userId) {
        cartService.changeCartItemQuantity(userId, form);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<CartItem>> retrieveCart(@RequestHeader(value = "X-USER-ID") Long userId) {
        return ResponseEntity.ok(cartService.retrieveCart(userId));
    }
}
