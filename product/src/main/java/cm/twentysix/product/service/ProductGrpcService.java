package cm.twentysix.product.service;

import cm.twentysix.ProductServiceGrpc;
import cm.twentysix.product.cache.ProductItemResponseGlobalCacheRepository;
import cm.twentysix.product.domain.model.Product;
import cm.twentysix.product.domain.repository.ProductRepository;
import cm.twentysix.product.exception.ProductException;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static cm.twentysix.ProductProto.*;
import static cm.twentysix.product.exception.Error.PRODUCT_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductGrpcService extends ProductServiceGrpc.ProductServiceImplBase {
    private final ProductRepository productRepository;
    private final ProductItemResponseGlobalCacheRepository productItemResponseGlobalCacheRepository;

    @Override
    public void getProductItem(ProductItemRequest request, StreamObserver<ProductItemResponse> responseObserver) {
        try {
            Product product = productRepository.findByIdAndIsDeletedFalse(request.getId())
                    .orElseThrow(() -> new ProductException(PRODUCT_NOT_FOUND));
            CompletableFuture.runAsync(() -> productItemResponseGlobalCacheRepository.put(product));
            ProductItemResponse response = ProductItemResponse.newBuilder()
                    .setId(product.getId())
                    .setBrandId(product.getProductBrand().getId())
                    .setName(product.getName())
                    .setThumbnail(product.getThumbnailPath())
                    .setDiscount(product.getDiscount())
                    .setPrice(product.getPrice())
                    .setDiscountedPrice(product.getDiscountedPrice())
                    .setBrandName(product.getProductBrand().getName())
                    .setQuantity(product.getQuantity())
                    .setOrderingOpensAt(product.getOrderingOpensAt().toString())
                    .build();
            responseObserver.onNext(response);
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

    @Override
    public void getProductItems(ProductItemsRequest request, StreamObserver<ProductItemsResponse> responseObserver) throws ProductException {
        try {
            List<Product> products = productRepository.findByIdInAndIsDeletedFalse(new HashSet<>(request.getIdsList()));
            if (products.isEmpty())
                throw new ProductException(PRODUCT_NOT_FOUND);
            CompletableFuture.runAsync(() -> productItemResponseGlobalCacheRepository.putAll(products));
            List<ProductItemResponse> productItemResponses = products.stream().map(product -> ProductItemResponse.newBuilder()
                    .setId(product.getId())
                    .setBrandId(product.getProductBrand().getId())
                    .setName(product.getName())
                    .setThumbnail(product.getThumbnailPath())
                    .setDiscount(product.getDiscount())
                    .setPrice(product.getPrice())
                    .setDiscountedPrice(product.getDiscountedPrice())
                    .setBrandName(product.getProductBrand().getName())
                    .setQuantity(product.getQuantity())
                    .setOrderingOpensAt(product.getOrderingOpensAt().toString())
                    .build()).toList();
            ProductItemsResponse response = ProductItemsResponse.newBuilder().addAllProducts(productItemResponses).build();
            responseObserver.onNext(response);
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
