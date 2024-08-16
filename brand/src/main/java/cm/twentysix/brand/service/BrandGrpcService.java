package cm.twentysix.brand.service;

import cm.twentysix.BrandProto;
import cm.twentysix.BrandServiceGrpc;
import cm.twentysix.brand.domain.model.Brand;
import cm.twentysix.brand.domain.repository.BrandRepository;
import cm.twentysix.brand.exception.BrandException;
import cm.twentysix.brand.exception.Error;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BrandGrpcService extends BrandServiceGrpc.BrandServiceImplBase {
    private final BrandRepository brandRepository;

    @Override
    public void getBrand(BrandProto.BrandRequest request, StreamObserver<BrandProto.BrandResponse> responseObserver) {
        try {
            Brand brand = brandRepository.findById(request.getId())
                    .orElseThrow(() -> new BrandException(Error.BRAND_NOT_FOUND));
            BrandProto.BrandResponse response = BrandProto.BrandResponse.newBuilder()
                    .setId(brand.getId())
                    .setUserId(brand.getUserId())
                    .setName(brand.getName())
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (BrandException e) {
            responseObserver.onError(
                    Status.INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException()
            );
        } catch (Exception e) {
            responseObserver.onError(
                    Status.INTERNAL.withDescription(HttpStatus.INTERNAL_SERVER_ERROR.name()).asRuntimeException()
            );
        }
    }

    @Override
    public void getBrandDetail(BrandProto.BrandDetailRequest request, StreamObserver<BrandProto.BrandDetailResponse> responseObserver) {
        try {
            Brand brand = brandRepository.findById(request.getId())
                    .orElseThrow(() -> new BrandException(Error.BRAND_NOT_FOUND));
            BrandProto.BrandDetailResponse response = BrandProto.BrandDetailResponse.newBuilder()
                    .setId(brand.getId())
                    .setName(brand.getName())
                    .setLegalName(brand.getLegalName())
                    .setThumbnail(brand.getThumbnail())
                    .setIntroduction(brand.getIntroduction())
                    .setRegistrationNumber(brand.getRegistrationNumber())
                    .setFreeDeliveryInfimum(brand.getFreeDeliveryInfimum())
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (BrandException e) {
            responseObserver.onError(
                    Status.INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException()
            );
        } catch (Exception e) {
            responseObserver.onError(
                    Status.INTERNAL.withDescription(HttpStatus.INTERNAL_SERVER_ERROR.name()).asRuntimeException()
            );
        }
    }

    @Override
    public void getBrandInfos(BrandProto.BrandInfosRequest request, StreamObserver<BrandProto.BrandInfosResponse> responseObserver) {
        try {
            List<BrandProto.BrandInfo> brandInfos = brandRepository.findByIdIn(request.getIdsList())
                    .stream().map(brand -> BrandProto.BrandInfo.newBuilder()
                            .setId(brand.getId())
                            .setName(brand.getName())
                            .setFreeDeliveryInfimum(brand.getFreeDeliveryInfimum())
                            .setDeliveryFee(brand.getDeliveryFee())
                            .build()
                    ).toList();
            responseObserver.onNext(BrandProto.BrandInfosResponse.newBuilder().addAllBrands(brandInfos).build());
            responseObserver.onCompleted();
        } catch (BrandException e) {
            responseObserver.onError(
                    Status.INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException()
            );
        } catch (Exception e) {
            responseObserver.onError(
                    Status.INTERNAL.withDescription(HttpStatus.INTERNAL_SERVER_ERROR.name()).asRuntimeException()
            );
        }
    }
}
