package cm.twentysix.payment.controller;

import cm.twentysix.payment.dto.PaymentForm;
import cm.twentysix.payment.exception.PaymentException;
import cm.twentysix.payment.exception.ProductException;
import cm.twentysix.payment.service.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;

import static cm.twentysix.payment.exception.Error.*;
import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;

@WebMvcTest(controllers = PaymentController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
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
        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, "Bearer eyJhbGciOiJIUzI1NiJ9.")
                        .content(objectMapper.writeValueAsString(form)
                                .getBytes(StandardCharsets.UTF_8)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("{methodName}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName(HttpHeaders.AUTHORIZATION)
                                .description("Bearer token for authorization"))
                ));
    }

    @Test
    void confirm_fail_REQUEST_ARGUMENT_NOT_VALID() throws Exception {
        //given
        PaymentForm form = new PaymentForm("     ", "    ", "     ");
        //when
        //then
        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, "Bearer eyJhbGciOiJIUzI1NiJ9.")
                        .content(objectMapper.writeValueAsString(form)
                                .getBytes(StandardCharsets.UTF_8)))
                .andDo(print())
                .andExpectAll(status().isBadRequest(),
                        jsonPath("$.message.orderId").value("주문 아이디는 비어있을 수 없습니다."),
                        jsonPath("$.message.amount").value("결제 금액은 비어있을 수 없습니다."),
                        jsonPath("$.message.paymentKey").value("payment key 는 비어있을 수 없습니다.")
                ).andDo(document("{methodName}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName(HttpHeaders.AUTHORIZATION)
                                .description("Bearer token for authorization"))
                ));
    }


    @Test
    void confirm_fail_NOT_FOUND_PAYMENT() throws Exception {
        //given
        PaymentForm form = new PaymentForm("1234", "10000", "12341341");
        //when
        doThrow(new PaymentException(NOT_FOUND_PAYMENT))
                .when(paymentService).confirm(any());

        //then
        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, "Bearer eyJhbGciOiJIUzI1NiJ9.")
                        .content(objectMapper.writeValueAsString(form)
                                .getBytes(StandardCharsets.UTF_8)))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andDo(document("{methodName}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName(HttpHeaders.AUTHORIZATION)
                                .description("Bearer token for authorization"))
                ));
    }

    @Test
    void confirm_fail_STOCK_SHORTAGE() throws Exception {
        //given
        PaymentForm form = new PaymentForm("1234", "10000", "12341341");
        //when
        doThrow(new ProductException(STOCK_SHORTAGE))
                .when(paymentService).confirm(any());

        //then
        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, "Bearer eyJhbGciOiJIUzI1NiJ9.")
                        .content(objectMapper.writeValueAsString(form)
                                .getBytes(StandardCharsets.UTF_8)))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andDo(document("{methodName}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName(HttpHeaders.AUTHORIZATION)
                                .description("Bearer token for authorization"))
                ));
    }

    @Test
    void confirm_fail_CANCELLED_ORDER() throws Exception {
        //given
        PaymentForm form = new PaymentForm("1234", "10000", "12341341");
        //when
        doThrow(new PaymentException(CANCELLED_ORDER))
                .when(paymentService).confirm(any());

        //then
        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, "Bearer eyJhbGciOiJIUzI1NiJ9.")
                        .content(objectMapper.writeValueAsString(form)
                                .getBytes(StandardCharsets.UTF_8)))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andDo(document("{methodName}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName(HttpHeaders.AUTHORIZATION)
                                .description("Bearer token for authorization"))
                ));
    }

    @Test
    void confirm_fail_ALREADY_PAID_ORDER() throws Exception {
        //given
        PaymentForm form = new PaymentForm("1234", "10000", "12341341");
        //when
        doThrow(new PaymentException(ALREADY_PAID_ORDER))
                .when(paymentService).confirm(any());

        //then
        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, "Bearer eyJhbGciOiJIUzI1NiJ9.")
                        .content(objectMapper.writeValueAsString(form)
                                .getBytes(StandardCharsets.UTF_8)))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andDo(document("{methodName}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName(HttpHeaders.AUTHORIZATION)
                                .description("Bearer token for authorization"))
                ));
    }

    @Test
    void confirm_fail_BLOCKED_ORDER() throws Exception {
        //given
        PaymentForm form = new PaymentForm("1234", "10000", "12341341");
        //when
        doThrow(new PaymentException(BLOCKED_ORDER))
                .when(paymentService).confirm(any());

        //then
        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, "Bearer eyJhbGciOiJIUzI1NiJ9.")
                        .content(objectMapper.writeValueAsString(form)
                                .getBytes(StandardCharsets.UTF_8)))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andDo(document("{methodName}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName(HttpHeaders.AUTHORIZATION)
                                .description("Bearer token for authorization"))
                ));
    }

    @Test
    void confirm_fail_PAYMENT_FAILED() throws Exception {
        //given
        PaymentForm form = new PaymentForm("1234", "10000", "12341341");
        //when
        doThrow(new PaymentException(PAYMENT_FAILED))
                .when(paymentService).confirm(any());

        //then
        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, "Bearer eyJhbGciOiJIUzI1NiJ9.")
                        .content(objectMapper.writeValueAsString(form)
                                .getBytes(StandardCharsets.UTF_8)))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andDo(document("{methodName}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName(HttpHeaders.AUTHORIZATION)
                                .description("Bearer token for authorization"))
                ));
    }


}