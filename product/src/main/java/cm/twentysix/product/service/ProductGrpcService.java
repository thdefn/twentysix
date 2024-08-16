package cm.twentysix.product.service;

import cm.twentysix.ProductServiceGrpc;
import cm.twentysix.product.domain.model.Product;
import cm.twentysix.product.domain.repository.ProductRepository;
import cm.twentysix.product.exception.Error;
import cm.twentysix.product.exception.ProductException;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

import static cm.twentysix.ProductProto.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductGrpcService extends ProductServiceGrpc.ProductServiceImplBase {
    private final ProductRepository productRepository;

    @Override
    public void getProductItem(ProductItemRequest request, StreamObserver<ProductItemResponse> responseObserver) {
        try {
            Product product = productRepository.findByIdAndIsDeletedFalse(request.getId())
                    .orElseThrow(() -> new ProductException(Error.PRODUCT_NOT_FOUND));
            ProductItemResponse response = ProductItemResponse.newBuilder()
                    .setId(product.getId())
                    .setBrandId(product.getProductBrand().getId())
                    .setName(product.getName())
                    .setThumbnail(product.getThumbnailPath())
                    .setDiscount(product.getDiscount())
                    .setPrice(product.getPrice())
                    .setBrandName(product.getProductBrand().getName())
                    .build();
            responseObserver.onNext(response);
            log.error(response+"");
            responseObserver.onCompleted();

        } catch (ProductException e) {
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
