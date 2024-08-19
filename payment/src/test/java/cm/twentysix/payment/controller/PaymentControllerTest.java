package cm.twentysix.payment.controller;

import cm.twentysix.payment.dto.PaymentForm;
import cm.twentysix.payment.service.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PaymentController.class)
@AutoConfigureMockMvc(addFilters = false)
class PaymentControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    PaymentService paymentService;

    @Test
    void confirm_success() throws Exception {
        //given
        PaymentForm form = new PaymentForm("1234", "10000", "12341341");
        //when
        //then
        mockMvc.perform(post("/confirm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .sessionAttr("SESSION_ID", "hihihiih")
                        .content(objectMapper.writeValueAsString(form)
                                .getBytes(StandardCharsets.UTF_8)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void confirm_fail_REQUEST_ARGUMENT_NOT_VALID() throws Exception {
        //given
        PaymentForm form = new PaymentForm("     ", "    ", "     ");
        //when
        //then
        mockMvc.perform(post("/confirm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .sessionAttr("SESSION_ID", "hihihiih")
                        .content(objectMapper.writeValueAsString(form)
                                .getBytes(StandardCharsets.UTF_8)))
                .andDo(print())
                .andExpectAll(status().isBadRequest(),
                        jsonPath("$.message.orderId").value("주문 아이디는 비어있을 수 없습니다."),
                        jsonPath("$.message.amount").value("결제 금액은 비어있을 수 없습니다."),
                        jsonPath("$.message.paymentKey").value("payment key 는 비어있을 수 없습니다.")
                );
    }


}