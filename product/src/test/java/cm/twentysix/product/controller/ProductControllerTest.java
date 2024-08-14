package cm.twentysix.product.controller;

import cm.twentysix.product.dto.CreateProductForm;
import cm.twentysix.product.dto.UpdateProductForm;
import cm.twentysix.product.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ProductService productService;

    @Test
    void createProduct_success() throws Exception {
        //given
        CreateProductForm form = new CreateProductForm(1L, "123edsf3fdsfdsa3560fdg", "NBGCEFW701 / 글로시 리본 더플백 (VIORET)", "(주)이랜드월드 뉴발란스 사업부", "중국", "뉴발란스 고객 상담실 (080-999-0456)", 69900, 200, 0, 2500);
        //when
        //then
        mockMvc.perform(multipart(HttpMethod.POST, "/products")
                        .file(new MockMultipartFile("form", "json",
                                MediaType.APPLICATION_JSON_VALUE,
                                objectMapper.writeValueAsString(form).getBytes(StandardCharsets.UTF_8)))
                        .file(new MockMultipartFile("thumbnail", "image.jpg",
                                MediaType.IMAGE_JPEG_VALUE, "abcde".getBytes()))
                        .file(new MockMultipartFile("descriptionImage", "image.jpg",
                                MediaType.IMAGE_JPEG_VALUE, "abcde".getBytes()))
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .header("X-USER-ID", 1L))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void createProduct_fail() throws Exception {
        //given
        CreateProductForm form = new CreateProductForm(null, "      ", "", "In a @WebMvcTest setup, if you encounter errors related to unnecessary bean dependencies, it often means there might be some implicit dependencies or configurations that the test context is trying to resolve. This usually occurs when the test context is expecting certain beans that aren't explicitly mocked or provided. It is essential to ensure that only the necessary beans for the controller under test are included and correctly mocked to avoid such dependency issues.", "very long long long long long country nameeeee", "AS센터", -1, -1, 101, 100001);
        //when
        //then
        mockMvc.perform(multipart(HttpMethod.POST, "/products")
                        .file(new MockMultipartFile("form", "json",
                                MediaType.APPLICATION_JSON_VALUE,
                                objectMapper.writeValueAsString(form).getBytes(StandardCharsets.UTF_8)))
                        .file(new MockMultipartFile("thumbnail", "image.jpg",
                                MediaType.IMAGE_JPEG_VALUE, "abcde".getBytes()))
                        .file(new MockMultipartFile("descriptionImage", "image.jpg",
                                MediaType.IMAGE_JPEG_VALUE, "abcde".getBytes()))
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .header("X-USER-ID", 1L))
                .andDo(print())
                .andExpectAll(status().isBadRequest(),
                        jsonPath("$.message.brandId").value("브랜드 id는 비어 있을 수 없습니다."),
                        jsonPath("$.message.categoryId").value("카테고리 id는 비어 있을 수 없습니다."),
                        jsonPath("$.message.name").value("이름은 50글자 이내, 1글자 이상입니다."),
                        jsonPath("$.message.manufacturer").value("제조자는 50글자 이내, 1글자 이상입니다."),
                        jsonPath("$.message.countryOfManufacture").value("제조국은 50글자 이내, 1글자 이상입니다."),
                        jsonPath("$.message.contact").value("A/S 책임자와 전화번호는 50글자 이내, 1글자 이상입니다."),
                        jsonPath("$.message.price").value("가격은 0원 이상입니다."),
                        jsonPath("$.message.amount").value("재고는 0개 이상입니다."),
                        jsonPath("$.message.discount").value("할인율은 100 퍼센트 이하입니다."),
                        jsonPath("$.message.deliveryFee").value("기본 배송비는 10000원 이하입니다.")
                );
    }

    @Test
    void updateProduct_success() throws Exception {
        //given
        UpdateProductForm form = new UpdateProductForm("123edsf3fdsfdsa3560fdg", "NBGCEFW701 / 글로시 리본 더플백 (VIORET)", "(주)이랜드월드 뉴발란스 사업부", "중국", "뉴발란스 고객 상담실 (080-999-0456)", 69900, 200, 0, 2500);
        //when
        //then
        mockMvc.perform(multipart(HttpMethod.PUT, "/products/{productId}", "abcdefkghaskfldlm123")
                        .file(new MockMultipartFile("form", "json",
                                MediaType.APPLICATION_JSON_VALUE,
                                objectMapper.writeValueAsString(form).getBytes(StandardCharsets.UTF_8)))
                        .file(new MockMultipartFile("thumbnail", "image.jpg",
                                MediaType.IMAGE_JPEG_VALUE, "abcde".getBytes()))
                        .file(new MockMultipartFile("descriptionImage", "image.jpg",
                                MediaType.IMAGE_JPEG_VALUE, "abcde".getBytes()))
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .header("X-USER-ID", 1L))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void updateProduct_fail() throws Exception {
        //given
        UpdateProductForm form = new UpdateProductForm("      ", "", "In a @WebMvcTest setup, if you encounter errors related to unnecessary bean dependencies, it often means there might be some implicit dependencies or configurations that the test context is trying to resolve. This usually occurs when the test context is expecting certain beans that aren't explicitly mocked or provided. It is essential to ensure that only the necessary beans for the controller under test are included and correctly mocked to avoid such dependency issues.", "very long long long long long country nameeeee", "AS센터", -1, -1, 101, 100001);
        //when
        //then
        mockMvc.perform(multipart(HttpMethod.PUT, "/products/{productId}", "abcdefkghaskfldlm123")
                        .file(new MockMultipartFile("form", "json",
                                MediaType.APPLICATION_JSON_VALUE,
                                objectMapper.writeValueAsString(form).getBytes(StandardCharsets.UTF_8)))
                        .file(new MockMultipartFile("thumbnail", "image.jpg",
                                MediaType.IMAGE_JPEG_VALUE, "abcde".getBytes()))
                        .file(new MockMultipartFile("descriptionImage", "image.jpg",
                                MediaType.IMAGE_JPEG_VALUE, "abcde".getBytes()))
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .header("X-USER-ID", 1L))
                .andDo(print())
                .andExpectAll(status().isBadRequest(),
                        jsonPath("$.message.categoryId").value("카테고리 id는 비어 있을 수 없습니다."),
                        jsonPath("$.message.name").value("이름은 50글자 이내, 1글자 이상입니다."),
                        jsonPath("$.message.manufacturer").value("제조자는 50글자 이내, 1글자 이상입니다."),
                        jsonPath("$.message.countryOfManufacture").value("제조국은 50글자 이내, 1글자 이상입니다."),
                        jsonPath("$.message.contact").value("A/S 책임자와 전화번호는 50글자 이내, 1글자 이상입니다."),
                        jsonPath("$.message.price").value("가격은 0원 이상입니다."),
                        jsonPath("$.message.amount").value("재고는 0개 이상입니다."),
                        jsonPath("$.message.discount").value("할인율은 100 퍼센트 이하입니다."),
                        jsonPath("$.message.deliveryFee").value("기본 배송비는 10000원 이하입니다.")
                );
    }

    @Test
    void deleteProduct_success() throws Exception {
        //given
        //when
        //then
        mockMvc.perform(delete("/products/{productId}", "abcdefkghaskfldlm123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-USER-ID", 1L))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void retrieveProduct_success() throws Exception {
        //given
        //when
        //then
        mockMvc.perform(get("/products/{productId}", "abcdefkghaskfldlm123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-USER-ID", 1L)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void retrieveProducts_success() throws Exception {
        //given
        //when
        //then
        mockMvc.perform(get("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("page", "0")
                        .param("size", "26")
                        .header("X-USER-ID", 1L)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

}