package cm.twentysix.brand.service;

import cm.twentysix.BrandProto;
import cm.twentysix.brand.domain.model.Brand;
import cm.twentysix.brand.domain.repository.BrandRepository;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static io.grpc.Status.Code.INTERNAL;
import static io.grpc.Status.Code.INVALID_ARGUMENT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BrandGrpcServiceTest {
    @Mock
    private BrandRepository brandRepository;
    @Mock
    private StreamObserver<BrandProto.BrandResponse> brandResponseStreamObserver;
    @Mock
    private StreamObserver<BrandProto.BrandDetailResponse> brandDetailResponseStreamObserver;
    @Mock
    private StreamObserver<BrandProto.BrandInfosResponse> brandInfosResponseStreamObserver;
    @InjectMocks
    private BrandGrpcService brandGrpcService;

    private static Brand mockBrandA;

    @BeforeAll
    static void setUp() {
        mockBrandA = mock(Brand.class);
        given(mockBrandA.getId()).willReturn(1L);
        given(mockBrandA.getName()).willReturn("아이캔더");
        given(mockBrandA.getLegalName()).willReturn("(주)아이캔더");
        given(mockBrandA.getThumbnail()).willReturn("/12345.jpg");
        given(mockBrandA.getIntroduction()).willReturn("강아지를 생각하는 브랜드입니다.");
        given(mockBrandA.getRegistrationNumber()).willReturn("123-12-12345");
        given(mockBrandA.getDeliveryFee()).willReturn(3000);
        given(mockBrandA.getFreeDeliveryInfimum()).willReturn(30000);
        given(mockBrandA.getUserId()).willReturn(1L);
    }

    @Test
    void getBrand_success() {
        //given
        BrandProto.BrandRequest request = BrandProto.BrandRequest.newBuilder()
                .setId(1L).build();
        given(brandRepository.findById(anyLong())).willReturn(Optional.of(mockBrandA));
        //when
        brandGrpcService.getBrand(request, brandResponseStreamObserver);
        //then
        ArgumentCaptor<BrandProto.BrandResponse> brandResponseCaptor = ArgumentCaptor.forClass(BrandProto.BrandResponse.class);
        verify(brandResponseStreamObserver, times(1)).onNext(brandResponseCaptor.capture());
        BrandProto.BrandResponse response = brandResponseCaptor.getValue();
        assertEquals(response.getName(), "아이캔더");
        assertEquals(response.getId(), 1L);
        assertEquals(response.getUserId(), 1L);
    }


    @Test
    void getBrand_fail_INVALID_ARGUMENT() {
        //given
        BrandProto.BrandRequest request = BrandProto.BrandRequest.newBuilder()
                .setId(1L).build();
        given(brandRepository.findById(anyLong())).willReturn(Optional.empty());
        //when
        brandGrpcService.getBrand(request, brandResponseStreamObserver);
        //then
        ArgumentCaptor<StatusRuntimeException> exceptionArgumentCaptor = ArgumentCaptor.forClass(StatusRuntimeException.class);
        verify(brandResponseStreamObserver).onError(exceptionArgumentCaptor.capture());
        assertEquals(exceptionArgumentCaptor.getValue().getStatus().getCode(), INVALID_ARGUMENT);
    }

    @Test
    void getBrand_fail_INTERNAL() {
        //given
        BrandProto.BrandRequest request = BrandProto.BrandRequest.newBuilder()
                .setId(1L).build();
        doThrow(new RuntimeException())
                .when(brandRepository).findById(anyLong());
        //when
        brandGrpcService.getBrand(request, brandResponseStreamObserver);
        //then
        ArgumentCaptor<StatusRuntimeException> exceptionArgumentCaptor = ArgumentCaptor.forClass(StatusRuntimeException.class);
        verify(brandResponseStreamObserver).onError(exceptionArgumentCaptor.capture());
        assertEquals(exceptionArgumentCaptor.getValue().getStatus().getCode(), Status.Code.INTERNAL);
    }

    @Test
    void getBrandDetail_success() {
        //given
        BrandProto.BrandDetailRequest request = BrandProto.BrandDetailRequest.newBuilder()
                .setId(1L).build();
        given(brandRepository.findById(anyLong())).willReturn(Optional.of(mockBrandA));
        //when
        brandGrpcService.getBrandDetail(request, brandDetailResponseStreamObserver);
        //then
        ArgumentCaptor<BrandProto.BrandDetailResponse> brandResponseCaptor = ArgumentCaptor.forClass(BrandProto.BrandDetailResponse.class);
        verify(brandDetailResponseStreamObserver, times(1)).onNext(brandResponseCaptor.capture());
        BrandProto.BrandDetailResponse response = brandResponseCaptor.getValue();
        assertEquals(response.getName(), "아이캔더");
        assertEquals(response.getLegalName(), "(주)아이캔더");
        assertEquals(response.getThumbnail(), "/12345.jpg");
        assertEquals(response.getId(), 1L);
        assertEquals(response.getUserId(), 1L);
        assertEquals(response.getIntroduction(), "강아지를 생각하는 브랜드입니다.");
        assertEquals(response.getRegistrationNumber(), "123-12-12345");
        assertEquals(response.getFreeDeliveryInfimum(), 30000);
        assertEquals(response.getUserId(), 1L);
    }

    @Test
    void getBrandDetail_fail_INVALID_ARGUMENT() {
        //given
        BrandProto.BrandDetailRequest request = BrandProto.BrandDetailRequest.newBuilder()
                .setId(1L).build();
        given(brandRepository.findById(anyLong())).willReturn(Optional.empty());
        //when
        brandGrpcService.getBrandDetail(request, brandDetailResponseStreamObserver);
        //then
        ArgumentCaptor<StatusRuntimeException> exceptionArgumentCaptor = ArgumentCaptor.forClass(StatusRuntimeException.class);
        verify(brandDetailResponseStreamObserver).onError(exceptionArgumentCaptor.capture());
        assertEquals(exceptionArgumentCaptor.getValue().getStatus().getCode(), INVALID_ARGUMENT);
    }

    @Test
    void getBrandDetail_fail_INTERNAL() {
        //given
        BrandProto.BrandDetailRequest request = BrandProto.BrandDetailRequest.newBuilder()
                .setId(1L).build();
        doThrow(new RuntimeException())
                .when(brandRepository).findById(anyLong());
        //when
        brandGrpcService.getBrandDetail(request, brandDetailResponseStreamObserver);
        //then
        ArgumentCaptor<StatusRuntimeException> exceptionArgumentCaptor = ArgumentCaptor.forClass(StatusRuntimeException.class);
        verify(brandDetailResponseStreamObserver).onError(exceptionArgumentCaptor.capture());
        assertEquals(exceptionArgumentCaptor.getValue().getStatus().getCode(), Status.Code.INTERNAL);
    }

    @Test
    void getBrandInfos_success() {
        //given
        BrandProto.BrandInfosRequest request = BrandProto.BrandInfosRequest.newBuilder()
                .addAllIds(List.of(1L)).build();
        given(brandRepository.findByIdIn(anyList())).willReturn(List.of(mockBrandA));
        //when
        brandGrpcService.getBrandInfos(request, brandInfosResponseStreamObserver);
        //then
        ArgumentCaptor<BrandProto.BrandInfosResponse> brandResponseCaptor = ArgumentCaptor.forClass(BrandProto.BrandInfosResponse.class);
        verify(brandInfosResponseStreamObserver, times(1)).onNext(brandResponseCaptor.capture());
        BrandProto.BrandInfosResponse response = brandResponseCaptor.getValue();
        assertEquals(response.getBrandsList().size(), 1);
        BrandProto.BrandInfo item = response.getBrandsList().getFirst();
        assertEquals(item.getName(), "아이캔더");
        assertEquals(item.getId(), 1L);
        assertEquals(item.getFreeDeliveryInfimum(), 30000);
        assertEquals(item.getDeliveryFee(), 3000);
    }

    @Test
    void getBrandInfos_fail_INVALID_ARGUMENT() {
        //given
        BrandProto.BrandInfosRequest request = BrandProto.BrandInfosRequest.newBuilder()
                .addAllIds(List.of(1L)).build();
        given(brandRepository.findByIdIn(anyList())).willReturn(List.of());
        //when
        brandGrpcService.getBrandInfos(request, brandInfosResponseStreamObserver);
        //then
        ArgumentCaptor<StatusRuntimeException> exceptionArgumentCaptor = ArgumentCaptor.forClass(StatusRuntimeException.class);
        verify(brandInfosResponseStreamObserver).onError(exceptionArgumentCaptor.capture());
        assertEquals(exceptionArgumentCaptor.getValue().getStatus().getCode(), INVALID_ARGUMENT);
    }

    @Test
    void getBrandInfos_fail_INTERNAL() {
        //given
        BrandProto.BrandInfosRequest request = BrandProto.BrandInfosRequest.newBuilder()
                .addAllIds(List.of(1L)).build();
        doThrow(new RuntimeException())
                .when(brandRepository).findByIdIn(anyList());
        //when
        brandGrpcService.getBrandInfos(request, brandInfosResponseStreamObserver);
        //then
        ArgumentCaptor<StatusRuntimeException> exceptionArgumentCaptor = ArgumentCaptor.forClass(StatusRuntimeException.class);
        verify(brandInfosResponseStreamObserver).onError(exceptionArgumentCaptor.capture());
        assertEquals(exceptionArgumentCaptor.getValue().getStatus().getCode(), INTERNAL);
    }


}