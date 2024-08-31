package cm.twentysix.order.controller;

import cm.twentysix.order.dto.*;
import cm.twentysix.order.exception.CartException;
import cm.twentysix.order.service.CartService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static cm.twentysix.order.exception.Error.ITEM_DOES_NOT_EXIST;
import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CartController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
class CartControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private CartService cartService;


    @Test
    void addCartItem_success() throws Exception {
        //given
        AddCartItemForm form = new AddCartItemForm("1234", 5);
        //when
        //then
        mockMvc.perform(post("/carts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)
                                .getBytes(StandardCharsets.UTF_8))
                        .header("X-USER-ID", 1L)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("{methodName}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
    }

    @Test
    void addCartItem_fail_REQUEST_ARGUMENT_NOT_VALID() throws Exception {
        //given
        AddCartItemForm form = new AddCartItemForm("    ", 0);
        //when
        //then
        mockMvc.perform(post("/carts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)
                                .getBytes(StandardCharsets.UTF_8))
                        .header("X-USER-ID", 1L)
                )
                .andDo(print())
                .andExpectAll(status().isBadRequest(),
                        jsonPath("$.message.id").value("아이디는 비어있을 수 없습니다."),
                        jsonPath("$.message.quantity").value("수량은 1개 이상입니다.")
                )
                .andDo(document("{methodName}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
    }


    @Test
    void deleteCartItem_success() throws Exception {
        //given
        DeleteCartItemForm form = new DeleteCartItemForm(List.of("1234"));
        //when
        //then
        mockMvc.perform(delete("/carts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)
                                .getBytes(StandardCharsets.UTF_8))
                        .header("X-USER-ID", 1L)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("{methodName}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
    }

    @Test
    void deleteCartItem_fail_REQUEST_ARGUMENT_NOT_VALID() throws Exception {
        //given
        DeleteCartItemForm form = new DeleteCartItemForm(List.of());
        //when
        //then
        mockMvc.perform(delete("/carts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)
                                .getBytes(StandardCharsets.UTF_8))
                        .header("X-USER-ID", 1L)
                )
                .andDo(print())
                .andExpectAll(status().isBadRequest(),
                        jsonPath("$.message.productIds").exists())
                .andDo(document("{methodName}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
    }

    @Test
    void deleteCartItem_fail_ITEM_DOES_NOT_EXIST() throws Exception {
        //given
        DeleteCartItemForm form = new DeleteCartItemForm(List.of("1234"));
        doThrow(new CartException(ITEM_DOES_NOT_EXIST))
                .when(cartService).deleteCartItems(anyLong(), any());

        //when
        //then
        mockMvc.perform(delete("/carts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)
                                .getBytes(StandardCharsets.UTF_8))
                        .header("X-USER-ID", 1L)
                )
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andDo(document("{methodName}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
    }

    @Test
    void changeCartItemQuantity_success() throws Exception {
        //given
        ChangeCartItemQuantityForm form = new ChangeCartItemQuantityForm("1234", 2);
        //when
        //then
        mockMvc.perform(put("/carts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)
                                .getBytes(StandardCharsets.UTF_8))
                        .header("X-USER-ID", 1L)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("{methodName}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
    }

    @Test
    void changeCartItemQuantity_fail_REQUEST_ARGUMENT_NOT_VALID() throws Exception {
        //given
        ChangeCartItemQuantityForm form = new ChangeCartItemQuantityForm("     ", 1001);
        //when
        //then
        mockMvc.perform(put("/carts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)
                                .getBytes(StandardCharsets.UTF_8))
                        .header("X-USER-ID", 1L)
                )
                .andDo(print())
                .andExpectAll(status().isBadRequest(),
                        jsonPath("$.message.id").value("아이디는 비어있을 수 없습니다."),
                        jsonPath("$.message.quantity").value("수량은 1000개 이하입니다."))
                .andDo(document("{methodName}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
    }

    @Test
    void changeCartItemQuantity_fail_ITEM_DOES_NOT_EXIST() throws Exception {
        //given
        ChangeCartItemQuantityForm form = new ChangeCartItemQuantityForm("1234", 2);
        doThrow(new CartException(ITEM_DOES_NOT_EXIST))
                .when(cartService).changeCartItemQuantity(anyLong(), any());

        //when
        //then
        mockMvc.perform(put("/carts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)
                                .getBytes(StandardCharsets.UTF_8))
                        .header("X-USER-ID", 1L)
                )
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andDo(document("{methodName}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
    }

    @Test
    void retrieveCart_success() throws Exception {
        //given
        given(cartService.retrieveCart(anyLong())).willReturn(
                List.of(
                        CartItem.builder()
                                .brandName("돌봄")
                                .brandId(1L)
                                .items(List.of(
                                        CartProductItem.builder()
                                                .price(10000)
                                                .name("터그 장난감")
                                                .totalPrice(10000)
                                                .discount(0)
                                                .discountedPrice(10000)
                                                .quantity(1)
                                                .productId("1234")
                                                .build()
                                ))
                                .freeDeliveryInfimum(10000)
                                .deliveryFee(2500)
                                .build()
                )
        );
        //when
        //then
        mockMvc.perform(get("/carts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-USER-ID", 1L)
                        .param("page", "0")
                        .param("size", "20")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("{methodName}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
    }


}