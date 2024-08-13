package cm.twentysix.product.client;

import cm.twentysix.BrandProto;
import cm.twentysix.BrandServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

@Component
public class BrandGrpcClient {
    @GrpcClient("brand")
    private BrandServiceGrpc.BrandServiceBlockingStub brandServiceBlockingStub;

    public BrandProto.BrandResponse getBrandInfo(Long brandId) {
        BrandProto.BrandRequest request = BrandProto.BrandRequest.newBuilder()
                .setId(brandId).build();
        return brandServiceBlockingStub.getBrand(request);
    }
}
