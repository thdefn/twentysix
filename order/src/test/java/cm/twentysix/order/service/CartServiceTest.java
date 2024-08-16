package cm.twentysix.order.service;

import cm.twentysix.BrandProto.BrandInfo;
import cm.twentysix.ProductProto.ProductItemResponse;
import cm.twentysix.order.client.BrandGrpcClient;
import cm.twentysix.order.client.ProductGrpcClient;
import cm.twentysix.order.domain.model.Cart;
import cm.twentysix.order.domain.model.CartProduct;
import cm.twentysix.order.domain.repository.CartRepository;
import cm.twentysix.order.dto.*;
import cm.twentysix.order.exception.CartException;
import cm.twentysix.order.exception.Error;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {
    @Mock
    private CartRepository cartRepository;
    @Mock
    private ProductGrpcClient productGrpcClient;
    @Mock
    private BrandGrpcClient brandGrpcClient;
    @InjectMocks
    private CartService cartService;

    @Test
    void addCartItem_success() {
        //given
        AddCartItemForm form = new AddCartItemForm("1234", 2);
        Cart cart = Cart.builder()
                .userId(1L)
                .items(new HashMap<>(
                        Map.of(
                                "1234", CartProduct.builder()
                                        .brandId(1L)
                                        .quantity(1)
                                        .build()
                        )
                )).build();
        given(cartRepository.findById(anyLong())).willReturn(Optional.of(cart));
        given(productGrpcClient.findProductItem(any())).willReturn(ProductItemResponse.newBuilder()
                .setId("1234")
                .setName("강아지 미용 티슈")
                .setPrice(3000)
                .setBrandName("돌봄")
                .setDiscountedPrice(2000)
                .setDiscount(10)
                .setBrandId(1L)
                .build());
        //when
        cartService.addCartItem(1L, form);
        //then
        assertEquals(3, cart.getItems().get("1234").getQuantity());
        assertEquals(1L, cart.getItems().get("1234").getBrandId());
    }

    @Test
    void addCartItem_successWhenCartIsEmpty() {
        //given
        AddCartItemForm form = new AddCartItemForm("1234", 2);
        given(cartRepository.findById(anyLong())).willReturn(Optional.empty());
        given(productGrpcClient.findProductItem(any())).willReturn(ProductItemResponse.newBuilder()
                .setId("1234")
                .setName("강아지 미용 티슈")
                .setPrice(3000)
                .setBrandName("돌봄")
                .setDiscountedPrice(2000)
                .setDiscount(10)
                .setBrandId(1L)
                .build());
        //when
        cartService.addCartItem(1L, form);
        //then
        ArgumentCaptor<Cart> cartCaptor = ArgumentCaptor.forClass(Cart.class);
        verify(cartRepository, times(1)).save(cartCaptor.capture());
        Cart saved = cartCaptor.getValue();
        assertTrue(saved.getItems().containsKey("1234"));
        assertEquals(2, saved.getItems().get("1234").getQuantity());
        assertEquals(1, saved.getItems().get("1234").getBrandId());
    }

    @Test
    void deleteCartItems_success() {
        //given
        DeleteCartItemForm form = new DeleteCartItemForm(List.of("1234"));
        Cart cart = Cart.builder()
                .userId(1L)
                .items(new HashMap<>(
                        Map.of(
                                "1234", CartProduct.builder()
                                        .brandId(1L)
                                        .quantity(1)
                                        .build(),
                                "4321", CartProduct.builder()
                                        .brandId(2L)
                                        .quantity(1)
                                        .build()
                        )
                )).build();
        given(cartRepository.findById(anyLong())).willReturn(Optional.of(cart));
        //when
        cartService.deleteCartItems(1L, form);
        //then
        assertEquals(cart.getItems().size(), 1);
    }

    @Test
    void deleteCartItems_fail_ITEM_DOES_NOT_EXIST() {
        //given
        DeleteCartItemForm form = new DeleteCartItemForm(List.of("1234"));
        Cart cart = Cart.builder()
                .userId(1L)
                .items(Map.of("5432", CartProduct.builder()
                        .brandId(1L)
                        .quantity(1)
                        .build())).build();
        given(cartRepository.findById(anyLong())).willReturn(Optional.of(cart));
        //when
        CartException e = assertThrows(CartException.class, () -> cartService.deleteCartItems(1L, form));
        //then
        assertEquals(e.getError(), Error.ITEM_DOES_NOT_EXIST);
    }

    @Test
    void changeCartItemQuantity_success() {
        //given
        ChangeCartItemQuantityForm form = new ChangeCartItemQuantityForm("1234", 5);
        Cart cart = Cart.builder()
                .userId(1L)
                .items(Map.of("1234", CartProduct.builder()
                        .brandId(1L)
                        .quantity(1)
                        .build())).build();
        given(cartRepository.findById(anyLong())).willReturn(Optional.of(cart));
        //when
        cartService.changeCartItemQuantity(1L, form);
        //then
        CartProduct changedItem = cart.getItems().get("1234");
        assertEquals(changedItem.getQuantity(), 5);
    }

    @Test
    void changeCartItemQuantity_fail_ITEM_DOES_NOT_EXIST() {
        //given
        ChangeCartItemQuantityForm form = new ChangeCartItemQuantityForm("1234", 5);
        Cart cart = Cart.builder()
                .userId(1L)
                .items(Map.of("5432", CartProduct.builder()
                        .brandId(1L)
                        .quantity(1)
                        .build())).build();
        given(cartRepository.findById(anyLong())).willReturn(Optional.of(cart));
        //when
        CartException e = assertThrows(CartException.class, () -> cartService.changeCartItemQuantity(1L, form));
        //then
        assertEquals(e.getError(), Error.ITEM_DOES_NOT_EXIST);
    }

    @Test
    void retrieveCart_success() {
        //given
        Cart cart = Cart.builder()
                .userId(1L)
                .items(Map.of("12345", CartProduct.builder()
                                .brandId(1L)
                                .quantity(1)
                                .build(),
                        "56789", CartProduct.builder()
                                .brandId(1L)
                                .quantity(2)
                                .build())).build();
        given(cartRepository.findById(anyLong())).willReturn(Optional.of(cart));
        List<ProductItemResponse> productInfos = List.of(
                ProductItemResponse.newBuilder()
                        .setId("12345")
                        .setName("강아지 미용 티슈")
                        .setPrice(3000)
                        .setBrandName("돌봄")
                        .setDiscountedPrice(2000)
                        .setDiscount(10)
                        .setBrandId(1L)
                        .build(),
                ProductItemResponse.newBuilder()
                        .setId("56789")
                        .setName("강아지 터그 장난감")
                        .setPrice(8000)
                        .setBrandName("돌봄")
                        .setDiscountedPrice(6000)
                        .setDiscount(15)
                        .setBrandId(1L)
                        .build()
        );
        given(productGrpcClient.findProductItems(anyList())).willReturn(productInfos);
        Map<Long, BrandInfo> brandInfos = Map.of(
                1L, BrandInfo.newBuilder()
                        .setId(1L)
                        .setName("돌봄")
                        .setDeliveryFee(3000)
                        .setFreeDeliveryInfimum(30000)
                        .build()
        );
        given(brandGrpcClient.findBrandInfo(anyList())).willReturn(brandInfos);
        //when
        List<CartItem> cartItems = cartService.retrieveCart(1L);
        //then
        assertEquals(1L, cartItems.getFirst().brandId());
        assertEquals("돌봄", cartItems.getFirst().brandName());
        assertEquals(30000, cartItems.getFirst().freeDeliveryInfimum());
        assertEquals(3000, cartItems.getFirst().deliveryFee());
        assertEquals(2, cartItems.getFirst().items().size());
        CartProductItem first = cartItems.getFirst().items().getFirst();
        assertEquals(first.discountedPrice(), 2000);
        assertEquals(first.totalPrice(), 2000);
        assertEquals(first.discount(), 10);
        assertEquals(first.price(), 3000);
        assertEquals(first.name(), "강아지 미용 티슈");
        assertEquals(first.productId(), "12345");

        CartProductItem second = cartItems.getFirst().items().getLast();
        assertEquals(second.discountedPrice(), 6000);
        assertEquals(second.totalPrice(), 12000);
        assertEquals(second.discount(), 15);
        assertEquals(second.price(), 8000);
        assertEquals(second.name(), "강아지 터그 장난감");
        assertEquals(second.productId(), "56789");

    }

    @Test
    void retrieveCart_successWhenCartIsEmpty() {
        //given
        given(cartRepository.findById(anyLong())).willReturn(Optional.empty());
        //when
        List<CartItem> cartItems = cartService.retrieveCart(1L);
        //then
        assertEquals(0, cartItems.size());
        verify(brandGrpcClient, times(0)).findBrandInfo(anyList());
        verify(productGrpcClient, times(0)).findProductItems(anyList());
    }

}