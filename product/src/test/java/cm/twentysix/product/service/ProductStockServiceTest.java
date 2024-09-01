package cm.twentysix.product.service;

import cm.twentysix.product.domain.model.Product;
import cm.twentysix.product.domain.model.ProductBrand;
import cm.twentysix.product.domain.repository.ProductRepository;
import cm.twentysix.product.dto.ProductStockResponse;
import cm.twentysix.product.exception.Error;
import cm.twentysix.product.exception.ProductException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductStockServiceTest {
    @Mock
    private ProductRepository productRepository;
    @InjectMocks
    private ProductStockService productStockService;

    static Product mockProductA = mock(Product.class);
    static Product mockProductB = mock(Product.class);
    static ProductBrand productBrandA = mock(ProductBrand.class);
    static ProductBrand productBrandB = mock(ProductBrand.class);

    @BeforeAll
    static void init() {
        given(mockProductA.getId()).willReturn("1");
        given(mockProductA.getName()).willReturn("강아지 눈물 티슈");
        given(mockProductA.getThumbnailPath()).willReturn("12345.jpg");
        given(mockProductA.getDiscountedPrice()).willReturn(9000);
        given(mockProductA.getQuantity()).willReturn(1);
        given(mockProductA.getProductBrand()).willReturn(productBrandA);
        given(productBrandA.getId()).willReturn(1L);
        given(productBrandA.getName()).willReturn("돌봄");
        given(mockProductA.getDeliveryFee()).willReturn(3500);

        given(mockProductB.getId()).willReturn("2");
        given(mockProductB.getName()).willReturn("스탠리 텀블러");
        given(mockProductB.getThumbnailPath()).willReturn("54321.jpg");
        given(mockProductB.getDiscountedPrice()).willReturn(7000);
        given(mockProductB.getQuantity()).willReturn(2);
        given(mockProductB.getProductBrand()).willReturn(productBrandB);
        given(productBrandB.getId()).willReturn(2L);
        given(productBrandB.getName()).willReturn("스탠리");
        given(mockProductB.getDeliveryFee()).willReturn(3500);
    }


    @Test
    void checkProductStock_success() {
        //given
        given(productRepository.findByIdAndIsDeletedFalse(anyString())).willReturn(Optional.of(mockProductA));
        //when
        boolean success = productStockService.checkAndDecreaseStock("1", 1);
        //then
        verify(mockProductA, times(1)).minusQuantity(1);
        assertTrue(success);
    }

    @Test
    void checkProductStock_success_checkFail() {
        //given
        given(productRepository.findByIdAndIsDeletedFalse(anyString())).willReturn(Optional.of(mockProductA));
        //when
        boolean success = productStockService.checkAndDecreaseStock("1", 2);
        //then
        verify(mockProductA, times(0)).minusQuantity(anyInt());
        assertFalse(success);
    }

    @Test
    void checkProductStock_fail_PRODUCT_NOT_FOUND() {
        //given
        given(productRepository.findByIdAndIsDeletedFalse(anyString())).willReturn(Optional.empty());
        //when
        ProductException e = assertThrows(ProductException.class, () -> productStockService.checkAndDecreaseStock("1", 1));
        //then
        assertEquals(e.getError(), Error.PRODUCT_NOT_FOUND);
    }


    @Test
    void restoreProductStock_success() {
        //given
        given(productRepository.findByIdAndIsDeletedFalse(anyString())).willReturn(Optional.of(mockProductA));
        //when
        productStockService.restoreProductStock("1", 2);
        //then
        verify(mockProductA, times(1)).addQuantity(anyInt());
    }

    @Test
    void restoreProductStock_fail_PRODUCT_NOT_FOUND() {
        //given
        given(productRepository.findByIdAndIsDeletedFalse(anyString())).willReturn(Optional.empty());
        //when
        ProductException e = assertThrows(ProductException.class, () -> productStockService.restoreProductStock("1", 1));
        //then
        assertEquals(e.getError(), Error.PRODUCT_NOT_FOUND);
    }


    @Test
    void retrieveProductStock_success() {
        //given
        given(productRepository.findByIdAndIsDeletedFalse(any())).willReturn(Optional.of(mockProductA));
        //when
        ProductStockResponse response = productStockService.retrieveProductStock("12345");
        //then
        assertEquals(response.id(), "1");
        assertEquals(response.name(), "강아지 눈물 티슈");
        assertEquals(response.quantity(), 1);
    }

    @Test
    void retrieveProductStock_fail_PRODUCT_NOT_FOUND() {
        //given
        given(productRepository.findByIdAndIsDeletedFalse(anyString())).willReturn(Optional.empty());
        //when
        ProductException e = assertThrows(ProductException.class, () -> productStockService.retrieveProductStock("123456yl"));
        //then
        assertEquals(e.getError(), Error.PRODUCT_NOT_FOUND);
    }

}