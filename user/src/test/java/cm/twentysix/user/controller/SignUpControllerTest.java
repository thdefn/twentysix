package cm.twentysix.user.controller;

import cm.twentysix.user.dto.SignUpForm;
import cm.twentysix.user.exception.EmailAuthException;
import cm.twentysix.user.exception.Error;
import cm.twentysix.user.exception.UserException;
import cm.twentysix.user.service.SignUpService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = SignUpController.class, excludeFilters = @ComponentScan.Filter(FeignClient.class))
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
class SignUpControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private SignUpService signUpService;

    @Test
    @DisplayName("회원가입_성공")
    void signUp_success() throws Exception {
        //given
        SignUpForm form = new SignUpForm("abcde@gmail.com", "Qwerty!@1", "010-1111-1111", "송송", "서울 특별시 성북구 보문로 23", "11111", "SELLER");
        //when
        //then
        mockMvc.perform(post("/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .sessionAttr("SESSION_ID", "hihihiih")
                        .content(objectMapper.writeValueAsString(form)
                                .getBytes(StandardCharsets.UTF_8)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("{methodName}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
    }

    @Test
    @DisplayName("회원가입_실패_REQUEST_ARGUMENT_NOT_VALID")
    void signUp_fail() throws Exception {
        //given
        SignUpForm form = new SignUpForm("abcdegmail.com", "Qwerty!@", "01011111111", "송", "서울 특별시", "1234", "USER");
        //when
        //then
        mockMvc.perform(post("/users/signup")
                        .sessionAttr("SESSION_ID", "hihihiih")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)
                                .getBytes(StandardCharsets.UTF_8)))
                .andDo(print())
                .andExpectAll(status().isBadRequest(),
                        jsonPath("$.message.zipCode").value("우편번호 형식이 아닙니다."),
                        jsonPath("$.message.password").value("비밀번호는 8자리 이상의 소문자, 대문자, 숫자, 특수문자를 포함해야 합니다."),
                        jsonPath("$.message.address").value("주소의 형식이 아닙니다."),
                        jsonPath("$.message.phone").value("전화 번호 형식이 아닙니다."),
                        jsonPath("$.message.name").value("이름의 형식이 아닙니다."),
                        jsonPath("$.message.email").value("이메일 형식이 아닙니다.")
                )
                .andDo(document("{methodName}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
    }

    @Test
    @DisplayName("회원가입 실패_NOT_VERIFIED_EMAIL")
    void signUp_fail_NOT_VERIFIED_EMAIL() throws Exception {
        //given
        SignUpForm form = new SignUpForm("abcde@gmail.com", "Qwerty!@1", "010-1111-1111", "송송", "서울 특별시 성북구 보문로 23", "11111", "SELLER");
        doThrow(new EmailAuthException(Error.NOT_VERIFIED_EMAIL))
                .when(signUpService).signUp(any(), any());
        //when
        //then
        mockMvc.perform(post("/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .sessionAttr("SESSION_ID", "hihihiih")
                        .content(objectMapper.writeValueAsString(form)
                                .getBytes(StandardCharsets.UTF_8)))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andDo(document("{methodName}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
    }

    @Test
    @DisplayName("회원가입 실패_ALREADY_REGISTER_EMAIL")
    void signUp_fail_ALREADY_REGISTER_EMAIL() throws Exception {
        //given
        SignUpForm form = new SignUpForm("abcde@gmail.com", "Qwerty!@1", "010-1111-1111", "송송", "서울 특별시 성북구 보문로 23", "11111", "SELLER");
        doThrow(new UserException(Error.ALREADY_REGISTER_EMAIL))
                .when(signUpService).signUp(any(), any());
        //when
        //then
        mockMvc.perform(post("/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .sessionAttr("SESSION_ID", "hihihiih")
                        .content(objectMapper.writeValueAsString(form)
                                .getBytes(StandardCharsets.UTF_8)))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andDo(document("{methodName}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
    }

}