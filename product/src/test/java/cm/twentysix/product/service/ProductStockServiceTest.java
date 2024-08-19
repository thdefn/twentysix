package cm.twentysix.product.service;

import cm.twentysix.product.domain.model.Product;
import cm.twentysix.product.domain.model.ProductBrand;
import cm.twentysix.product.domain.repository.ProductRepository;
import cm.twentysix.product.dto.ProductOrderFailedEvent;
import cm.twentysix.product.messaging.MessageSender;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductStockServiceTest {
    @Mock
    private ProductRepository productRepository;
    @Mock
    private MessageSender messageSender;
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
        Map<String, Integer> orderedProductQuantity = Map.of("1", 1, "2", 2);
        List<Product> products = List.of(mockProductA, mockProductB);
        given(productRepository.findByIdInAndIsDeletedFalse(any())).willReturn(products);
        //when
        productStockService.checkProductStock(orderedProductQuantity, "12345");
        //then
        verify(mockProductA, times(1)).minusQuantity(1);
        verify(mockProductB, times(1)).minusQuantity(2);
        verify(productRepository, times(1)).saveAll(anyCollection());
    }

    @Test
    void checkProductStock_success_ProductStockShortage() {
        //given
        Map<String, Integer> orderedProductQuantity = Map.of("1", 1, "2", 3);
        List<Product> products = List.of(mockProductA, mockProductB);
        given(productRepository.findByIdInAndIsDeletedFalse(any())).willReturn(products);
        //when
        productStockService.checkProductStock(orderedProductQuantity, "12345");
        //then
        ArgumentCaptor<ProductOrderFailedEvent> productOrderFailedEventCaptor = ArgumentCaptor.forClass(ProductOrderFailedEvent.class);
        verify(messageSender, times(1)).sendProductOrderFailedEvent(productOrderFailedEventCaptor.capture());
        ProductOrderFailedEvent event = productOrderFailedEventCaptor.getValue();
        assertEquals(event.orderId(), "12345");
    }

    @Test
    void restoreProductStock_success() {
        //given
        Map<String, Integer> productQuantity = Map.of("1", 1, "2", 3);
        List<Product> products = List.of(mockProductA, mockProductB);
        given(productRepository.findByIdInAndIsDeletedFalse(any())).willReturn(products);
        //when
        productStockService.restoreProductStock(productQuantity);
        //then
        verify(mockProductA, times(1)).addQuantity(1);
        verify(mockProductB, times(1)).addQuantity(3);
        verify(productRepository, times(1)).saveAll(anyCollection());
    }

}