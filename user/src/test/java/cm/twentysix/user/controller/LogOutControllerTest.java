package cm.twentysix.user.controller;

import cm.twentysix.user.service.LogOutService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = LogOutController.class)
@AutoConfigureMockMvc(addFilters = false)
class LogOutControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private LogOutService logOutService;

    @Test
    @DisplayName("로그아웃 성공")
    @WithMockUser
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
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("로그아웃 성공-타입을 명시하지 않을 경우")
    @WithMockUser
    void logout_success_hasNoType() throws Exception {
        //given
        Cookie cookie = new Cookie("refreshToken", "anytoken");
        //when
        //then
        mockMvc.perform(get("/users/logout")
                        .cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("로그아웃 실패-허용하지 않는 타입인 경우")
    @WithMockUser
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
                .andExpect(status().isBadRequest());
    }

}