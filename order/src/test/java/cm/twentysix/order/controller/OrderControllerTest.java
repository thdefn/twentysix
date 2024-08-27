package cm.twentysix.order.controller;

import cm.twentysix.order.domain.model.OrderStatus;
import cm.twentysix.order.dto.*;
import cm.twentysix.order.exception.OrderException;
import cm.twentysix.order.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static cm.twentysix.order.exception.Error.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private OrderService orderService;

    @Test
    void receiveOrder_success() throws Exception {
        //given
        List<OrderProductItemForm> items = List.of(
                new OrderProductItemForm("123456", 1)
        );
        CreateOrderForm.ReceiverForm receiver = new CreateOrderForm.ReceiverForm(false, "송송이", "서울특별시 성북구 보문로", "11112", "010-2222-2222");
        CreateOrderForm form = new CreateOrderForm(items, true, true, receiver);
        //when
        //then
        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)
                                .getBytes(StandardCharsets.UTF_8))
                        .header("X-USER-ID", 1L)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("{class-name}/{method-name}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
    }

    @Test
    void receiveOrder_fail_REQUEST_ARGUMENT_NOT_VALID() throws Exception {
        //given
        List<OrderProductItemForm> items = List.of(
                new OrderProductItemForm("1", 0)
        );
        CreateOrderForm.ReceiverForm receiver = new CreateOrderForm.ReceiverForm(false, "송", "           ", "1212", "010-");
        CreateOrderForm form = new CreateOrderForm(items, true, true, receiver);
        //when
        //then
        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)
                                .getBytes(StandardCharsets.UTF_8))
                        .header("X-USER-ID", 1L)
                )
                .andDo(print())
                .andExpectAll(status().isBadRequest(),
                        jsonPath("$.message.['products[0].quantity']").value("수량은 1개 이상입니다."),
                        jsonPath("$.message.['receiver.address']").value("주소는 비어있을 수 없습니다."),
                        jsonPath("$.message.['receiver.zipCode']").value("우편번호 형식이 아닙니다."),
                        jsonPath("$.message.['receiver.name']").value("이름의 형식이 아닙니다."),
                        jsonPath("$.message.['receiver.phone']").value("전화 번호 형식이 아닙니다.")
                )
                .andDo(document("{class-name}/{method-name}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
    }

    @Test
    void receiveOrder_fail_STOCK_SHORTAGE() throws Exception {
        //given
        List<OrderProductItemForm> items = List.of(
                new OrderProductItemForm("123456", 1)
        );
        CreateOrderForm.ReceiverForm receiver = new CreateOrderForm.ReceiverForm(false, "송송이", "서울특별시 성북구 보문로", "11112", "010-2222-2222");
        CreateOrderForm form = new CreateOrderForm(items, true, true, receiver);
        doThrow(new OrderException(STOCK_SHORTAGE))
                .when(orderService).receiveOrder(any(), anyLong(), any());
        //when
        //then
        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)
                                .getBytes(StandardCharsets.UTF_8))
                        .header("X-USER-ID", 1L)
                )
                .andDo(print())
                .andExpect(status().isConflict())
                .andDo(document("{class-name}/{method-name}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
    }

    @Test
    void receiveOrder_fail_ORDER_CONTAIN_CLOSING_PRODUCT() throws Exception {
        //given
        List<OrderProductItemForm> items = List.of(
                new OrderProductItemForm("123456", 1)
        );
        CreateOrderForm.ReceiverForm receiver = new CreateOrderForm.ReceiverForm(false, "송송이", "서울특별시 성북구 보문로", "11112", "010-2222-2222");
        CreateOrderForm form = new CreateOrderForm(items, true, true, receiver);
        doThrow(new OrderException(ORDER_CONTAIN_CLOSING_PRODUCT))
                .when(orderService).receiveOrder(any(), anyLong(), any());
        //when
        //then
        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)
                                .getBytes(StandardCharsets.UTF_8))
                        .header("X-USER-ID", 1L)
                )
                .andDo(print())
                .andExpect(status().is4xxClientError()
                )
                .andDo(document("{class-name}/{method-name}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
    }

    @Test
    void cancelOrder_success() throws Exception {
        //given
        //when
        //then
        mockMvc.perform(post("/orders/{orderId}/cancel", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-USER-ID", 1L)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("{class-name}/{method-name}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
    }

    @Test
    void cancelOrder_fail_ORDER_IN_PREPARATION_NOT_FOUND() throws Exception {
        //given
        doThrow(new OrderException(ORDER_IN_PREPARATION_NOT_FOUND))
                .when(orderService).cancelOrder(anyLong(), anyLong());
        //when
        //then
        mockMvc.perform(post("/orders/{orderId}/cancel", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-USER-ID", 1L)
                )
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andDo(document("{class-name}/{method-name}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
    }

    @Test
    void cancelOrder_fail_NOT_USERS_ORDER() throws Exception {
        //given
        doThrow(new OrderException(NOT_USERS_ORDER))
                .when(orderService).cancelOrder(anyLong(), anyLong());
        //when
        //then
        mockMvc.perform(post("/orders/{orderId}/cancel", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-USER-ID", 1L)
                )
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andDo(document("{class-name}/{method-name}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
    }

    @Test
    void returnOrder_success() throws Exception {
        //given
        //when
        //then
        mockMvc.perform(post("/orders/{orderId}/return", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-USER-ID", 1L)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("{class-name}/{method-name}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
    }

    @Test
    void returnOrder_fail_ORDER_RETURN_NOT_ALLOWED() throws Exception {
        //given
        doThrow(new OrderException(ORDER_RETURN_NOT_ALLOWED))
                .when(orderService).returnOrder(anyLong(), anyLong());

        //when
        //then
        mockMvc.perform(post("/orders/{orderId}/return", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-USER-ID", 1L)
                )
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andDo(document("{class-name}/{method-name}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
    }

    @Test
    void returnOrder_fail_NOT_USERS_ORDER() throws Exception {
        //given
        doThrow(new OrderException(NOT_USERS_ORDER))
                .when(orderService).returnOrder(anyLong(), anyLong());

        //when
        //then
        mockMvc.perform(post("/orders/{orderId}/return", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-USER-ID", 1L)
                )
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andDo(document("{class-name}/{method-name}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
    }

    @Test
    void retrieveMyOrder_success() throws Exception {
        //given
        given(orderService.retrieveMyOrder(anyInt(), anyInt(), anyLong()))
                .willReturn(new SliceImpl<>(
                        List.of(
                                OrderItem.builder()
                                        .id(1L)
                                        .status(OrderStatus.CANCEL)
                                        .orderingAt(LocalDateTime.now())
                                        .orderNumber("202332231313-1212313")
                                        .brands(List.of(
                                                OrderBrandItem.builder()
                                                        .brandId(1L)
                                                        .deliveryFee(0)
                                                        .products(
                                                                List.of(
                                                                        BrandProductItem.builder()
                                                                                .productId("1234")
                                                                                .amount(20000)
                                                                                .name("터그 장난감")
                                                                                .quantity(10)
                                                                                .brandName("돌봄")
                                                                                .brandId(1L)
                                                                                .thumbnail("/12121221.jpg")
                                                                                .deliveryFee(0)
                                                                                .build())).build())).build())));
        //when
        //then
        mockMvc.perform(get("/orders/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-USER-ID", 1L)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("{class-name}/{method-name}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
    }


}