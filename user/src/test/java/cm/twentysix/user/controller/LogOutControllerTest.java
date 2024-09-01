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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.relaxedQueryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = LogOutController.class)
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
                        .header(AUTHORIZATION, "Bearer eyJhbGciOiJIUzI1NiJ9.")
                        .cookie(cookie)
                        .param("type", "ALL")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("{methodName}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName(AUTHORIZATION)
                                .description("Bearer token for authorization")),
                        relaxedQueryParameters(
                                parameterWithName("type").optional().description("logout type is ALL or SINGLE")
                        )

                ));
    }

    @Test
    @DisplayName("로그아웃 성공")
    void logout_success_WhenSingleType() throws Exception {
        //given
        Cookie cookie = new Cookie("refreshToken", "anytoken");
        //when
        //then
        mockMvc.perform(get("/users/logout")
                        .header(AUTHORIZATION, "Bearer eyJhbGciOiJIUzI1NiJ9.")
                        .cookie(cookie)
                        .param("type", "SINGLE")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("{methodName}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName(AUTHORIZATION)
                                .description("Bearer token for authorization")),
                        relaxedQueryParameters(
                                parameterWithName("type").optional().description("logout type is ALL or SINGLE")
                        )

                ));
    }

    @Test
    @DisplayName("로그아웃 실패-허용하지 않는 타입인 경우")
    void logout_fail() throws Exception {
        //given
        Cookie cookie = new Cookie("refreshToken", "anytoken");
        //when
        //then
        mockMvc.perform(get("/users/logout")
                        .header(AUTHORIZATION, "Bearer eyJhbGciOiJIUzI1NiJ9.")
                        .cookie(cookie)
                        .param("type", "ALLL")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andDo(document("{methodName}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        relaxedQueryParameters(
                                parameterWithName("type").optional().description("login type is ALL or SINGLE")
                        )
                ));
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
                        .header(AUTHORIZATION, "Bearer eyJhbGciOiJIUzI1NiJ9.")
                        .param("type", "ALL")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andDo(document("{methodName}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        relaxedQueryParameters(
                                parameterWithName("type").optional().description("logout type is ALL or SINGLE")
                        )
                ));
    }

}