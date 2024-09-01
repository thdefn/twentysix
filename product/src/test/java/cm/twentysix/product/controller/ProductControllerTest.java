package cm.twentysix.product.controller;

import cm.twentysix.product.dto.*;
import cm.twentysix.product.exception.Error;
import cm.twentysix.product.exception.ProductException;
import cm.twentysix.product.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.relaxedQueryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
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
        CreateProductForm form = new CreateProductForm(1L, "123edsf3fdsfdsa3560fdg", "NBGCEFW701 / 글로시 리본 더플백 (VIORET)", "(주)이랜드월드 뉴발란스 사업부", "중국", "뉴발란스 고객 상담실 (080-999-0456)", 69900, 200, 0, 2500, null);
        //when
        //then
        mockMvc.perform(multipart("/products")
                        .file(new MockMultipartFile("form", "json",
                                MediaType.APPLICATION_JSON_VALUE,
                                objectMapper.writeValueAsString(form).getBytes(StandardCharsets.UTF_8)))
                        .file(new MockMultipartFile("thumbnail", "image.jpg",
                                MediaType.IMAGE_JPEG_VALUE, "abcde".getBytes()))
                        .file(new MockMultipartFile("descriptionImage", "image.jpg",
                                MediaType.IMAGE_JPEG_VALUE, "abcde".getBytes()))
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .header("X-USER-ID", 1L)
                        .header(AUTHORIZATION, "Bearer eyJhbGciOiJIUzI1NiJ9.")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("{methodName}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName(AUTHORIZATION)
                                .description("Bearer token for authorization"))
                ));
    }

    @Test
    void createProduct_fail_REQUEST_ARGUMENT_NOT_VALID() throws Exception {
        //given
        CreateProductForm form = new CreateProductForm(null, "      ", "", "In a @WebMvcTest setup, if you encounter errors related to unnecessary bean dependencies, it often means there might be some implicit dependencies or configurations that the test context is trying to resolve. This usually occurs when the test context is expecting certain beans that aren't explicitly mocked or provided. It is essential to ensure that only the necessary beans for the controller under test are included and correctly mocked to avoid such dependency issues.", "very long long long long long country nameeeee", "AS센터", -1, -1, 101, 100001, "1234");
        //when
        //then
        mockMvc.perform(multipart("/products")
                        .file(new MockMultipartFile("form", "json",
                                MediaType.APPLICATION_JSON_VALUE,
                                objectMapper.writeValueAsString(form).getBytes(StandardCharsets.UTF_8)))
                        .file(new MockMultipartFile("thumbnail", "image.jpg",
                                MediaType.IMAGE_JPEG_VALUE, "abcde".getBytes()))
                        .file(new MockMultipartFile("descriptionImage", "image.jpg",
                                MediaType.IMAGE_JPEG_VALUE, "abcde".getBytes()))
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .header("X-USER-ID", 1L)
                        .header(AUTHORIZATION, "Bearer eyJhbGciOiJIUzI1NiJ9.")
                )
                .andDo(print())
                .andExpectAll(status().isBadRequest(),
                        jsonPath("$.message.brandId").value("브랜드 id는 비어 있을 수 없습니다."),
                        jsonPath("$.message.categoryId").value("카테고리 id는 비어 있을 수 없습니다."),
                        jsonPath("$.message.name").value("이름은 50글자 이내, 1글자 이상입니다."),
                        jsonPath("$.message.manufacturer").value("제조자는 50글자 이내, 1글자 이상입니다."),
                        jsonPath("$.message.countryOfManufacture").value("제조국은 50글자 이내, 1글자 이상입니다."),
                        jsonPath("$.message.contact").value("A/S 책임자와 전화번호는 50글자 이내, 1글자 이상입니다."),
                        jsonPath("$.message.price").value("가격은 0원 이상입니다."),
                        jsonPath("$.message.quantity").value("재고는 0개 이상입니다."),
                        jsonPath("$.message.discount").value("할인율은 100 퍼센트 이하입니다."),
                        jsonPath("$.message.deliveryFee").value("기본 배송비는 10000원 이하입니다."),
                        jsonPath("$.message.orderingOpensAt").value("과거이거나 datetime 형식이 아닙니다.")
                )
                .andDo(document("{methodName}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName(AUTHORIZATION)
                                .description("Bearer token for authorization"))
                ));
    }

    @Test
    void createProduct_fail_NOT_PRODUCT_ADMIN() throws Exception {
        //given
        CreateProductForm form = new CreateProductForm(1L, "123edsf3fdsfdsa3560fdg", "NBGCEFW701 / 글로시 리본 더플백 (VIORET)", "(주)이랜드월드 뉴발란스 사업부", "중국", "뉴발란스 고객 상담실 (080-999-0456)", 69900, 200, 0, 2500, null);
        doThrow(new ProductException(Error.NOT_PRODUCT_ADMIN))
                .when(productService).createProduct(any(), any(), any(), anyLong());
        //when
        //then
        mockMvc.perform(multipart("/products")
                        .file(new MockMultipartFile("form", "json",
                                MediaType.APPLICATION_JSON_VALUE,
                                objectMapper.writeValueAsString(form).getBytes(StandardCharsets.UTF_8)))
                        .file(new MockMultipartFile("thumbnail", "image.jpg",
                                MediaType.IMAGE_JPEG_VALUE, "abcde".getBytes()))
                        .file(new MockMultipartFile("descriptionImage", "image.jpg",
                                MediaType.IMAGE_JPEG_VALUE, "abcde".getBytes()))
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .header("X-USER-ID", 1L)
                        .header(AUTHORIZATION, "Bearer eyJhbGciOiJIUzI1NiJ9.")
                )
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andDo(document("{methodName}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName(AUTHORIZATION)
                                .description("Bearer token for authorization"))
                ));
    }

    @Test
    void updateProduct_success() throws Exception {
        //given
        UpdateProductForm form = new UpdateProductForm("123edsf3fdsfdsa3560fdg", "NBGCEFW701 / 글로시 리본 더플백 (VIORET)", "(주)이랜드월드 뉴발란스 사업부", "중국", "뉴발란스 고객 상담실 (080-999-0456)", 69900, 200, 0, 2500, LocalDateTime.MAX.toString());
        //when
        //then
        mockMvc.perform(multipart("/products/{productId}", "abcdefkghaskfldlm123")
                        .file(new MockMultipartFile("form", "json",
                                MediaType.APPLICATION_JSON_VALUE,
                                objectMapper.writeValueAsString(form).getBytes(StandardCharsets.UTF_8)))
                        .file(new MockMultipartFile("thumbnail", "image.jpg",
                                MediaType.IMAGE_JPEG_VALUE, "abcde".getBytes()))
                        .file(new MockMultipartFile("descriptionImage", "image.jpg",
                                MediaType.IMAGE_JPEG_VALUE, "abcde".getBytes()))
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .header("X-USER-ID", 1L)
                        .header(AUTHORIZATION, "Bearer eyJhbGciOiJIUzI1NiJ9.")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("{methodName}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName(AUTHORIZATION)
                                .description("Bearer token for authorization"))
                ));
    }

    @Test
    void updateProduct_fail_REQUEST_ARGUMENT_NOT_VALID() throws Exception {
        //given
        UpdateProductForm form = new UpdateProductForm("      ", "", "In a @WebMvcTest setup, if you encounter errors related to unnecessary bean dependencies, it often means there might be some implicit dependencies or configurations that the test context is trying to resolve. This usually occurs when the test context is expecting certain beans that aren't explicitly mocked or provided. It is essential to ensure that only the necessary beans for the controller under test are included and correctly mocked to avoid such dependency issues.", "very long long long long long country nameeeee", "AS센터", -1, -1, 101, 100001, "1234");
        //when
        //then
        mockMvc.perform(multipart("/products/{productId}", "abcdefkghaskfldlm123")
                        .file(new MockMultipartFile("form", "json",
                                MediaType.APPLICATION_JSON_VALUE,
                                objectMapper.writeValueAsString(form).getBytes(StandardCharsets.UTF_8)))
                        .file(new MockMultipartFile("thumbnail", "image.jpg",
                                MediaType.IMAGE_JPEG_VALUE, "abcde".getBytes()))
                        .file(new MockMultipartFile("descriptionImage", "image.jpg",
                                MediaType.IMAGE_JPEG_VALUE, "abcde".getBytes()))
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .header("X-USER-ID", 1L)
                        .header(AUTHORIZATION, "Bearer eyJhbGciOiJIUzI1NiJ9.")
                )
                .andDo(print())
                .andExpectAll(status().isBadRequest(),
                        jsonPath("$.message.categoryId").value("카테고리 id는 비어 있을 수 없습니다."),
                        jsonPath("$.message.name").value("이름은 50글자 이내, 1글자 이상입니다."),
                        jsonPath("$.message.manufacturer").value("제조자는 50글자 이내, 1글자 이상입니다."),
                        jsonPath("$.message.countryOfManufacture").value("제조국은 50글자 이내, 1글자 이상입니다."),
                        jsonPath("$.message.contact").value("A/S 책임자와 전화번호는 50글자 이내, 1글자 이상입니다."),
                        jsonPath("$.message.price").value("가격은 0원 이상입니다."),
                        jsonPath("$.message.quantity").value("재고는 0개 이상입니다."),
                        jsonPath("$.message.discount").value("할인율은 100 퍼센트 이하입니다."),
                        jsonPath("$.message.deliveryFee").value("기본 배송비는 10000원 이하입니다."),
                        jsonPath("$.message.orderingOpensAt").value("과거이거나 datetime 형식이 아닙니다.")
                )
                .andDo(document("{methodName}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName(AUTHORIZATION)
                                .description("Bearer token for authorization"))
                        ));
    }

    @Test
    void updateProduct_fail_PRODUCT_NOT_FOUND() throws Exception {
        //given
        UpdateProductForm form = new UpdateProductForm("123edsf3fdsfdsa3560fdg", "NBGCEFW701 / 글로시 리본 더플백 (VIORET)", "(주)이랜드월드 뉴발란스 사업부", "중국", "뉴발란스 고객 상담실 (080-999-0456)", 69900, 200, 0, 2500, LocalDateTime.MAX.toString());
        doThrow(new ProductException(Error.PRODUCT_NOT_FOUND))
                .when(productService).updateProduct(anyString(), any(), any(), any(), anyLong());
        //when
        //then
        mockMvc.perform(multipart("/products/{productId}", "abcdefkghaskfldlm123")
                        .file(new MockMultipartFile("form", "json",
                                MediaType.APPLICATION_JSON_VALUE,
                                objectMapper.writeValueAsString(form).getBytes(StandardCharsets.UTF_8)))
                        .file(new MockMultipartFile("thumbnail", "image.jpg",
                                MediaType.IMAGE_JPEG_VALUE, "abcde".getBytes()))
                        .file(new MockMultipartFile("descriptionImage", "image.jpg",
                                MediaType.IMAGE_JPEG_VALUE, "abcde".getBytes()))
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .header("X-USER-ID", 1L)
                        .header(AUTHORIZATION, "Bearer eyJhbGciOiJIUzI1NiJ9.")
                )
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andDo(document("{methodName}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName(AUTHORIZATION)
                                .description("Bearer token for authorization"))
                ));
    }

    @Test
    void updateProduct_fail_NOT_PRODUCT_ADMIN() throws Exception {
        //given
        UpdateProductForm form = new UpdateProductForm("123edsf3fdsfdsa3560fdg", "NBGCEFW701 / 글로시 리본 더플백 (VIORET)", "(주)이랜드월드 뉴발란스 사업부", "중국", "뉴발란스 고객 상담실 (080-999-0456)", 69900, 200, 0, 2500, LocalDateTime.MAX.toString());
        doThrow(new ProductException(Error.NOT_PRODUCT_ADMIN))
                .when(productService).updateProduct(anyString(), any(), any(), any(), anyLong());
        //when
        //then
        mockMvc.perform(multipart("/products/{productId}", "abcdefkghaskfldlm123")
                        .file(new MockMultipartFile("form", "json",
                                MediaType.APPLICATION_JSON_VALUE,
                                objectMapper.writeValueAsString(form).getBytes(StandardCharsets.UTF_8)))
                        .file(new MockMultipartFile("thumbnail", "image.jpg",
                                MediaType.IMAGE_JPEG_VALUE, "abcde".getBytes()))
                        .file(new MockMultipartFile("descriptionImage", "image.jpg",
                                MediaType.IMAGE_JPEG_VALUE, "abcde".getBytes()))
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .header("X-USER-ID", 1L)
                        .header(AUTHORIZATION, "Bearer eyJhbGciOiJIUzI1NiJ9.")
                )
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andDo(document("{methodName}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName(AUTHORIZATION)
                                .description("Bearer token for authorization"))
                ));
    }

    @Test
    void updateProduct_fail_ALREADY_DELETED_PRODUCT() throws Exception {
        //given
        UpdateProductForm form = new UpdateProductForm("123edsf3fdsfdsa3560fdg", "NBGCEFW701 / 글로시 리본 더플백 (VIORET)", "(주)이랜드월드 뉴발란스 사업부", "중국", "뉴발란스 고객 상담실 (080-999-0456)", 69900, 200, 0, 2500, LocalDateTime.MAX.toString());
        doThrow(new ProductException(Error.ALREADY_DELETED_PRODUCT))
                .when(productService).updateProduct(anyString(), any(), any(), any(), anyLong());
        //when
        //then
        mockMvc.perform(multipart("/products/{productId}", "abcdefkghaskfldlm123")
                        .file(new MockMultipartFile("form", "json",
                                MediaType.APPLICATION_JSON_VALUE,
                                objectMapper.writeValueAsString(form).getBytes(StandardCharsets.UTF_8)))
                        .file(new MockMultipartFile("thumbnail", "image.jpg",
                                MediaType.IMAGE_JPEG_VALUE, "abcde".getBytes()))
                        .file(new MockMultipartFile("descriptionImage", "image.jpg",
                                MediaType.IMAGE_JPEG_VALUE, "abcde".getBytes()))
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .header("X-USER-ID", 1L)
                        .header(AUTHORIZATION, "Bearer eyJhbGciOiJIUzI1NiJ9.")
                )
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andDo(document("{methodName}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName(AUTHORIZATION)
                                .description("Bearer token for authorization"))
                ));
    }


    @Test
    void deleteProduct_success() throws Exception {
        //given
        //when
        //then
        mockMvc.perform(delete("/products/{productId}", "abcdefkghaskfldlm123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-USER-ID", 1L)
                        .header(AUTHORIZATION, "Bearer eyJhbGciOiJIUzI1NiJ9.")
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void deleteProduct_fail_PRODUCT_NOT_FOUND() throws Exception {
        //given
        doThrow(new ProductException(Error.ALREADY_DELETED_PRODUCT))
                .when(productService).deleteProduct(anyString(), anyLong());
        //when
        //then
        mockMvc.perform(delete("/products/{productId}", "abcdefkghaskfldlm123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-USER-ID", 1L)
                        .header(AUTHORIZATION, "Bearer token")
                )
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andDo(document("{methodName}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName(AUTHORIZATION)
                                .description("Bearer token for authorization"))
                ));
    }


    @Test
    void deleteProduct_fail_NOT_PRODUCT_ADMIN() throws Exception {
        //given
        doThrow(new ProductException(Error.NOT_PRODUCT_ADMIN))
                .when(productService).deleteProduct(anyString(), anyLong());
        //when
        //then
        mockMvc.perform(delete("/products/{productId}", "abcdefkghaskfldlm123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-USER-ID", 1L)
                        .header(AUTHORIZATION, "Bearer eyJhbGciOiJIUzI1NiJ9.")
                )
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andDo(document("{methodName}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName(AUTHORIZATION)
                                .description("Bearer token for authorization"))
                ));
    }

    @Test
    void deleteProduct_fail_ALREADY_DELETED_PRODUCT() throws Exception {
        //given
        doThrow(new ProductException(Error.ALREADY_DELETED_PRODUCT))
                .when(productService).deleteProduct(anyString(), anyLong());
        //when
        //then
        mockMvc.perform(delete("/products/{productId}", "abcdefkghaskfldlm123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-USER-ID", 1L)
                        .header(AUTHORIZATION, "Bearer eyJhbGciOiJIUzI1NiJ9.")
                )
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andDo(document("{methodName}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName(AUTHORIZATION)
                                .description("Bearer token for authorization"))
                ));
    }

    @Test
    void retrieveProduct_success() throws Exception {
        //given
        given(productService.retrieveProduct(anyString(), any())).willReturn(
                ProductResponse.builder()
                        .name("강아지 하네스")
                        .brand(ProductBrandResponse.builder()
                                .name("돌봄")
                                .legalName("(주)돌봄")
                                .brandId(1L)
                                .freeDeliveryInfimum(10000)
                                .introduction("강아지가 행복한 세상을 만듭니다.")
                                .registrationNumber("111-12-12323")
                                .thumbnail("/12356.jpg")
                                .build()
                        )
                        .categories(List.of("반려용품", "강아지", "산책"))
                        .price(20000)
                        .isUserLike(true)
                        .isFreeDelivery(true)
                        .discount(0)
                        .discountedPrice(20000)
                        .orderingOpensAt(LocalDateTime.of(2024, 7, 7, 7, 7, 7, 7))
                        .thumbnailPath("/22222.jpg")
                        .descriptionImagePath("/33333.jpg")
                        .id("abcdefkghaskfldlm123")
                        .isOpen(true)
                        .info(ProductInfoResponse.builder()
                                .contact("010-1111-1111")
                                .countryOfManufacture("중국")
                                .manufacturer("돌봄")
                                .build())
                        .build()
        );
        //when
        //then
        mockMvc.perform(get("/products/{productId}", "abcdefkghaskfldlm123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-USER-ID", 1L)
                        .header(AUTHORIZATION, "Bearer eyJhbGciOiJIUzI1NiJ9.")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("{methodName}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName(AUTHORIZATION)
                                .optional()
                                .description("Bearer token for authorization"))
                ));
    }

    @Test
    void retrieveProduct_fail_PRODUCT_NOT_FOUND() throws Exception {
        //given
        doThrow(new ProductException(Error.PRODUCT_NOT_FOUND))
                .when(productService).retrieveProduct(anyString(), any());
        //when
        //then
        mockMvc.perform(get("/products/{productId}", "abcdefkghaskfldlm123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-USER-ID", 1L)
                        .header(AUTHORIZATION, "Bearer eyJhbGciOiJIUzI1NiJ9.")
                )
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andDo(document("{methodName}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
    }

    @Test
    void retrieveProductList_success() throws Exception {
        //given
        given(productService.retrieveProducts(anyInt(), anyInt(), any())).willReturn(
                List.of(
                        ProductItem.builder()
                                .price(20000)
                                .isUserLike(true)
                                .isFreeDelivery(true)
                                .discount(0)
                                .thumbnailPath("/22222.jpg")
                                .id("abcdefkghaskfldlm123")
                                .isOpen(true)
                                .brandId(1L)
                                .countOfLikes(1L)
                                .brandName("돌봄")
                                .build(),
                        ProductItem.builder()
                                .price(30000)
                                .isUserLike(false)
                                .isFreeDelivery(true)
                                .discount(10)
                                .thumbnailPath("/33333.jpg")
                                .id("qewreqrtuoyiyopeqwoo")
                                .isOpen(true)
                                .brandId(2L)
                                .brandName("디어")
                                .countOfLikes(2L)
                                .build()
                )
        );
        //when
        //then
        mockMvc.perform(get("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("page", "0")
                        .param("size", "26")
                        .header("X-USER-ID", 1L)
                        .header(AUTHORIZATION, "Bearer eyJhbGciOiJIUzI1NiJ9.")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("{methodName}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName(AUTHORIZATION)
                                .optional()
                                .description("Bearer token for authorization")),
                        relaxedQueryParameters(
                                parameterWithName("page").optional().description("number of index"),
                                parameterWithName("size").optional().description("number of items per page")
                        )
                ));
    }


}