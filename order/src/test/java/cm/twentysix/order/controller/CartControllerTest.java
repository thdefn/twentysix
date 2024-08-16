package cm.twentysix.order.controller;

import cm.twentysix.order.dto.AddCartItemForm;
import cm.twentysix.order.dto.ChangeCartItemQuantityForm;
import cm.twentysix.order.dto.DeleteCartItemForm;
import cm.twentysix.order.service.CartService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CartController.class)
@AutoConfigureMockMvc(addFilters = false)
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
                .andExpect(status().isOk());
    }

    @Test
    void addCartItem_fail() throws Exception {
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
                );
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
                .andExpect(status().isOk());
    }

    @Test
    void deleteItem_fail() throws Exception {
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
                        jsonPath("$.message.productIds").exists()
                );
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
                .andExpect(status().isOk());
    }

    @Test
    void changeCartItemQuantity_fail() throws Exception {
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
                        jsonPath("$.message.quantity").value("수량은 1000개 이하입니다.")
                );
    }

    @Test
    void retrieveCart_success() throws Exception {
        //given
        //when
        //then
        mockMvc.perform(get("/carts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-USER-ID", 1L)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }


}