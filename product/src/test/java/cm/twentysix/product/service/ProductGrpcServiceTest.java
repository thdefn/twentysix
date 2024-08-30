package cm.twentysix.product.service;

import cm.twentysix.ProductProto;
import cm.twentysix.ProductProto.CheckProductStockResponse;
import cm.twentysix.ProductProto.ProductItemResponse;
import cm.twentysix.ProductProto.ProductItemsResponse;
import cm.twentysix.product.cache.global.ProductItemResponseGlobalCacheRepository;
import cm.twentysix.product.domain.model.Product;
import cm.twentysix.product.domain.model.ProductBrand;
import cm.twentysix.product.domain.repository.ProductRepository;
import cm.twentysix.product.exception.Error;
import cm.twentysix.product.exception.ProductException;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static io.grpc.Status.Code.INVALID_ARGUMENT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductGrpcServiceTest {
    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProductItemResponseGlobalCacheRepository productItemResponseGlobalCacheRepository;
    @Mock
    private ProductStockFacade productStockFacade;
    @Mock
    private StreamObserver<ProductItemResponse> productItemResponseStreamObserver;
    @Mock
    private StreamObserver<ProductItemsResponse> productItemsResponseStreamObserver;
    @Mock
    private StreamObserver<CheckProductStockResponse> checkProductStockResponseStreamObserver;
    @InjectMocks
    private ProductGrpcService productGrpcService;

    private static Product mockProductA;

    @BeforeAll
    static void setUp() {
        mockProductA = Mockito.mock(Product.class);
        given(mockProductA.getId()).willReturn("123A");
        ProductBrand productBrand = ProductBrand.builder()
                .name("돌봄")
                .id(1L)
                .build();
        given(mockProductA.getProductBrand()).willReturn(productBrand);
        given(mockProductA.getName()).willReturn("강아지 눈물 티슈");
        given(mockProductA.getOrderingOpensAt()).willReturn(LocalDateTime.MIN);
        given(mockProductA.getThumbnailPath()).willReturn("12345.jpg");
        given(mockProductA.getDiscount()).willReturn(10);
        given(mockProductA.getPrice()).willReturn(10000);
        given(mockProductA.getDiscountedPrice()).willReturn(9000);
        given(mockProductA.getQuantity()).willReturn(10);
    }

    @Test
    void getProductItem_success() {
        //given
        ProductProto.ProductItemRequest request = ProductProto
                .ProductItemRequest
                .newBuilder()
                .setId("12345")
                .build();
        given(productRepository.findByIdAndIsDeletedFalse(anyString()))
                .willReturn(Optional.of(mockProductA));
        //when
        productGrpcService.getProductItem(request, productItemResponseStreamObserver);
        //then
        ArgumentCaptor<ProductItemResponse> productResponseCaptor = ArgumentCaptor.forClass(ProductItemResponse.class);
        verify(productItemResponseStreamObserver, times(1)).onNext(productResponseCaptor.capture());
        ProductItemResponse response = productResponseCaptor.getValue();
        assertEquals(response.getBrandId(), 1L);
        assertEquals(response.getThumbnail(), "12345.jpg");
        assertEquals(response.getDiscount(), 10);
        assertEquals(response.getPrice(), 10000);
        assertEquals(response.getName(), "강아지 눈물 티슈");
        assertEquals(response.getDiscountedPrice(), 9000);
        assertEquals(response.getBrandName(), "돌봄");
        assertEquals(response.getQuantity(), 10);
        assertEquals(response.getOrderingOpensAt(), LocalDateTime.MIN.toString());
    }

    @Test
    void getProductItem_fail_INVALID_ARGUMENT() {
        //given
        ProductProto.ProductItemRequest request = ProductProto
                .ProductItemRequest
                .newBuilder()
                .setId("12345")
                .build();
        given(productRepository.findByIdAndIsDeletedFalse(anyString()))
                .willReturn(Optional.empty());
        //when
        productGrpcService.getProductItem(request, productItemResponseStreamObserver);
        //then
        ArgumentCaptor<StatusRuntimeException> exceptionArgumentCaptor = ArgumentCaptor.forClass(StatusRuntimeException.class);
        verify(productItemResponseStreamObserver).onError(exceptionArgumentCaptor.capture());
        assertEquals(exceptionArgumentCaptor.getValue().getStatus().getCode(), INVALID_ARGUMENT);
    }

    @Test
    void getProductItem_fail_INTERNAL() {
        //given
        ProductProto.ProductItemRequest request = ProductProto
                .ProductItemRequest
                .newBuilder()
                .setId("12345")
                .build();
        doThrow(new RuntimeException())
                .when(productRepository).findByIdAndIsDeletedFalse(anyString());
        //when
        productGrpcService.getProductItem(request, productItemResponseStreamObserver);
        //then
        ArgumentCaptor<StatusRuntimeException> exceptionArgumentCaptor = ArgumentCaptor.forClass(StatusRuntimeException.class);
        verify(productItemResponseStreamObserver).onError(exceptionArgumentCaptor.capture());
        assertEquals(exceptionArgumentCaptor.getValue().getStatus().getCode(), Status.Code.INTERNAL);
    }

    @Test
    void getProductItems() {
        //given
        ProductProto.ProductItemsRequest request = ProductProto
                .ProductItemsRequest
                .newBuilder()
                .addAllIds(List.of("12345"))
                .build();
        given(productRepository.findByIdInAndIsDeletedFalse(anySet()))
                .willReturn(List.of(mockProductA));
        //when
        productGrpcService.getProductItems(request, productItemsResponseStreamObserver);
        //then
        ArgumentCaptor<ProductItemsResponse> productResponseCaptor = ArgumentCaptor.forClass(ProductItemsResponse.class);
        verify(productItemsResponseStreamObserver, times(1)).onNext(productResponseCaptor.capture());
        ProductItemsResponse response = productResponseCaptor.getValue();
        assertEquals(response.getProductsList().size(), 1);

        ProductItemResponse item = response.getProductsList().getFirst();
        assertEquals(item.getId(), "123A");
        assertEquals(item.getBrandId(), 1L);
        assertEquals(item.getThumbnail(), "12345.jpg");
        assertEquals(item.getDiscount(), 10);
        assertEquals(item.getPrice(), 10000);
        assertEquals(item.getName(), "강아지 눈물 티슈");
        assertEquals(item.getDiscountedPrice(), 9000);
        assertEquals(item.getBrandName(), "돌봄");
        assertEquals(item.getQuantity(), 10);
        assertEquals(item.getOrderingOpensAt(), LocalDateTime.MIN.toString());
    }

    @Test
    void getProductItems_fail_INVALID_ARGUMENT() {
        //given
        ProductProto.ProductItemsRequest request = ProductProto
                .ProductItemsRequest
                .newBuilder()
                .addAllIds(List.of("12345"))
                .build();
        given(productRepository.findByIdInAndIsDeletedFalse(anySet()))
                .willReturn(List.of());
        //when
        productGrpcService.getProductItems(request, productItemsResponseStreamObserver);
        //then
        ArgumentCaptor<StatusRuntimeException> exceptionArgumentCaptor = ArgumentCaptor.forClass(StatusRuntimeException.class);
        verify(productItemsResponseStreamObserver).onError(exceptionArgumentCaptor.capture());
        assertEquals(exceptionArgumentCaptor.getValue().getStatus().getCode(), INVALID_ARGUMENT);
    }

    @Test
    void getProductItems_fail_INTERNAL() {
        //given
        ProductProto.ProductItemsRequest request = ProductProto
                .ProductItemsRequest
                .newBuilder()
                .addAllIds(List.of("12345"))
                .build();
        doThrow(new RuntimeException())
                .when(productRepository).findByIdInAndIsDeletedFalse(anySet());
        //when
        productGrpcService.getProductItems(request, productItemsResponseStreamObserver);
        //then
        ArgumentCaptor<StatusRuntimeException> exceptionArgumentCaptor = ArgumentCaptor.forClass(StatusRuntimeException.class);
        verify(productItemsResponseStreamObserver).onError(exceptionArgumentCaptor.capture());
        assertEquals(exceptionArgumentCaptor.getValue().getStatus().getCode(), Status.Code.INTERNAL);
    }

    @Test
    void checkAndUpdateProductStock_success() {
        //given
        ProductProto.CheckProductStockRequest request = ProductProto.CheckProductStockRequest.newBuilder()
                .setOrderId("any-order-id")
                .addAllProductQuantity(List.of(ProductProto.ProductIdQuantity.newBuilder()
                                .setQuantity(1)
                                .setProductId("123456")
                        .build()))
                .build();
        given(productStockFacade.handleOrder(anyMap(), anyString())).willReturn(true);
        //when
        productGrpcService.checkAndUpdateProductStock(request, checkProductStockResponseStreamObserver);
        //then
        ArgumentCaptor<CheckProductStockResponse> responseArgumentCaptor = ArgumentCaptor.forClass(CheckProductStockResponse.class);
        verify(checkProductStockResponseStreamObserver, times(1)).onNext(responseArgumentCaptor.capture());
        CheckProductStockResponse response = responseArgumentCaptor.getValue();
        assertTrue(response.getIsSuccess());
    }

    @Test
    void checkAndUpdateProductStock_fail_INVALID_ARGUMENT() {
        //given
        ProductProto.CheckProductStockRequest request = ProductProto.CheckProductStockRequest.newBuilder()
                .setOrderId("any-order-id")
                .addAllProductQuantity(List.of(ProductProto.ProductIdQuantity.newBuilder()
                        .setQuantity(1)
                        .setProductId("123456")
                        .build()))
                .build();
        doThrow(new ProductException(Error.STOCK_SHORTAGE))
                .when(productStockFacade).handleOrder(anyMap(), anyString());
        //when
        productGrpcService.checkAndUpdateProductStock(request, checkProductStockResponseStreamObserver);
        //then
        ArgumentCaptor<StatusRuntimeException> exceptionArgumentCaptor = ArgumentCaptor.forClass(StatusRuntimeException.class);
        verify(checkProductStockResponseStreamObserver).onError(exceptionArgumentCaptor.capture());
        assertEquals(exceptionArgumentCaptor.getValue().getStatus().getCode(), INVALID_ARGUMENT);
    }

    @Test
    void checkAndUpdateProductStock_fail_INTERNAL() {
        //given
        ProductProto.CheckProductStockRequest request = ProductProto.CheckProductStockRequest.newBuilder()
                .setOrderId("any-order-id")
                .addAllProductQuantity(List.of(ProductProto.ProductIdQuantity.newBuilder()
                        .setQuantity(1)
                        .setProductId("123456")
                        .build()))
                .build();
        doThrow(new RuntimeException())
                .when(productStockFacade).handleOrder(anyMap(), anyString());
        //when
        productGrpcService.checkAndUpdateProductStock(request, checkProductStockResponseStreamObserver);
        //then
        ArgumentCaptor<StatusRuntimeException> exceptionArgumentCaptor = ArgumentCaptor.forClass(StatusRuntimeException.class);
        verify(checkProductStockResponseStreamObserver).onError(exceptionArgumentCaptor.capture());
        assertEquals(exceptionArgumentCaptor.getValue().getStatus().getCode(), Status.Code.INTERNAL);
    }

}