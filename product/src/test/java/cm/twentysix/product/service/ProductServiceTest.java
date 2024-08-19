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
import cm.twentysix.product.dto.ProductItem;
import cm.twentysix.product.dto.ProductResponse;
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
import java.util.Set;

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

    String orderingBeginAt = LocalDateTime.of(2024,12,12,12,12,12).toString();

    @Test
    void createProduct_success() {
        //given
        MultipartFile thumbnail = new MockMultipartFile("abc.jpg", "abcd".getBytes());
        MultipartFile descriptionImage = new MockMultipartFile("abc.jpg", "abcd".getBytes());
        CreateProductForm form = new CreateProductForm(1L, "123edsf3fdsfdsa3560fdg", "NBGCEFW701 / 글로시 리본 더플백 (VIORET)", "(주)이랜드월드 뉴발란스 사업부", "중국", "뉴발란스 고객 상담실 (080-999-0456)", 69900, 200, 0, 2500, null);
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
        assertEquals(product.getQuantity(), form.quantity());
        assertEquals(product.getDeliveryFee(), form.deliveryFee());
        assertNotNull(product.getLastModifiedAt());
        assertEquals(product.getCategories().size(), 4);
        assertNotNull(product.getLikes());
        assertFalse(product.isDeleted());
        assertEquals(product.getProductBrand().getId(), 1L);
        assertEquals(product.getProductBrand().getName(), "뉴발란스");
        assertTrue(LocalDateTime.now().isAfter(product.getOrderingOpensAt()));
    }

    @Test
    void updateProduct_success() {
        //given
        MultipartFile thumbnail = new MockMultipartFile("abc.jpg", "abcd".getBytes());
        MultipartFile descriptionImage = new MockMultipartFile("abc.jpg", "abcd".getBytes());
        UpdateProductForm form = new UpdateProductForm("123edsf3fdsfdsa3560fdg", "NBGCEFW701 / 글로시 리본 더플백 (VIORET)", "(주)이랜드월드 뉴발란스 사업부", "중국", "뉴발란스 고객 상담실 (080-999-0456)", 69900, 200, 0, 2500, orderingBeginAt);
        given(brandGrpcClient.getBrandInfo(anyLong())).willReturn(BrandProto.BrandResponse.newBuilder().setId(1L).setName("뉴발란스").setUserId(1L).build());
        Product product = Product.builder()
                .thumbnailPath("12345.jpg")
                .bodyImagePath("78910.jpg")
                .price(10000)
                .discount(10)
                .name("강아지 눈물 티슈")
                .productInfo(ProductInfo.from("돌봄", "중국", "010-1111-1234"))
                .quantity(1000)
                .deliveryFee(3000)
                .likes(Set.of(1L))
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
        assertEquals(product.getQuantity(), form.quantity());
        assertEquals(product.getDeliveryFee(), form.deliveryFee());
        assertTrue(product.getLastModifiedAt().isAfter(LocalDateTime.MIN));
        assertEquals(product.getCategories().size(), 4);
        assertNotNull(product.getLikes());
        assertFalse(product.isDeleted());
        assertEquals(product.getProductBrand().getId(), 1L);
        assertEquals(product.getProductBrand().getName(), "뉴발란스");
        assertEquals(orderingBeginAt, product.getOrderingOpensAt().toString());
    }

    @Test
    void updateProduct_fail_PRODUCT_NOT_FOUND() {
        //given
        MultipartFile thumbnail = new MockMultipartFile("abc.jpg", "abcd".getBytes());
        MultipartFile descriptionImage = new MockMultipartFile("abc.jpg", "abcd".getBytes());
        UpdateProductForm form = new UpdateProductForm("123edsf3fdsfdsa3560fdg", "NBGCEFW701 / 글로시 리본 더플백 (VIORET)", "(주)이랜드월드 뉴발란스 사업부", "중국", "뉴발란스 고객 상담실 (080-999-0456)", 69900, 200, 0, 2500, orderingBeginAt);
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
        UpdateProductForm form = new UpdateProductForm("123edsf3fdsfdsa3560fdg", "NBGCEFW701 / 글로시 리본 더플백 (VIORET)", "(주)이랜드월드 뉴발란스 사업부", "중국", "뉴발란스 고객 상담실 (080-999-0456)", 69900, 200, 0, 2500, orderingBeginAt);
        Product product = Product.builder()
                .thumbnailPath("12345.jpg")
                .bodyImagePath("78910.jpg")
                .price(10000)
                .discount(10)
                .name("강아지 눈물 티슈")
                .productInfo(ProductInfo.from("돌봄", "중국", "010-1111-1234"))
                .quantity(1000)
                .deliveryFee(3000)
                .likes(Set.of(1L))
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
        UpdateProductForm form = new UpdateProductForm("123edsf3fdsfdsa3560fdg", "NBGCEFW701 / 글로시 리본 더플백 (VIORET)", "(주)이랜드월드 뉴발란스 사업부", "중국", "뉴발란스 고객 상담실 (080-999-0456)", 69900, 200, 0, 2500, orderingBeginAt);
        Product product = Product.builder()
                .thumbnailPath("12345.jpg")
                .bodyImagePath("78910.jpg")
                .price(10000)
                .discount(10)
                .name("강아지 눈물 티슈")
                .productInfo(ProductInfo.from("돌봄", "중국", "010-1111-1234"))
                .quantity(1000)
                .deliveryFee(3000)
                .likes(Set.of(1L))
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
                .quantity(1000)
                .deliveryFee(3000)
                .likes(Set.of(1L))
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
                .quantity(1000)
                .deliveryFee(3000)
                .likes(Set.of(1L))
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
                .quantity(1000)
                .deliveryFee(3000)
                .likes(Set.of(1L))
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

    @Test
    void retrieveProducts_success() {
        //given
        List<Product> products = List.of(
                Product.builder()
                        .thumbnailPath("12345.jpg")
                        .bodyImagePath("78910.jpg")
                        .price(10000)
                        .discount(10)
                        .name("강아지 눈물 티슈")
                        .productInfo(ProductInfo.from("돌봄", "중국", "010-1111-1234"))
                        .quantity(1000)
                        .deliveryFee(0)
                        .likes(Set.of(2L))
                        .lastModifiedAt(LocalDateTime.MIN)
                        .categories(List.of(CategoryInfo.builder()
                                .id("12efad").name("강아지 용품")
                                .build(), CategoryInfo.builder()
                                .id("12345").name("리빙")
                                .build()))
                        .productBrand(ProductBrand.builder()
                                .name("돌봄")
                                .id(1L)
                                .build()
                        ).userId(1L).isDeleted(true).build(),
                Product.builder()
                        .thumbnailPath("12345.jpg")
                        .bodyImagePath("78910.jpg")
                        .price(5000)
                        .discount(0)
                        .name("스탠리 텀블러")
                        .productInfo(ProductInfo.from("스탠리", "중국", "02-000-0000"))
                        .quantity(100)
                        .deliveryFee(2500)
                        .likes(Set.of(1L, 2L, 3L, 4L))
                        .lastModifiedAt(LocalDateTime.MIN)
                        .categories(List.of(CategoryInfo.builder()
                                        .id("abcdee").name("주방 용품")
                                        .build(),
                                CategoryInfo.builder()
                                        .id("12345").name("리빙")
                                        .build()))
                        .productBrand(ProductBrand.builder()
                                .name("스탠리")
                                .id(3L)
                                .build()
                        ).userId(4L).isDeleted(true).build()
        );
        given(productRepository.findByIsDeletedFalseOrderByIdDesc(any())).willReturn(products);
        //when
        List<ProductItem> productItems = productService.retrieveProducts(0, 10, Optional.of(1L));
        //then
        assertEquals(productItems.size(), 2);
        assertEquals(productItems.getFirst().thumbnailPath(), "12345.jpg");
        assertEquals(productItems.getFirst().name(), "강아지 눈물 티슈");
        assertEquals(productItems.getFirst().price(), products.getFirst().getDiscountedPrice());
        assertEquals(productItems.getFirst().countOfLikes(), 1);
        assertEquals(productItems.getFirst().discount(), 10);
        assertTrue(productItems.getFirst().isFreeDelivery());
        assertFalse(productItems.getFirst().isUserLike());
        assertEquals(productItems.getFirst().brandName(), "돌봄");
        assertEquals(productItems.getFirst().brandId(), 1L);

        assertEquals(productItems.getLast().thumbnailPath(), "12345.jpg");
        assertEquals(productItems.getLast().name(), "스탠리 텀블러");
        assertEquals(productItems.getLast().price(), products.getLast().getDiscountedPrice());
        assertEquals(productItems.getLast().countOfLikes(), 4);
        assertEquals(productItems.getLast().discount(), 0);
        assertFalse(productItems.getLast().isFreeDelivery());
        assertTrue(productItems.getLast().isUserLike());
        assertEquals(productItems.getLast().brandName(), "스탠리");
        assertEquals(productItems.getLast().brandId(), 3L);
    }

    @Test
    void retrieveProducts_success_WhenUserIdIsNull() {
        //given
        List<Product> products = List.of(
                Product.builder()
                        .thumbnailPath("12345.jpg")
                        .bodyImagePath("78910.jpg")
                        .price(10000)
                        .discount(10)
                        .name("강아지 눈물 티슈")
                        .productInfo(ProductInfo.from("돌봄", "중국", "010-1111-1234"))
                        .quantity(1000)
                        .deliveryFee(0)
                        .likes(Set.of(2L))
                        .lastModifiedAt(LocalDateTime.MIN)
                        .categories(List.of(CategoryInfo.builder()
                                .id("12efad").name("강아지 용품")
                                .build(), CategoryInfo.builder()
                                .id("12345").name("리빙")
                                .build()))
                        .productBrand(ProductBrand.builder()
                                .name("돌봄")
                                .id(1L)
                                .build()
                        ).userId(1L).isDeleted(true).build()
        );
        given(productRepository.findByIsDeletedFalseOrderByIdDesc(any())).willReturn(products);
        //when
        List<ProductItem> productItems = productService.retrieveProducts(0, 10, Optional.empty());
        //then
        assertEquals(productItems.getFirst().thumbnailPath(), "12345.jpg");
        assertEquals(productItems.getFirst().name(), "강아지 눈물 티슈");
        assertEquals(productItems.getFirst().price(), products.getFirst().getDiscountedPrice());
        assertEquals(productItems.getFirst().countOfLikes(), 1);
        assertEquals(productItems.getFirst().discount(), 10);
        assertTrue(productItems.getFirst().isFreeDelivery());
        assertFalse(productItems.getFirst().isUserLike());
        assertEquals(productItems.getFirst().brandName(), "돌봄");
        assertEquals(productItems.getFirst().brandId(), 1L);
    }

    @Test
    void retrieveProduct_success() {
        //given
        Product product = Product.builder()
                .thumbnailPath("12345.jpg")
                .bodyImagePath("78910.jpg")
                .price(10000)
                .discount(10)
                .name("강아지 눈물 티슈")
                .productInfo(ProductInfo.from("돌봄", "중국", "010-1111-1234"))
                .quantity(1000)
                .deliveryFee(0)
                .likes(Set.of(1L))
                .lastModifiedAt(LocalDateTime.MIN)
                .categories(List.of(CategoryInfo.builder()
                        .id("12efad").name("강아지 용품")
                        .build(), CategoryInfo.builder()
                        .id("12345").name("리빙")
                        .build()))
                .productBrand(ProductBrand.builder()
                        .name("돌봄")
                        .id(1L)
                        .build()
                ).userId(1L).isDeleted(true).build();
        given(productRepository.findByIdAndIsDeletedFalse(anyString())).willReturn(Optional.of(product));
        given(brandGrpcClient.getBrandDetail(anyLong())).willReturn(
                BrandProto.BrandDetailResponse.newBuilder()
                        .setId(1L)
                        .setName("돌봄")
                        .setLegalName("(주) 돌봄")
                        .setIntroduction("강아지가 행복한 세상을 만듭니다.")
                        .setFreeDeliveryInfimum(30000)
                        .setRegistrationNumber("000-00-00000")
                        .setThumbnail("101112.jpg")
                        .build());
        //when
        ProductResponse response = productService.retrieveProduct("1234567675", Optional.of(1L));
        //then
        assertEquals(response.thumbnailPath(), "12345.jpg");
        assertEquals(response.name(), "강아지 눈물 티슈");
        assertEquals(response.price(), 10000);
        assertEquals(response.discount(), 10);
        assertEquals(response.discountedPrice(), product.getDiscountedPrice());
        assertEquals(response.deliveryFee(), 0);
        assertTrue(response.isFreeDelivery());
        assertTrue(response.isUserLike());
        assertEquals(response.info().contact(), "010-1111-1234");
        assertEquals(response.info().countryOfManufacture(), "중국");
        assertEquals(response.info().manufacturer(), "돌봄");
        assertEquals(response.brand().brandId(), 1L);
        assertEquals(response.brand().legalName(), "(주) 돌봄");
        assertEquals(response.brand().introduction(), "강아지가 행복한 세상을 만듭니다.");
        assertEquals(response.brand().freeDeliveryInfimum(), 30000);
        assertEquals(response.brand().registrationNumber(), "000-00-00000");
        assertEquals(response.brand().thumbnail(), "101112.jpg");
    }

    @Test
    void retrieveProduct_fail_PRODUCT_NOT_FOUND() {
        //given
        given(productRepository.findByIdAndIsDeletedFalse(anyString())).willReturn(Optional.empty());
        //when
        ProductException e = assertThrows(ProductException.class, () -> productService.retrieveProduct("123456yl", Optional.of(1L)));
        //then
        assertEquals(e.getError(), Error.PRODUCT_NOT_FOUND);
    }


}