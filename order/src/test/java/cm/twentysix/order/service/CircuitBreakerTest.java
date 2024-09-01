package cm.twentysix.order.service;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@DisabledIfEnvironmentVariable(named = "SPRING_PROFILES_ACTIVE", matches = "ci")
class CircuitBreakerTest {
    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    private CircuitBreaker circuitBreaker;

    @BeforeEach
    public void setUp() {
        circuitBreaker = circuitBreakerRegistry.circuitBreaker("testCircuitBreaker");
    }

    @Test
    public void testCircuitBreakerWithCustomConfig() throws InterruptedException {
        System.out.println("CircuitBreaker Config: " + circuitBreaker.getCircuitBreakerConfig());

        Supplier<String> failingSupplier = () -> {
            throw new RuntimeException("Service failed");
        };

        for (int i = 0; i < 50; i++) {
            assertThrows(RuntimeException.class, () -> circuitBreaker.executeSupplier(failingSupplier));
        }

        assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreaker.State.OPEN);

        Thread.sleep(30000);
        assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreaker.State.HALF_OPEN);

        for (int i = 0; i < 3; i++) {
            try {
                circuitBreaker.executeSupplier(() -> "Success");
            } catch (RuntimeException e) {
            }
        }

        Thread.sleep(1000);

        assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreaker.State.CLOSED);
    }


}