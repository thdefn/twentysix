package cm.twentysix.user.controller;

import cm.twentysix.user.exception.Error;
import cm.twentysix.user.exception.UserException;
import cm.twentysix.user.service.LogOutService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
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

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = LogOutController.class, excludeFilters = @ComponentScan.Filter(FeignClient.class))
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
class LogOutControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private LogOutService logOutService;

    @Test
    @DisplayName("로그아웃 성공")
    void logout_success() throws Exception {
        //given
        Cookie cookie = new Cookie("refreshToken", "anytoken");
        //when
        //then
        mockMvc.perform(get("/users/logout")
                        .cookie(cookie)
                        .param("type", "ALL")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("{methodName}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
    }

    @Test
    @DisplayName("로그아웃 성공-타입을 명시하지 않을 경우")
    void logout_success_hasNoType() throws Exception {
        //given
        Cookie cookie = new Cookie("refreshToken", "anytoken");
        //when
        //then
        mockMvc.perform(get("/users/logout")
                        .cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("{methodName}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
    }

    @Test
    @DisplayName("로그아웃 실패-허용하지 않는 타입인 경우")
    void logout_fail() throws Exception {
        //given
        Cookie cookie = new Cookie("refreshToken", "anytoken");
        //when
        //then
        mockMvc.perform(get("/users/logout")
                        .cookie(cookie)
                        .param("type", "ALLL")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andDo(document("{methodName}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
    }

    @Test
    @DisplayName("로그아웃 실패-EMPTY_REFRESH_TOKEN")
    void logout_fail_EMPTY_REFRESH_TOKEN() throws Exception {
        //given
        Cookie cookie = new Cookie("refreshToken", null);
        doThrow(new UserException(Error.EMPTY_REFRESH_TOKEN))
                .when(logOutService).logout(any(), anyString());
        //when
        //then
        mockMvc.perform(get("/users/logout")
                        .cookie(cookie)
                        .param("type", "ALLL")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andDo(document("{methodName}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
    }

}