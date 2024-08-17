package cm.twentysix.order.config;

import io.grpc.Server;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class GrpcServerRunner implements ApplicationRunner {
    private final Server grpcServer;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        grpcServer.start();
        log.info("gRPC server started on port " + grpcServer.getPort());
        Runtime.getRuntime().addShutdownHook(new Thread(() ->
        {
            try {
                grpcServer.shutdown().awaitTermination();
            } catch (InterruptedException e) {
                log.error(e.getMessage());
            }
        }));
    }
}
