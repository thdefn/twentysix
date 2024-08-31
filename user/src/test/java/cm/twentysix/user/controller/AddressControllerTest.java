package cm.twentysix.user.controller;

import cm.twentysix.user.dto.AddressItem;
import cm.twentysix.user.exception.AddressException;
import cm.twentysix.user.exception.Error;
import cm.twentysix.user.service.AddressService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AddressController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
class AddressControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private AddressService addressService;

    @Test
    void retrieveDefaultAddress_success() throws Exception {
        //given
        given(addressService.retrieveDefaultAddress(anyLong()))
                .willReturn(AddressItem.builder()
                        .isDefault(true)
                        .receiverName("송송이")
                        .address("서울시 성북구 보문로 23")
                        .zipCode("11111")
                        .id(1L)
                        .phone("010-1223-2222")
                        .build());
        //when
        //then
        mockMvc.perform(get("/users/addresses/default")
                        .header("X-USER-ID", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("{methodName}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
    }

    @Test
    void retrieveDefaultAddress_fail_ADDRESS_NOT_FOUND() throws Exception {
        //given
        doThrow(new AddressException(Error.ADDRESS_NOT_FOUND))
                .when(addressService).retrieveDefaultAddress(anyLong());
        //when
        //then
        mockMvc.perform(get("/users/addresses/default")
                        .header("X-USER-ID", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andDo(document("{methodName}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
    }

    @Test
    void retrieveAllAddress_success() throws Exception {
        //given
        given(addressService.retrieveAllAddress(anyLong()))
                .willReturn(List.of(
                        AddressItem.builder()
                                .isDefault(true)
                                .receiverName("송송이")
                                .address("서울시 성북구 보문로 23")
                                .zipCode("11111")
                                .id(1L)
                                .phone("010-1223-2222")
                                .build(),
                        AddressItem.builder()
                                .isDefault(false)
                                .receiverName("안은영")
                                .address("경북 안동")
                                .zipCode("22222")
                                .id(2L)
                                .phone("010-2222-3333")
                                .build()
                ));
        //when
        //then
        mockMvc.perform(get("/users/addresses")
                        .header("X-USER-ID", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("{methodName}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
    }


    @Test
    void deleteAddress_success() throws Exception {
        //given
        //when
        //then
        mockMvc.perform(delete("/users/addresses/{addressId}", 1L)
                        .header("X-USER-ID", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("{methodName}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
    }

    @Test
    void deleteAddress_fail_ADDRESS_NOT_FOUND() throws Exception {
        //given
        doThrow(new AddressException(Error.ADDRESS_NOT_FOUND))
                .when(addressService).deleteAddress(anyLong(), anyLong());
        //when
        //then
        mockMvc.perform(delete("/users/addresses/{addressId}", 1L)
                        .header("X-USER-ID", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andDo(document("{methodName}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
    }

    @Test
    void deleteAddress_fail_ONLY_ADDRESS() throws Exception {
        //given
        doThrow(new AddressException(Error.ONLY_ADDRESS))
                .when(addressService).deleteAddress(anyLong(), anyLong());
        //when
        //then
        mockMvc.perform(delete("/users/addresses/{addressId}", 1L)
                        .header("X-USER-ID", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andDo(document("{methodName}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
    }

    @Test
    void changeDefaultAddress_success() throws Exception {
        //given
        //when
        //then
        mockMvc.perform(put("/users/addresses/{addressId}", 1L)
                        .header("X-USER-ID", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("{methodName}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
    }

    @Test
    void changeDefaultAddress_fail_ADDRESS_NOT_FOUND() throws Exception {
        //given
        doThrow(new AddressException(Error.ADDRESS_NOT_FOUND))
                .when(addressService).deleteAddress(anyLong(), anyLong());
        //when
        //then
        mockMvc.perform(put("/users/addresses/{addressId}", 1L)
                        .header("X-USER-ID", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("{methodName}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
    }

    @Test
    void changeDefaultAddress_fail_ALREADY_DEFAULT_ADDRESS() throws Exception {
        //given
        doThrow(new AddressException(Error.ALREADY_DEFAULT_ADDRESS))
                .when(addressService).deleteAddress(anyLong(), anyLong());
        //when
        //then
        mockMvc.perform(put("/users/addresses/{addressId}", 1L)
                        .header("X-USER-ID", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("{methodName}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
    }

}