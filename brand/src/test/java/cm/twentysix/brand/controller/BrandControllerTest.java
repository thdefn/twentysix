package cm.twentysix.brand.controller;

import cm.twentysix.brand.dto.CreateBrandForm;
import cm.twentysix.brand.dto.UpdateBrandForm;
import cm.twentysix.brand.exception.BrandException;
import cm.twentysix.brand.service.BrandService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;

import static cm.twentysix.brand.exception.Error.*;
import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BrandController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
class BrandControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private BrandService brandService;

    @Test
    @DisplayName("브랜드 생성 성공")
    void createBrand_success() throws Exception {
        //given
        CreateBrandForm form = new CreateBrandForm("아이캔더", "주식회사 캔더스", "000-00-00000", 3500, 50000, "우리와 함께 사는 반려동물이 언제 어디서든 더욱 편안하고, 안전하며, 행복하게 살도록 하는 것을 목표로 하는 브랜드입니다");
        //when
        //then
        mockMvc.perform(multipart("/brands", HttpMethod.POST)
                        .file(new MockMultipartFile("form", "json",
                                MediaType.APPLICATION_JSON_VALUE,
                                objectMapper.writeValueAsString(form).getBytes(StandardCharsets.UTF_8)))
                        .file(new MockMultipartFile("image", "image.jpg",
                                MediaType.IMAGE_JPEG_VALUE, "abcde".getBytes()))
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .header(AUTHORIZATION, "Bearer eyJhbGciOiJIUzI1NiJ9.")
                        .header("X-USER-ID", 1L))
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
    @DisplayName("브랜드 생성 실패")
    void createBrand_fail_REQUEST_ARGUMENT_NOT_VALID() throws Exception {
        //given
        CreateBrandForm form = new CreateBrandForm("", "", "000200500000", -1, 10000001,
                "이것은 결국 로봇의 이야기다. 사물에 깃든 생명에 바치는 경애다. 종의 기원담 1편을 쓰기 시작한 것이 2000년 즈음이었다. 그때 스물다섯 살이었다. 완성한 해는 2005년이었고 서른 살이었다. 2편은 그해에 써서 완성했다. 3편은 올해 완성했고 지금 나는 마흔여덟 살이다. 그러니 이 세 편은 각기 다른 이야기다. 세 편을 쓴 사람 각각이 다른 사람이라고 봐도 과언이 아니다. 그저 사람이 나이가 들어가며 같은 주제에 대한 관점이 변해가는 과정으로 보아주셨으면 한다. 부디 이야기를 자신에게 익숙한 세상에 맞추기 위해, 모든 것을 은유로 보며 눈에 보이는 단어를 다른 단어로 치환하려 애쓰지는 말기 바란다. 단어는 눈에 보이는 단어 그대로의 뜻이다. 이것은 결국 로봇의 이야기다. 무기생명에 대한 내 개인적인 헌사며, 곧이곧대로 기계생명을 향한 찬가다. 사물에 깃든 생명에 바치는 경애다. 1편에서 원래 주석을 많이 넣으려다가, 소설에서 설명할 수 없다면 무의미하다는 생각에 다 빼었는데, 3편에서는 그런 부분들도 조금은 풀어놓았다. 대부분은 로봇의 지식이 불완전하다는 것이다. 1편과 2편에서 뒤늦게 발견한 모순과 오류들도 이번에 여러 군데 수정했다. 〈종의 기원〉이었던 원래 제목도 너무 많이 쓰이는 듯하여 〈종의 기원담〉으로 수정했다.");
        //when
        //then
        mockMvc.perform(multipart("/brands", HttpMethod.POST)
                        .file(new MockMultipartFile("form", "json",
                                MediaType.APPLICATION_JSON_VALUE,
                                objectMapper.writeValueAsString(form).getBytes(StandardCharsets.UTF_8)))
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .header(AUTHORIZATION, "Bearer eyJhbGciOiJIUzI1NiJ9.")
                        .header("X-USER-ID", 1L))
                .andDo(print())
                .andExpectAll(status().isBadRequest(),
                        jsonPath("$.message.name").value("이름은 50글자 이내, 1글자 이상입니다."),
                        jsonPath("$.message.legalName").value("상호명은 50글자 이내, 1글자 이상입니다."),
                        jsonPath("$.message.registrationNumber").value("사업자등록번호 형식이 아닙니다."),
                        jsonPath("$.message.deliveryFee").value("기본 배송비는 0원 이상입니다."),
                        jsonPath("$.message.freeDeliveryInfimum").value("무료 배송 하한은 1000만원 이하입니다."),
                        jsonPath("$.message.introduction").value("상호명은 255글자 이내입니다.")
                )
                .andDo(document("{methodName}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName(HttpHeaders.AUTHORIZATION)
                                .description("Bearer token for authorization"))
                ));
    }

    @Test
    @DisplayName("브랜드 생성 실패")
    void createBrand_fail_BRAND_LIMIT_OVER() throws Exception {
        //given
        CreateBrandForm form = new CreateBrandForm("아이캔더", "주식회사 캔더스", "000-00-00000", 3500, 50000, "우리와 함께 사는 반려동물이 언제 어디서든 더욱 편안하고, 안전하며, 행복하게 살도록 하는 것을 목표로 하는 브랜드입니다");
        doThrow(new BrandException(BRAND_LIMIT_OVER))
                .when(brandService).createBrand(anyLong(), any(), any());
        //when
        //then
        mockMvc.perform(multipart("/brands", HttpMethod.POST)
                        .file(new MockMultipartFile("form", "json",
                                MediaType.APPLICATION_JSON_VALUE,
                                objectMapper.writeValueAsString(form).getBytes(StandardCharsets.UTF_8)))
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .header(AUTHORIZATION, "Bearer eyJhbGciOiJIUzI1NiJ9.")
                        .header("X-USER-ID", 1L))
                .andDo(print())
                .andExpectAll(status().is4xxClientError())
                .andDo(document("{methodName}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName(HttpHeaders.AUTHORIZATION)
                                .description("Bearer token for authorization"))
                ));
    }

    @Test
    @DisplayName("브랜드 수정 성공")
    void updateBrand_success() throws Exception {
        //given
        UpdateBrandForm form = new UpdateBrandForm("아이캔더", "주식회사 캔더스", 3500, 50000, "우리와 함께 사는 반려동물이 언제 어디서든 더욱 편안하고, 안전하며, 행복하게 살도록 하는 것을 목표로 하는 브랜드입니다");
        //when
        //then
        mockMvc.perform(multipart("/brands/{brandId}", 1L)
                        .file(new MockMultipartFile("form", "json",
                                MediaType.APPLICATION_JSON_VALUE,
                                objectMapper.writeValueAsString(form).getBytes(StandardCharsets.UTF_8)))
                        .file(new MockMultipartFile("image", "image.jpg",
                                MediaType.IMAGE_JPEG_VALUE, "abcde".getBytes()))
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .header(AUTHORIZATION, "Bearer eyJhbGciOiJIUzI1NiJ9.")
                        .header("X-USER-ID", 1L))
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
    @DisplayName("브랜드 수정 실패")
    void updateBrand_fail_REQUEST_ARGUMENT_NOT_VALID() throws Exception {
        //given
        UpdateBrandForm form = new UpdateBrandForm("", "", -1, 10000001,
                "이것은 결국 로봇의 이야기다. 사물에 깃든 생명에 바치는 경애다. 종의 기원담 1편을 쓰기 시작한 것이 2000년 즈음이었다. 그때 스물다섯 살이었다. 완성한 해는 2005년이었고 서른 살이었다. 2편은 그해에 써서 완성했다. 3편은 올해 완성했고 지금 나는 마흔여덟 살이다. 그러니 이 세 편은 각기 다른 이야기다. 세 편을 쓴 사람 각각이 다른 사람이라고 봐도 과언이 아니다. 그저 사람이 나이가 들어가며 같은 주제에 대한 관점이 변해가는 과정으로 보아주셨으면 한다. 부디 이야기를 자신에게 익숙한 세상에 맞추기 위해, 모든 것을 은유로 보며 눈에 보이는 단어를 다른 단어로 치환하려 애쓰지는 말기 바란다. 단어는 눈에 보이는 단어 그대로의 뜻이다. 이것은 결국 로봇의 이야기다. 무기생명에 대한 내 개인적인 헌사며, 곧이곧대로 기계생명을 향한 찬가다. 사물에 깃든 생명에 바치는 경애다. 1편에서 원래 주석을 많이 넣으려다가, 소설에서 설명할 수 없다면 무의미하다는 생각에 다 빼었는데, 3편에서는 그런 부분들도 조금은 풀어놓았다. 대부분은 로봇의 지식이 불완전하다는 것이다. 1편과 2편에서 뒤늦게 발견한 모순과 오류들도 이번에 여러 군데 수정했다. 〈종의 기원〉이었던 원래 제목도 너무 많이 쓰이는 듯하여 〈종의 기원담〉으로 수정했다.");
        //when
        //then
        mockMvc.perform(multipart("/brands/{brandId}", 1L, HttpMethod.PUT)
                        .file(new MockMultipartFile("form", "json",
                                MediaType.APPLICATION_JSON_VALUE,
                                objectMapper.writeValueAsString(form).getBytes(StandardCharsets.UTF_8)))
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .header(AUTHORIZATION, "Bearer eyJhbGciOiJIUzI1NiJ9.")
                        .header("X-USER-ID", 1L))
                .andDo(print())
                .andExpectAll(status().isBadRequest(),
                        jsonPath("$.message.name").value("이름은 50글자 이내, 1글자 이상입니다."),
                        jsonPath("$.message.legalName").value("상호명은 50글자 이내, 1글자 이상입니다."),
                        jsonPath("$.message.deliveryFee").value("기본 배송비는 0원 이상입니다."),
                        jsonPath("$.message.freeDeliveryInfimum").value("무료 배송 하한은 1000만원 이하입니다."),
                        jsonPath("$.message.introduction").value("상호명은 255글자 이내입니다.")
                )
                .andDo(document("{methodName}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName(HttpHeaders.AUTHORIZATION)
                                .description("Bearer token for authorization"))
                ));
    }

    @Test
    @DisplayName("브랜드 수정 실패_BRAND_NOT_FOUND")
    void updateBrand_fail_BRAND_NOT_FOUND() throws Exception {
        //given
        UpdateBrandForm form = new UpdateBrandForm("아이캔더", "주식회사 캔더스", 3500, 50000, "우리와 함께 사는 반려동물이 언제 어디서든 더욱 편안하고, 안전하며, 행복하게 살도록 하는 것을 목표로 하는 브랜드입니다");
        doThrow(new BrandException(BRAND_NOT_FOUND))
                .when(brandService).updateBrand(anyLong(), any(), any(), anyLong());
        //when
        //then
        mockMvc.perform(multipart("/brands/{brandId}", 1L)
                        .file(new MockMultipartFile("form", "json",
                                MediaType.APPLICATION_JSON_VALUE,
                                objectMapper.writeValueAsString(form).getBytes(StandardCharsets.UTF_8)))
                        .file(new MockMultipartFile("image", "image.jpg",
                                MediaType.IMAGE_JPEG_VALUE, "abcde".getBytes()))
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .header(AUTHORIZATION, "Bearer eyJhbGciOiJIUzI1NiJ9.")
                        .header("X-USER-ID", 1L))
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
    @DisplayName("브랜드 수정 실패_NOT_BRAND_OWNER")
    void updateBrand_fail_NOT_BRAND_OWNER() throws Exception {
        //given
        UpdateBrandForm form = new UpdateBrandForm("아이캔더", "주식회사 캔더스", 3500, 50000, "우리와 함께 사는 반려동물이 언제 어디서든 더욱 편안하고, 안전하며, 행복하게 살도록 하는 것을 목표로 하는 브랜드입니다");
        doThrow(new BrandException(NOT_BRAND_OWNER))
                .when(brandService).updateBrand(anyLong(), any(), any(), anyLong());
        //when
        //then
        mockMvc.perform(multipart("/brands/{brandId}", 1L)
                        .file(new MockMultipartFile("form", "json",
                                MediaType.APPLICATION_JSON_VALUE,
                                objectMapper.writeValueAsString(form).getBytes(StandardCharsets.UTF_8)))
                        .file(new MockMultipartFile("image", "image.jpg",
                                MediaType.IMAGE_JPEG_VALUE, "abcde".getBytes()))
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .header(AUTHORIZATION, "Bearer eyJhbGciOiJIUzI1NiJ9.")
                        .header("X-USER-ID", 1L))
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