package cm.twentysix.payment.service;

import cm.twentysix.payment.domain.model.Payment;
import cm.twentysix.payment.domain.model.PaymentStatus;
import cm.twentysix.payment.domain.repository.PaymentRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@DisabledIfEnvironmentVariable(named = "SPRING_PROFILES_ACTIVE", matches = "ci")
public class PaymentOptimisticLockTest {
    @Autowired
    private PaymentRepository paymentRepository;

    private Payment savedA;

    @BeforeEach
    void setUp() {
        savedA = Payment.builder()
                .status(PaymentStatus.PENDING)
                .orderId("order-id-123456")
                .build();
        paymentRepository.save(savedA);
    }

    @AfterEach
    void tearDown() {
        paymentRepository.deleteById(savedA.getId());
    }

    @Test
    void optimisticLock_success() throws InterruptedException {
        //given
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(2);
        AtomicReference<OptimisticLockingFailureException> exception = new AtomicReference<>();
        executor.submit(() -> {
            try {
                Payment payment = paymentRepository.findById(savedA.getId())
                        .orElseThrow();
                payment.trying();
                paymentRepository.save(payment);
            } catch (OptimisticLockingFailureException e) {
                System.out.println("낙관적 락 발생");
                exception.set(e);
            } finally {
                latch.countDown();
            }
        });
        executor.submit(() -> {
            try {
                Payment payment = paymentRepository.findById(savedA.getId())
                        .orElseThrow();
                payment.trying();
                paymentRepository.save(payment);
            } catch (OptimisticLockingFailureException e) {
                System.out.println("낙관적 락 발생");
                exception.set(e);
            } finally {
                latch.countDown();
            }
        });

        latch.await();
        Payment payment = paymentRepository.findById(savedA.getId())
                .orElseThrow();
        assertEquals(payment.getVersion(), 1);
        assertEquals(payment.getStatus(), PaymentStatus.TRYING);
        assertNotNull(exception.get());

    }
}
