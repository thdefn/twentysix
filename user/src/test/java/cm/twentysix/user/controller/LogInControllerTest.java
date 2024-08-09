package cm.twentysix.user.controller;

import cm.twentysix.user.controller.dto.LogInForm;
import cm.twentysix.user.controller.dto.TokenResponse;
import cm.twentysix.user.service.LogInService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = LogInController.class)
@AutoConfigureMockMvc(addFilters = false)
class LogInControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private LogInService logInService;

    @Test
    @DisplayName("로그인_성공")
    void login_success() throws Exception {
        //given
        LogInForm form = new LogInForm("abcde@gmail.com", "Qwerty!@1");
        given(logInService.login(any()))
                .willReturn(TokenResponse.of("any", "any"));
        //when
        logInService.login(form);
        //then
        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)
                                .getBytes(StandardCharsets.UTF_8)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("로그인_실패")
    void login_fail() throws Exception {
        //given
        LogInForm form = new LogInForm("abcdegmail.com", "Qwerty!@");
        //when
        logInService.login(form);
        //then
        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)
                                .getBytes(StandardCharsets.UTF_8)))
                .andDo(print())
                .andExpectAll(status().isBadRequest(),
                        jsonPath("$.message.email").value("이메일 형식이 아닙니다."),
                        jsonPath("$.message.password").value("비밀번호는 8자리 이상의 소문자, 대문자, 숫자, 특수문자를 포함해야 합니다."));
    }



}