package cm.twentysix.order.controller;

import cm.twentysix.order.dto.CreateOrderForm;
import cm.twentysix.order.dto.ProductItem;
import cm.twentysix.order.service.OrderService;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
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
        List<ProductItem> items = List.of(
                new ProductItem("123456", 1)
        );
        CreateOrderForm.Receiver receiver = new CreateOrderForm.Receiver("송송이", "서울특별시 성북구 보문로", "11112", "010-2222-2222");
        CreateOrderForm form = new CreateOrderForm(items, true, receiver);
        //when
        //then
        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)
                                .getBytes(StandardCharsets.UTF_8))
                        .header("X-USER-ID", 1L)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void receiveOrder_fail() throws Exception {
        //given
        List<ProductItem> items = List.of(
                new ProductItem("1", 0)
        );
        CreateOrderForm.Receiver receiver = new CreateOrderForm.Receiver("송", "           ", "1212", "010-");
        CreateOrderForm form = new CreateOrderForm(items, true, receiver);
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
                );
    }

}