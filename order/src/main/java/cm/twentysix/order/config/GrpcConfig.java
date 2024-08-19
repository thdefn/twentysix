package cm.twentysix.order.config;

import cm.twentysix.order.service.OrderGrpcService;
import com.google.common.collect.Lists;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter;

@Configuration
public class GrpcConfig {
    @Value("${grpc.server.port}")
    private int port;

    @Bean
    public Server grpcServer(OrderGrpcService orderGrpcService) {
        return ServerBuilder.forPort(port)
                .addService(orderGrpcService)
                .build();
    }

    @Bean
    public ProtobufHttpMessageConverter protobufHttpMessageConverter() {
        ProtobufHttpMessageConverter protobufHttpMessageConverter = new ProtobufHttpMessageConverter();
        protobufHttpMessageConverter.setSupportedMediaTypes(Lists.newArrayList(MediaType.APPLICATION_JSON,
                MediaType.parseMediaType(MediaType.TEXT_PLAIN_VALUE + ";charset=ISO-8859-1")));
        return protobufHttpMessageConverter;
    }
}
