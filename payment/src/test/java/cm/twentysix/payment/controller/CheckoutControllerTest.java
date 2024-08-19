package cm.twentysix.payment.controller;

import cm.twentysix.payment.dto.RequiredPaymentResponse;
import cm.twentysix.payment.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CheckoutController.class)
@AutoConfigureMockMvc(addFilters = false)
class CheckoutControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    PaymentService paymentService;

    @Test
    void checkout_success() throws Exception {
        //given
        given(paymentService.getRequiredPayment(anyString()))
                .willReturn(RequiredPaymentResponse.builder()
                        .amount(1000)
                        .orderName("귀여운 데님 외 1건")
                        .isBlocked(false)
                        .orderId("abcdefkghaskfldlm123")
                        .userId(1L)
                        .build());
        //when
        //then
        mockMvc.perform(get("/checkout/{orderId}", "abcdefkghaskfldlm123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-USER-ID", 1L)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attribute("amount", 1000))
                .andExpect(model().attribute("orderId", "abcdefkghaskfldlm123"))
                .andExpect(model().attribute("userId", 1L))
                .andExpect(model().attribute("orderName", "귀여운 데님 외 1건"))
                .andExpect(model().attribute("isBlocked", false))
                .andExpect(view().name("checkout"));
    }

    @Test
    void fail_success() throws Exception {
        //given
        //when
        //then
        mockMvc.perform(get("/checkout/fail")
                        .param("code", "STOCK_SHORTAGE")
                        .param("message", "Order failed due to insufficient stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-USER-ID", 1L)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void success_success() throws Exception {
        //given
        //when
        //then
        mockMvc.perform(get("/checkout/success")
                        .param("paymentKey", "dag_123435")
                        .param("orderId", "abcdefkghaskfldlm123")
                        .param("amount", "10000")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-USER-ID", 1L)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

}