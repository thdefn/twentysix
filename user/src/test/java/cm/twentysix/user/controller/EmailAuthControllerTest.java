package cm.twentysix.user.controller;

import cm.twentysix.user.dto.SendAuthEmailForm;
import cm.twentysix.user.dto.SendAuthEmailResponse;
import cm.twentysix.user.exception.EmailAuthException;
import cm.twentysix.user.exception.Error;
import cm.twentysix.user.exception.UserException;
import cm.twentysix.user.service.EmailAuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.relaxedQueryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = EmailAuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
class EmailAuthControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private EmailAuthService emailAuthService;

    @Test
    @DisplayName("인증 메일 발송_성공")
    void sendAuthEmail_success() throws Exception {
        SendAuthEmailForm form = new SendAuthEmailForm("abcde@naver.com");
        given(emailAuthService.sendAuthEmail(any(), anyString())).willReturn(new SendAuthEmailResponse("randomuid"));
        mockMvc.perform(post("/users/email-auths")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)
                                .getBytes(StandardCharsets.UTF_8)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("{methodName}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
    }

    @Test
    @DisplayName("인증 메일 발송_실패")
    void sendAuthEmail_fail_REQUEST_ARGUMENT_NOT_VALID() throws Exception {
        SendAuthEmailForm form = new SendAuthEmailForm("abcdenaver.com");
        given(emailAuthService.sendAuthEmail(any(), anyString())).willReturn(new SendAuthEmailResponse("randomuid"));
        mockMvc.perform(post("/users/email-auths")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)
                                .getBytes(StandardCharsets.UTF_8)))
                .andDo(print())
                .andExpectAll(status().isBadRequest(),
                        jsonPath("$.message.email").value("이메일 형식이 아닙니다."))
                .andDo(document("{methodName}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
    }

    @Test
    @DisplayName("인증 메일 발송 실패_ALREADY_REGISTER_EMAIL")
    void sendAuthEmail_fail_ALREADY_REGISTER_EMAIL() throws Exception {
        SendAuthEmailForm form = new SendAuthEmailForm("abcde@naver.com");
        doThrow(new UserException(Error.ALREADY_REGISTER_EMAIL))
                .when(emailAuthService).sendAuthEmail(any(), anyString());
        mockMvc.perform(post("/users/email-auths")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)
                                .getBytes(StandardCharsets.UTF_8)))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andDo(document("{methodName}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
    }

    @Test
    @DisplayName("메일 인증_성공")
    void verifyEmail_success() throws Exception {
        emailAuthService.verifyEmail("abcde@naver.com", "veryverylonglongcode");
        mockMvc.perform(get("/users/email-auths/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("email", "abcde@naver.com")
                        .queryParam("code", "veryverylonglongcode"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("{methodName}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        relaxedQueryParameters(
                                parameterWithName("email").description("verification email"),
                                parameterWithName("code").description("verification code")
                        )
                ));
    }

    @Test
    @DisplayName("메일 인증 실패_NOT_VALID_EMAIL")
    void verifyEmail_fail_NOT_VALID_EMAIL() throws Exception {
        doThrow(new EmailAuthException(Error.NOT_VALID_EMAIL))
                .when(emailAuthService).verifyEmail(anyString(), anyString());
        mockMvc.perform(get("/users/email-auths/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("email", "abcde@naver.com")
                        .queryParam("code", "veryverylonglongcode"))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andDo(document("{methodName}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        relaxedQueryParameters(
                                parameterWithName("email").description("verification email"),
                                parameterWithName("code").description("verification code")
                        )
                ));
    }

    @Test
    @DisplayName("메일 인증 실패_EMAIL_VERIFY_CODE_UNMATCHED")
    void verifyEmail_fail_EMAIL_VERIFY_CODE_UNMATCHED() throws Exception {
        doThrow(new EmailAuthException(Error.EMAIL_VERIFY_CODE_UNMATCHED))
                .when(emailAuthService).verifyEmail(anyString(), anyString());
        mockMvc.perform(get("/users/email-auths/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("email", "abcde@naver.com")
                        .queryParam("code", "veryverylonglongcode"))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andDo(document("{methodName}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        relaxedQueryParameters(
                                parameterWithName("email").description("verification email"),
                                parameterWithName("code").description("verification code")
                        )
                ));
    }

    @Test
    @DisplayName("메일 인증 실패_ALREADY_VERIFIED")
    void verifyEmail_fail_ALREADY_VERIFIED() throws Exception {
        doThrow(new EmailAuthException(Error.ALREADY_VERIFIED))
                .when(emailAuthService).verifyEmail(anyString(), anyString());
        mockMvc.perform(get("/users/email-auths/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("email", "abcde@naver.com")
                        .queryParam("code", "veryverylonglongcode"))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andDo(document("{methodName}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        relaxedQueryParameters(
                                parameterWithName("email").description("verification email"),
                                parameterWithName("code").description("verification code")
                        )
                ));
    }

}