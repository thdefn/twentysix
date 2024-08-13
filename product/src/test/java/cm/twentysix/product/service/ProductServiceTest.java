package cm.twentysix.product.service;

import cm.twentysix.BrandProto;
import cm.twentysix.product.client.BrandGrpcClient;
import cm.twentysix.product.client.FileStorageClient;
import cm.twentysix.product.domain.model.CategoryInfo;
import cm.twentysix.product.domain.model.Product;
import cm.twentysix.product.domain.model.ProductBrand;
import cm.twentysix.product.domain.model.ProductInfo;
import cm.twentysix.product.domain.repository.ProductRepository;
import cm.twentysix.product.dto.CreateProductForm;
import cm.twentysix.product.dto.UpdateProductForm;
import cm.twentysix.product.exception.Error;
import cm.twentysix.product.exception.ProductException;
import cm.twentysix.product.service.dto.CategoryInfoDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;
    @Mock
    private CategoryService categoryService;
    @Mock
    FileStorageClient fileStorageClient;
    @Mock
    BrandGrpcClient brandGrpcClient;
    @InjectMocks
    ProductService productService;

    List<CategoryInfoDto> categoryInfoDtos = List.of(
            CategoryInfoDto.builder()
                    .categoryId("123edsf3fdsfdsa3560fdg")
                    .name("레인 부츠")
                    .build(),
            CategoryInfoDto.builder()
                    .categoryId("123edsf3fdsfdsa3560fd2")
                    .name("부츠,샌달")
                    .build(),
            CategoryInfoDto.builder()
                    .categoryId("123edsf3fdsfdsa3560fd3")
                    .name("신발")
                    .build(),
            CategoryInfoDto.builder()
                    .categoryId("123edsf3fdsfdsa3560fd5")
                    .name("여성")
                    .build()
    );

    @Test
    void createProduct_success() {
        //given
        MultipartFile thumbnail = new MockMultipartFile("abc.jpg", "abcd".getBytes());
        MultipartFile descriptionImage = new MockMultipartFile("abc.jpg", "abcd".getBytes());
        CreateProductForm form = new CreateProductForm(1L, "123edsf3fdsfdsa3560fdg", "NBGCEFW701 / 글로시 리본 더플백 (VIORET)", "(주)이랜드월드 뉴발란스 사업부", "중국", "뉴발란스 고객 상담실 (080-999-0456)", 69900, 200, 0, 2500);
        given(categoryService.retrieveBelongingCategories(form.categoryId())).willReturn(categoryInfoDtos);
        given(brandGrpcClient.getBrandInfo(anyLong())).willReturn(BrandProto.BrandResponse.newBuilder().setId(1L).setName("뉴발란스").setUserId(1L).build());
        given(fileStorageClient.upload((MultipartFile) any(), any())).willReturn("afsdfasfsdafasd.jpg");
        //when
        productService.createProduct(thumbnail, descriptionImage, form, 1L);
        //then
        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository, times(1)).save(productCaptor.capture());
        Product product = productCaptor.getValue();
        assertEquals(product.getUserId(), 1L);
        assertEquals(product.getThumbnailPath(), "afsdfasfsdafasd.jpg");
        assertEquals(product.getThumbnailPath(), "afsdfasfsdafasd.jpg");
        assertEquals(product.getPrice(), form.price());
        assertEquals(product.getDiscount(), form.discount());
        assertEquals(product.getName(), form.name());
        assertEquals(product.getProductInfo().getContact(), form.contact());
        assertEquals(product.getProductInfo().getManufacturer(), form.manufacturer());
        assertEquals(product.getProductInfo().getCountryOfManufacture(), form.countryOfManufacture());
        assertEquals(product.getAmount(), form.amount());
        assertEquals(product.getDeliveryFee(), form.deliveryFee());
        assertNotNull(product.getLastModifiedAt());
        assertEquals(product.getCategories().size(), 4);
        assertNotNull(product.getLikes());
        assertFalse(product.isDeleted());
        assertEquals(product.getProductBrand().getId(), 1L);
        assertEquals(product.getProductBrand().getName(), "뉴발란스");
    }

    @Test
    void updateProduct_success() {
        //given
        MultipartFile thumbnail = new MockMultipartFile("abc.jpg", "abcd".getBytes());
        MultipartFile descriptionImage = new MockMultipartFile("abc.jpg", "abcd".getBytes());
        UpdateProductForm form = new UpdateProductForm("123edsf3fdsfdsa3560fdg", "NBGCEFW701 / 글로시 리본 더플백 (VIORET)", "(주)이랜드월드 뉴발란스 사업부", "중국", "뉴발란스 고객 상담실 (080-999-0456)", 69900, 200, 0, 2500);
        given(brandGrpcClient.getBrandInfo(anyLong())).willReturn(BrandProto.BrandResponse.newBuilder().setId(1L).setName("뉴발란스").setUserId(1L).build());
        Product product = Product.builder()
                .thumbnailPath("12345.jpg")
                .bodyImagePath("78910.jpg")
                .price(10000)
                .discount(10)
                .name("강아지 눈물 티슈")
                .productInfo(ProductInfo.from("돌봄", "중국", "010-1111-1234"))
                .amount(1000)
                .deliveryFee(3000)
                .likes(List.of(1L))
                .lastModifiedAt(LocalDateTime.MIN)
                .categories(List.of(CategoryInfo.builder()
                        .id("12efad").name("강아지 용품")
                        .build()))
                .productBrand(ProductBrand.builder()
                        .name("돌봄")
                        .id(1L)
                        .build()
                ).userId(1L).isDeleted(false).build();
        given(productRepository.findById(anyString())).willReturn(Optional.of(product));

        given(categoryService.retrieveBelongingCategories(form.categoryId())).willReturn(categoryInfoDtos);
        given(fileStorageClient.upload((MultipartFile) any(), any())).willReturn("afsdfasfsdafasd.jpg");
        //when
        productService.updateProduct("123456yl", thumbnail, descriptionImage, form, 1L);
        //then
        assertEquals(product.getUserId(), 1L);
        assertEquals(product.getThumbnailPath(), "afsdfasfsdafasd.jpg");
        assertEquals(product.getThumbnailPath(), "afsdfasfsdafasd.jpg");
        assertEquals(product.getPrice(), form.price());
        assertEquals(product.getDiscount(), form.discount());
        assertEquals(product.getName(), form.name());
        assertEquals(product.getProductInfo().getContact(), form.contact());
        assertEquals(product.getProductInfo().getManufacturer(), form.manufacturer());
        assertEquals(product.getProductInfo().getCountryOfManufacture(), form.countryOfManufacture());
        assertEquals(product.getAmount(), form.amount());
        assertEquals(product.getDeliveryFee(), form.deliveryFee());
        assertTrue(product.getLastModifiedAt().isAfter(LocalDateTime.MIN));
        assertEquals(product.getCategories().size(), 4);
        assertNotNull(product.getLikes());
        assertFalse(product.isDeleted());
        assertEquals(product.getProductBrand().getId(), 1L);
        assertEquals(product.getProductBrand().getName(), "뉴발란스");
    }

    @Test
    void updateProduct_fail_PRODUCT_NOT_FOUND() {
        //given
        MultipartFile thumbnail = new MockMultipartFile("abc.jpg", "abcd".getBytes());
        MultipartFile descriptionImage = new MockMultipartFile("abc.jpg", "abcd".getBytes());
        UpdateProductForm form = new UpdateProductForm("123edsf3fdsfdsa3560fdg", "NBGCEFW701 / 글로시 리본 더플백 (VIORET)", "(주)이랜드월드 뉴발란스 사업부", "중국", "뉴발란스 고객 상담실 (080-999-0456)", 69900, 200, 0, 2500);
        given(productRepository.findById(anyString())).willReturn(Optional.empty());

        //when
        ProductException e = assertThrows(ProductException.class, () -> productService.updateProduct("123456yl", thumbnail, descriptionImage, form, 1L));
        //then
        assertEquals(e.getError(), Error.PRODUCT_NOT_FOUND);
    }

    @Test
    void updateProduct_fail_NOT_PRODUCT_ADMIN() {
        //given
        MultipartFile thumbnail = new MockMultipartFile("abc.jpg", "abcd".getBytes());
        MultipartFile descriptionImage = new MockMultipartFile("abc.jpg", "abcd".getBytes());
        UpdateProductForm form = new UpdateProductForm("123edsf3fdsfdsa3560fdg", "NBGCEFW701 / 글로시 리본 더플백 (VIORET)", "(주)이랜드월드 뉴발란스 사업부", "중국", "뉴발란스 고객 상담실 (080-999-0456)", 69900, 200, 0, 2500);
        Product product = Product.builder()
                .thumbnailPath("12345.jpg")
                .bodyImagePath("78910.jpg")
                .price(10000)
                .discount(10)
                .name("강아지 눈물 티슈")
                .productInfo(ProductInfo.from("돌봄", "중국", "010-1111-1234"))
                .amount(1000)
                .deliveryFee(3000)
                .likes(List.of(1L))
                .lastModifiedAt(LocalDateTime.MIN)
                .categories(List.of(CategoryInfo.builder()
                        .id("12efad").name("강아지 용품")
                        .build()))
                .productBrand(ProductBrand.builder()
                        .name("돌봄")
                        .id(1L)
                        .build()
                ).userId(1L).isDeleted(false).build();
        given(productRepository.findById(anyString())).willReturn(Optional.of(product));

        //when
        ProductException e = assertThrows(ProductException.class, () -> productService.updateProduct("123456yl", thumbnail, descriptionImage, form, 2L));
        //then
        assertEquals(e.getError(), Error.NOT_PRODUCT_ADMIN);
    }

    @Test
    void updateProduct_fail_ALREADY_DELETED_PRODUCT() {
        //given
        MultipartFile thumbnail = new MockMultipartFile("abc.jpg", "abcd".getBytes());
        MultipartFile descriptionImage = new MockMultipartFile("abc.jpg", "abcd".getBytes());
        UpdateProductForm form = new UpdateProductForm("123edsf3fdsfdsa3560fdg", "NBGCEFW701 / 글로시 리본 더플백 (VIORET)", "(주)이랜드월드 뉴발란스 사업부", "중국", "뉴발란스 고객 상담실 (080-999-0456)", 69900, 200, 0, 2500);
        Product product = Product.builder()
                .thumbnailPath("12345.jpg")
                .bodyImagePath("78910.jpg")
                .price(10000)
                .discount(10)
                .name("강아지 눈물 티슈")
                .productInfo(ProductInfo.from("돌봄", "중국", "010-1111-1234"))
                .amount(1000)
                .deliveryFee(3000)
                .likes(List.of(1L))
                .lastModifiedAt(LocalDateTime.MIN)
                .categories(List.of(CategoryInfo.builder()
                        .id("12efad").name("강아지 용품")
                        .build()))
                .productBrand(ProductBrand.builder()
                        .name("돌봄")
                        .id(1L)
                        .build()
                ).userId(1L).isDeleted(true).build();
        given(productRepository.findById(anyString())).willReturn(Optional.of(product));

        //when
        ProductException e = assertThrows(ProductException.class, () -> productService.updateProduct("123456yl", thumbnail, descriptionImage, form, 1L));
        //then
        assertEquals(e.getError(), Error.ALREADY_DELETED_PRODUCT);
    }

    @Test
    void deleteProduct_success() {
        //given
        Product product = Product.builder()
                .thumbnailPath("12345.jpg")
                .bodyImagePath("78910.jpg")
                .price(10000)
                .discount(10)
                .name("강아지 눈물 티슈")
                .productInfo(ProductInfo.from("돌봄", "중국", "010-1111-1234"))
                .amount(1000)
                .deliveryFee(3000)
                .likes(List.of(1L))
                .lastModifiedAt(LocalDateTime.MIN)
                .categories(List.of(CategoryInfo.builder()
                        .id("12efad").name("강아지 용품")
                        .build()))
                .productBrand(ProductBrand.builder()
                        .name("돌봄")
                        .id(1L)
                        .build()
                ).userId(1L).isDeleted(false).build();
        given(productRepository.findById(anyString())).willReturn(Optional.of(product));
        //when
        productService.deleteProduct("123456yl", 1L);
        //then
        assertTrue(product.isDeleted());
    }

    @Test
    void deleteProduct_fail_PRODUCT_NOT_FOUND() {
        given(productRepository.findById(anyString())).willReturn(Optional.empty());
        //when
        ProductException e = assertThrows(ProductException.class, () -> productService.deleteProduct("123456yl", 1L));
        //then
        assertEquals(e.getError(), Error.PRODUCT_NOT_FOUND);
    }

    @Test
    void deleteProduct_fail_NOT_PRODUCT_ADMIN() {
        //given
        Product product = Product.builder()
                .thumbnailPath("12345.jpg")
                .bodyImagePath("78910.jpg")
                .price(10000)
                .discount(10)
                .name("강아지 눈물 티슈")
                .productInfo(ProductInfo.from("돌봄", "중국", "010-1111-1234"))
                .amount(1000)
                .deliveryFee(3000)
                .likes(List.of(1L))
                .lastModifiedAt(LocalDateTime.MIN)
                .categories(List.of(CategoryInfo.builder()
                        .id("12efad").name("강아지 용품")
                        .build()))
                .productBrand(ProductBrand.builder()
                        .name("돌봄")
                        .id(1L)
                        .build()
                ).userId(1L).isDeleted(false).build();
        given(productRepository.findById(anyString())).willReturn(Optional.of(product));

        //when
        ProductException e = assertThrows(ProductException.class, () -> productService.deleteProduct("123456yl", 2L));
        //then
        assertEquals(e.getError(), Error.NOT_PRODUCT_ADMIN);
    }

    @Test
    void deleteProduct_fail_ALREADY_DELETED_PRODUCT() {
        //given
        Product product = Product.builder()
                .thumbnailPath("12345.jpg")
                .bodyImagePath("78910.jpg")
                .price(10000)
                .discount(10)
                .name("강아지 눈물 티슈")
                .productInfo(ProductInfo.from("돌봄", "중국", "010-1111-1234"))
                .amount(1000)
                .deliveryFee(3000)
                .likes(List.of(1L))
                .lastModifiedAt(LocalDateTime.MIN)
                .categories(List.of(CategoryInfo.builder()
                        .id("12efad").name("강아지 용품")
                        .build()))
                .productBrand(ProductBrand.builder()
                        .name("돌봄")
                        .id(1L)
                        .build()
                ).userId(1L).isDeleted(true).build();
        given(productRepository.findById(anyString())).willReturn(Optional.of(product));

        //when
        ProductException e = assertThrows(ProductException.class, () -> productService.deleteProduct("123456yl", 1L));
        //then
        assertEquals(e.getError(), Error.ALREADY_DELETED_PRODUCT);
    }


}